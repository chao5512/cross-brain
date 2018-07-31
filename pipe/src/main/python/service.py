#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.feature import HashingTF, Tokenizer
from pyspark.ml import Pipeline
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from flask_cors import *

import logging
import time
import os.path

# 创建logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)  # Log等级总开关
# 创建handler，用于写入日志文件
rq = time.strftime('%Y%m%d%H%M', time.localtime(time.time()))
log_path = os.path.dirname(os.getcwd()) + '/Logs/'
log_name = log_path + rq + '.log'
logfile = log_name
fh = logging.FileHandler(logfile, mode='w')
fh.setLevel(logging.DEBUG)  # 输出到file的log等级的开关
# 定义handler的输出格式
formatter = logging.Formatter("%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s")
fh.setFormatter(formatter)
# 将logger添加到handler里面
logger.addHandler(fh)

app = Flask(__name__)
#CORS(app, supports_credentials=True)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')
@app.route("/submit",methods=['POST'])
def submit():
    data = json.loads(request.get_data())
    appName = data['appName']
    pipe = MLPipeline(appName)
    pipe.create()
    pipe.buildStages(data['originalStages'])
    result = {'appName': appName}
    return Response(json.dumps(result), mimetype='application/json')

# the pipeline of LogisticRegression
@app.route("/LRDemo",methods=['POST'])
def loadDataSet():
    # step1 create sparksession and dataframe
    print(request.get_data())
    data = json.loads(request.get_data())
    logger.info(data)
    appName = data['appName']
    pipe = MLPipeline(appName)
    spark = pipe.create()
    #数据元路径
    datasource = json.loads(data['datasource'])
    print(datasource)
    #filepath = datasource['filepath']
    filepath = "hdfs://172.16.31.231:9000/data"

    print(filepath)
    logger.info(datasource)
    filepath = datasource['filepath']
    logger.info(filepath)

    textRDD = spark.sparkContext.textFile(filepath)
    lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])
    schema = StructType([
        StructField("label", StringType(), True),
        StructField("content", StringType(), True)])
    textDF = spark.createDataFrame(lastRDD, schema)
    lastDF = textDF.withColumn("label", textDF["label"].cast("Double"))
    pipe.loadDataSet(lastDF)

    # step2 切分数据
    isSplitSample = json.loads(data['isSplitSample'])
    if  isSplitSample['fault']:
        trainRatio = isSplitSample['trainRatio']
        pipe.split([trainRatio, 1-trainRatio])

    # step3 构造模型
    b={}
    b['Tokenizer'] = json.loads(data['Tokenizer'])
    b['HashingTF'] = json.loads(data['HashingTF'])
    b['LogisticRegression'] = json.loads(data['LogisticRegression'])

    logger.info(b)
    model = pipe.buildPipeline(b)
    #model = pipe.buildPipeline(data['originalStages'])

    # step4 模型作用于测试集
    prediction = pipe.validator(model)

    # step5 验证准确性
    accuracy = pipe.evaluator(data['evaluator'], prediction, "label")

    # step6 打印模型准确率
    logger.info("Test set accuracy = " + str(accuracy))

    pipe.stop()
    result = {'appName': appName, 'accuracy': accuracy}
    return Response(json.dumps(result), mimetype='application/json')
if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)