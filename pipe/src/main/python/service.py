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

app = Flask(__name__)
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
    appName = data['appName']
    pipe = MLPipeline(appName)
    spark = pipe.create()
    textRDD = spark.sparkContext.textFile(data['filePath'])
    lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])
    schema = StructType([
        StructField("label", StringType(), True),
        StructField("content", StringType(), True)])
    textDF = spark.createDataFrame(lastRDD, schema)
    lastDF = textDF.withColumn("label", textDF["label"].cast("Double"))
    pipe.loadDataSet(lastDF)

    # step2 切分数据
    if data['isSplitSample'] :
        trainRatio = data['trainRatio']
        pipe.split([trainRatio, 1-trainRatio])

    # step3 构造模型
    a = data['originalStages']
    b={}
    b['Tokenizer'] = a['Tokenizer']
    b['HashingTF'] = a['HashingTF']
    b['LogisticRegression'] = a['LogisticRegression']

    print(b)
    model = pipe.buildPipeline(b)
    #model = pipe.buildPipeline(data['originalStages'])

    # step4 模型作用于测试集
    prediction = pipe.validator(model)

    # step5 验证准确性
    accuracy = pipe.evaluator(data['evaluator'], prediction, "label")

    # step6 打印模型准确率
    print("Test set accuracy = " + str(accuracy))
    result = {'appName': appName, 'accuracy': accuracy}
    return Response(json.dumps(result), mimetype='application/json')
if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)