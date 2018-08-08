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
conf = configparser.ConfigParser()
conf.read(sys.path[0]+"/conf/conf.ini")
os.environ["PYSPARK_PYTHON"]=conf.get('config','python_home')
os.environ['SPARK_CONF_DIR'] = os.getcwd()+'../resources'
os.environ['HADOOP_CONF_DIR'] = os.getcwd()+'../resources'

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
    data = json.loads(request.get_data())
    # step 1 create sparksession and dataframe
    appName = data['appName']
    jobId = data['jobId']
    pipe = MLPipeline(appName)
    try:
        spark = pipe.create()
        t =threading.Thread(target=submit,args=(spark,pipe),kwargs=(data))
        t.start()
    except:
        result = {'status': 2,'msg':'任务提交失败!','jobId':jobId,'applicationId':""}
    else:
        result = {'status': 1,'msg':'任务提交成功!','jobId':jobId,'applicationId':spark.sparkContext.applicationId}
    print(result)
    return Response(json.dumps(result), mimetype='application/json')

def submit(*args,**kwaggs):
    logger.info('start threading')
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
    rootPath = conf.get("Job","jobHdfsPath")+"/"+data["userId"]+"/"+data["modelId"]+"/"+data["jobId"]
    for task in data['tasks']:
        if data['tasks'][task]['type'] == 3:
            originalStages[task]= data[task]
            originalTask.append(data['tasks'][task]['taskId'])
        elif data['tasks'][task]['type'] == 2:
            originalPreProcess[task]= data[task]
            originalPreTask.append(data['tasks'][task]['taskId'])
        elif data['tasks'][task]['type'] == 4:
            originalEvaluator.append(data['tasks'][task]['taskId'])
        elif data['tasks'][task]['type'] == 1:# 数据切割
            originalSplitData.append(data['tasks'][task]['taskId'])
        elif data['tasks'][task]['type'] == 0:# 数据源
            originalDatasource.append(data['tasks'][task]['taskId'])

    #Step 2 加载数据
    try:
        file = rootPath+"/logs/datasource.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始加载数据!\n",True)
        pipe.loadDataSetFromTable(data['datasource']['tablename'])
        HDFSUtil.append(file,"数据加载成功!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalDatasource,'status':1})
    except:
        HDFSUtil.append(file,"数据加载失败!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalDatasource,'status':-1})
    #Step 3 数据预处理
    try:
        file = rootPath+"/logs/process.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始预处理数据!\n",True)
        pipe.buildProcess(originalPreProcess)
        HDFSUtil.append(file,"数据预处理成功!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalPreTask,'status':1})
    except:
        HDFSUtil.append(file,"数据预处理失败!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalPreTask,'status':-1})

    #Step 4 切分数据
    try:
        file = rootPath+"/logs/splitdata.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        isSplitSample = data['isSplitSample']
        if  isSplitSample['fault']:
            HDFSUtil.append(file,"开始切分数据!\n",True)
            trainRatio = isSplitSample['trainRatio']
            pipe.split([trainRatio, 1-trainRatio])
            res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalSplitData,'status':1})
    except:
        HDFSUtil.append(file,"数据切分失败!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalSplitData,'status':-1})

    #Step 5 构造模型,测试集训练模型,验证集验证模型
    try:
        file = rootPath+"/logs/pipeline.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始创建机器学习流程!\n开始训练数据!\n",True)
        model = pipe.buildPipeline(originalStages)
        HDFSUtil.append(file,"开始验证数据!\n",True)
        prediction = pipe.validator(model)
        HDFSUtil.append(file,"任务运行成功!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalTask,'status':1})
    except:
        HDFSUtil.append(file,"pipeline运行失败!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalTask,'status':-1})

    #Step 6 评估模型
    try:
        file = rootPath+"/logs/evaluator.log"
        HDFSUtil.append(file,"",False) #创建日志文件
        HDFSUtil.append(file,"开始运行评估器!\n",True)
        accuracy = pipe.evaluator(data['evaluator']['method'], prediction, "label")
        logger.info("Test set accuracy = " + str(accuracy))
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalEvaluator,'status':1})
    except:
        HDFSUtil.append(file,"评估器运行失败!\n",True)
        res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalEvaluator,'status':-1})

    pipe.stop()

    #任务运行成功,更新任务信息
    res = requests.post(conf.get("Job","jobService")+"Job/updatejob",params={'jobId':data['JobId'],'taskId':originalDatasource+originalPreTask+originalSplitData+originalTask+originalEvaluator,'status':1})

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)
