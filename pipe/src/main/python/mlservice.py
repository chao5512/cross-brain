#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from pyspark.sql.types import *

import logging
from logging.config import fileConfig

import threading

app = Flask(__name__)

fileConfig('logging.conf')
logger=logging.getLogger('root')

#CORS(app, supports_credentials=True)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

# the pipeline of LogisticRegression
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

    print("Stages")
    print(originalStages)
    print("process")
    print(originalPreProcess)

    #Step 2 加载数据
    try:
        pipe.loadDataSetFromTable()
    except:
        print("load fail")
    else:
        print("load success")

    #Step 3 数据预处理
    pipe.buildProcess(originalPreProcess)

    #Step 4 切分数据
    try:
        isSplitSample = data['isSplitSample']
        if  isSplitSample['fault']:
            trainRatio = isSplitSample['trainRatio']
            pipe.split([trainRatio, 1-trainRatio])
    except:
        print("split fail")
    else:
        print("split success")

    #Step 5 构造模型,测试集训练模型,验证集验证模型
    model = pipe.buildPipeline(originalStages)
    prediction = pipe.validator(model)

    #Step 6 评估模型
    accuracy = pipe.evaluator(data['evaluator'], prediction, "label")
    logger.info("Test set accuracy = " + str(accuracy))

    pipe.stop()

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)
