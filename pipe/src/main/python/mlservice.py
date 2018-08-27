#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from pyspark.ml import PipelineModel
from util.HDFSUtil import HDFSUtil
from util.hiveutil  import HiveUtil
from pyspark.sql.types import *
import sys
import logging
from logging.config import fileConfig
import threading
import requests
import configparser
import os
import time
print(sys.path[0])
conf = configparser.ConfigParser()
conf.read(sys.path[0]+"/conf/conf.ini")
os.environ["PYSPARK_PYTHON"]=conf.get('config','python_home')

#os.environ['SPARK_CONF_DIR'] = sys.path[0]+'../resources'
#os.environ['HADOOP_CONF_DIR'] = sys.path[0]+'../resources'

app = Flask(__name__)

fileConfig(sys.path[0]+'/conf/logging.conf')
logger=logging.getLogger('pipline')

#CORS(app, supports_credentials=True)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

# the pipeline of Spark
@app.route("/machinelearning/execute",methods=['POST'])
def execute():
    logger.info("reqdata")
    logger.info(request.get_data())
    data = json.loads(request.get_data())
    # step 1 create sparksession and dataframe
    appName = data['appName']
    jobId = data['jobId']
    pipe = MLPipeline(appName)
    try:
        spark = pipe.create()
        t =threading.Thread(target=submit,args=(spark,pipe),kwargs=(data))
        t.start()
    except BaseException as e:
        logger.error("job error!")
        logger.error(e.args)
        result = {'status': 2,'msg':'任务提交失败!','jobId':jobId,'applicationId':""}
    else:
        result = {'status': 1,'msg':'任务提交成功!','jobId':jobId,'applicationId':spark.sparkContext.applicationId}
    logger.info(result)
    return Response(json.dumps(result), mimetype='application/json')

