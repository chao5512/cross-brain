#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from pyspark.sql.types import *

import logging
from logging.config import fileConfig

import threading

import requests

from hdfs.client import Client

app = Flask(__name__)

fileConfig('logging.conf')
logger=logging.getLogger('pipline')

#CORS(app, supports_credentials=True)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

# the pipeline of Spark
@app.route("/execute",methods=['POST'])
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
    client = Client("http://172.16.31.232:50070")
    logger.info('start threading')
    spark = args[0]
    pipe = args[1]
    data = kwaggs
    originalPreProcess = {}
    originalStages = {}

    for task in data['tasks']:
        if data['tasks'][task]['type'] == 1:
            originalStages[task]= data[task]
        elif data['tasks'][task]['type'] == 4:
            originalPreProcess[task]= data[task]

    #Step 2 加载数据
    try:
        with client.write("/datasource.log",
                          overwrite=False,append=True,encoding='utf-8') as writer:
            writer.write("开始加载数据!\n")
        with client.write("/datasource.log",
                          overwrite=False,append=True,encoding='utf-8') as writer:
            writer.write("数据加载成功!\n")
        pipe.loadDataSetFromTable()
        #res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    except:
        client.write("/datasource.log",data="数据加载失败!",append=True,encoding='utf-8')
        #res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    #Step 3 数据预处理
    try:
        client.write("/process.log",data="开始预处理数据!",append=True)
        pipe.buildProcess(originalPreProcess)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    except:
        client.write("/process.log",data="数据预处理失败!",append=True)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

    #Step 4 切分数据
    try:
        isSplitSample = data['isSplitSample']
        if  isSplitSample['fault']:
            client.write("/splitdata.log",data="开始切分数据!",append=True)
            trainRatio = isSplitSample['trainRatio']
            pipe.split([trainRatio, 1-trainRatio])
            res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    except:
        client.write("/pipeline.log",data="数据切分失败!",append=True)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

    #Step 5 构造模型,测试集训练模型,验证集验证模型
    try:
        client.write("/pipeline.log",data="开始创建机器学习流程!",append=True)
        client.write("/pipeline.log",data="开始训练数据!",append=True)
        model = pipe.buildPipeline(originalStages)
        client.write("/pipeline.log",data="开始验证数据!",append=True)
        prediction = pipe.validator(model)
        client.write("/pipeline.log",data="任务运行成功!",append=True)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    except:
        client.write("/pipeline.log",data="机器学习流程运行失败!",append=True)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

    #Step 6 评估模型
    try:
        client.write("/evaluator.log",data="开始运行评估器!",append=True)
        accuracy = pipe.evaluator(data['evaluator']['method'], prediction, "label")
        logger.info("Test set accuracy = " + str(accuracy))
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    except:
        client.write("/evaluator.log",data="评估器运行失败!",append=True)
        res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

    pipe.stop()

    #任务运行成功,更新任务信息
    res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)
