#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from util.HDFSUtil import HDFSUtil
from pyspark.sql.types import *
import sys
import logging
from logging.config import fileConfig
import threading
import requests
import configparser
import os
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
    originalEvaluator = []
    originalPreProcess = {}
    originalPreTask = []
    originalStages = {}
    originalTask = []
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
                originalEvaluator.append(t['taskId'])
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
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalDatasource,'status':1})
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
    except:
        HDFSUtil.append(file,"数据预处理失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalPreTask,'status':-1})

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
    except:
        HDFSUtil.append(file,"数据切分失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalSplitData,'status':-1})

    #Step 5 构造模型,测试集训练模型,验证集验证模型
    try:
        file = rootPath+"/logs/pipeline.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始创建机器学习流程!\n开始训练数据!\n",True)
        model = pipe.buildPipeline(originalStages)
        HDFSUtil.append(file,"开始验证数据!\n",True)
        prediction = pipe.validator(model)
        HDFSUtil.append(file,"任务运行成功!\n",True)
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalTask,'status':1})
    except:
        HDFSUtil.append(file,"pipeline运行失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalTask,'status':-1})

    #Step 6 评估模型
    try:
        file = rootPath+"/logs/evaluator.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始运行评估器!\n",True)
        accuracy = pipe.evaluator(data['evaluator']['method'], prediction, "label")
        logger.info("Test set accuracy = " + str(accuracy))
        res = requests.post(req_task_address,params={'jobId':data['jobId'],'taskId':originalEvaluator,'status':1})
    except:
        HDFSUtil.append(file,"评估器运行失败!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':originalEvaluator,'status':-1})

    pipe.stop()

    #任务运行成功,更新JOB信息
    res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':"",'status':1})

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)