def submit(*args,**kwaggs):
    logger.info('start threading')
    req_job_address = conf.get("Job","jobService")+"Job/updatejob"
    req_task_address = conf.get("Job","jobService")+"task/updateTask"
    spark = args[0]
    pipe = args[1]
    data = kwaggs
    # 保存数据变量
    originalDatasource = []
    originalSplitData = []
    originalEvaluatorTask = []
    originalTask = []
    originalPreTask = []
    originalPreProcess = {}
    originalStages = {}
    originalEvaluator = {}

    rootPath = conf.get("Job","jobHdfsPath")+data["userId"]+"/"+data["modelId"]+"/"+data["jobId"]
    try:
        for task in data['tasks']:
            print(data['tasks'][task])
            t = data['tasks'][task]
            if t['type'] == 3:
                logger.info(data[task])
                originalStages[task]= data[task]
                originalTask.append(t['taskId'])
            elif t['type'] == 2:
                originalPreProcess[task]= data[task]
                originalPreTask.append(t['taskId'])
            elif t['type'] == 4:
                originalEvaluator[task] = data[task]
                originalEvaluatorTask.append(t['taskId'])
            elif t['type'] == 1:# 数据切割
                originalSplitData.append(t['taskId'])
            elif t['type'] == 0:# 数据源
                originalDatasource.append(t['taskId'])
    except BaseException as e:
        logging.exception(e)
    logger.info('load data')
    logger.info('update address')
    logger.info(req_job_address)
    logger.info(req_task_address)
    logger.info('come on')
    logger.info(originalStages)
    logger.info(originalPreProcess)
    #Step 2 加载数据
    try:
        file = rootPath+"/logs/datasource.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始加载数据!\n",True)
        pipe.loadDataSetFromTable(data['datasource']['tablename'])
        HDFSUtil.append(file,"数据加载成功!\n",True)
        logger.info("update status")
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalDatasource,'status':1})
        logger.info("update finish")
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(file,"数据加载失败!\n",True)

        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalDatasource,'status':-1})

    logger.info('process')
    #Step 3 数据预处理
    try:
        file = rootPath+"/logs/process.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始预处理数据!\n",True)
        pipe.buildProcess(originalPreProcess)
        HDFSUtil.append(file,"数据预处理成功!\n",True)
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalPreTask,'status':1})
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(file,"数据预处理失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalPreTask,'status':-1})

    logger.info('split data')
    #Step 4 切分数据
    try:
        file = rootPath+"/logs/splitdata.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        isSplitSample = data['isSplitSample']
        if  isSplitSample['fault']:
            HDFSUtil.append(file,"开始切分数据!\n",True)
            trainRatio = isSplitSample['trainRatio']
            pipe.split([trainRatio, 1-trainRatio])
            res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalSplitData,'status':1})
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(file,"数据切分失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalSplitData,'status':-1})

    logger.info('train model')
    #Step 5 构造模型,测试集训练模型,验证集验证模型
    try:
        file = rootPath+"/logs/pipeline.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始创建机器学习流程!\n开始训练数据!\n",True)
        modelPath = conf.get("cluster", "hadoopFS") + conf.get("Job","jobHdfsPath") + \
                    data["userId"] + "/" + data["modelId"] + "/" + data["jobId"] + "/model"
        pipe.modelPath = modelPath
        model = pipe.buildPipeline(originalStages)
        HDFSUtil.append(file,"开始验证数据!\n",True)
        prediction = pipe.validator(model)
        HDFSUtil.append(file,"任务运行成功!\n",True)
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalTask,'status':1})
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(file,"pipeline运行失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalTask,'status':-1})

    logger.info('evaluator')
    #Step 6 评估模型
    try:
        file = rootPath+"/logs/evaluator.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        evaluatorResult = rootPath+"/evaluator/evaluator.json"
        HDFSUtil.append(evaluatorResult,"",False) #创建日志文件
        HDFSUtil.append(file,"开始运行评估器!\n",True)
        # lable
        accuracy = pipe.evaluator(originalEvaluator, prediction, "label")
        logger.info("Test evaluatorResult = " + str(accuracy))
        HDFSUtil.append(evaluatorResult,str(accuracy),True)
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalEvaluator,'status':1})
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(file,"评估器运行失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalEvaluator,'status':-1})

    pipe.stop()


'''
url：http://47.105.127.125:3001/machinelearning/predict
请求方式：post
请求参数示例：{"messagedata":"\"test content1 \"","appName":"hanweitest","jobId":"JOBID00066","modelId":"MDL00061","userId":"2288","datasource":{"tablename":"sougou"},"TypeTransfer":{"castType":"Double","outputCol":"label","inputCol":"label"},"isSplitSample":{"trainRatio":0.7,"fault":1},"Tokenizer":{"outputCol":"words","inputCol":"content"},"HashingTF":{"outputCol":"features","inputCol":"words"},"LogisticRegression":{"maxIter":10,"regParam":0.001},"MulticlassClassificationEvaluator":{"method":"MulticlassClassificationEvaluator"},"tasks":{"datasource":{"type":0,"taskId":"TASKID00474"},"TypeTransfer":{"type":2,"taskId":"TASKID00475"},"isSplitSample":{"type":1,"taskId":"TASKID00476"},"Tokenizer":{"type":3,"taskId":"TASKID00477"},"HashingTF":{"type":3,"taskId":"TASKID00478"},"LogisticRegression":{"type":3,"taskId":"TASKID00479"},"MulticlassClassificationEvaluator":{"type":4,"taskId":"TASKID00480"}}}
'''
@app.route("/machinelearning/predict",methods=['POST'])
def predict():
    logger.info("reqdata")
    logger.info(request.get_data())
    data = json.loads(request.get_data())
    # step 1 create sparksession and dataframe
    appName = data['appName']
    jobId = data['jobId']
    pipe = MLPipeline(appName)
    try:
        spark = pipe.create()
        t =threading.Thread(target=predict_submit,args=(spark,pipe),kwargs=(data))
        t.start()
    except BaseException as e:
        logger.error("job error!")
        logger.error(e.args)
        result = {'status': 2,'msg':'调用模型任务提交失败!','jobId':jobId,'applicationId':""}
    else:
        result = {'status': 1,'msg':'调用模型任务提交成功!','jobId':jobId,'applicationId':spark.sparkContext.applicationId}
    logger.info(result)
    return Response(json.dumps(result), mimetype='application/json')


def predict_submit(*args, **kwaggs):
    logger.info('start predict threading')
    spark = args[0]
    pipe = args[1]
    data = kwaggs
    # 保存数据变量
    originalPreProcess = {}

    try:
        for task in data['tasks']:
            t = data['tasks'][task]
            if t['type'] == 2 and "label" not in data[task].values():
                originalPreProcess[task]= data[task]
    except BaseException as e:
        logging.exception(e)
    logger.info(originalPreProcess)

    #Step 2 加载原始表信息 构建同样表结构
    try:
        train_table_name = data['datasource']['tablename']
        predict_table_name = train_table_name + "_" + data["jobId"] + "_predict"
        predict_data_path = conf.get("Job", "jobHdfsPath") + data["userId"] + \
                            "/" + data["modelId"] + "/" + data["jobId"]
        if (data.get("messagedata") is None or data.get("messagedata") == ""):
            predict_data_path += "/data"
        else :
            predict_data_path += "/messagedata"
        spark.sql("create table if not exists %s like  %s LOCATION '%s'" % (
            predict_table_name, train_table_name, predict_data_path))
        pipe.loadDataSetFromTable(predict_table_name)
        # 创建的新表不应该有label列，这里将其移动到最后一列（没有直接删除的方法）
        lastColumn = pipe.dataFrame.schema.fields[-1].name
        if(lastColumn != "label"):
            HiveUtil.queryBySql("alter table %s  change label label STRING after %s" % ( predict_table_name, lastColumn))
            pipe.loadDataSetFromTable(predict_table_name)
        if (not (data.get("messagedata") is None or data.get("messagedata") == "")):
            logger.info("准备插入临时表，数据为:%s" % (data.get("messagedata")))
            messagedata = data.get("messagedata")
            spark.sql("insert overwrite table  %s VALUES (%s)" % (predict_table_name , messagedata+',""'))
            logger.info("数据插入完毕")
    except BaseException as e:
        logger.exception(e)
    logger.info('predict process')
    # Step 3 数据预处理
    try:
        pipe.buildProcess(originalPreProcess)
    except BaseException as e:
        logger.exception(e)

    # Step 4 预测
    logger.info('load  model')
    try:
        root_path = conf.get("cluster", "hadoopFS") + conf.get("Job","jobHdfsPath") + \
                     data["userId"] + "/" + data["modelId"] + "/" + data["jobId"]
        model_path = root_path + "/model"
        model = PipelineModel.load(model_path)
        prediction = model.transform(pipe.dataFrame)
    except BaseException as e:
        logger.exception(e)

    logger.info('save  result')
    try:
        #Step 5 保存结果
        selection = ",".join('"'+column.name+'"' for column in prediction.schema.fields if column.name != "label")
        selection += ',"prediction"'
        prediction.write.json(path=root_path + "/result/",mode="overwrite")
        spark.sql("drop table if exists  %s " % (predict_table_name))
    except BaseException as e:
        logger.exception(e)
    pipe.stop()

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)
