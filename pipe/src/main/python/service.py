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
    print(request.form.get('appName'))
    appName = request.form.get('appName')
    pipe = MLPipeline(appName)
    pipe.create()
    result = {'appName': appName}
    return Response(json.dumps(result), mimetype='application/json')

# the pipeline of LogisticRegression
@app.route("/LRDemo",methods=['POST'])
def loadDataSet():
    # step1 create sparksession and dataframe
    print(request.form.get('appName'))
    appName = request.form.get('appName')
    pipe = MLPipeline(appName)
    spark = pipe.create()
    textRDD = spark.sparkContext.textFile(request.form.get('filePath'))
    lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])
    schema = StructType([
        StructField("label", StringType(), True),
        StructField("content", StringType(), True)])
    textDF = spark.createDataFrame(lastRDD, schema)
    lastDF = textDF.withColumn("label", textDF["label"].cast("Double"))

    # step2 load and split dataset
    pipe.loadDataSet(lastDF)
    pipe.split([0.6, 0.4])
    train = pipe.trainSet
    test = pipe.testSet

    # step3 分词器分词
    tokenizer = Tokenizer(inputCol="content", outputCol="words")

    # step4 计算词频
    hashingTF = HashingTF(inputCol=tokenizer.getOutputCol(), outputCol="features")

    # step5 逻辑回归
    lr = LogisticRegression(maxIter=10, regParam=0.001)

    # step6组装pipeline
    pipeline = Pipeline(stages=[tokenizer, hashingTF, lr])

    # step7 训练模型
    model = pipeline.fit(train)

    # step8 评估模型
    prediction = model.transform(test)
    selected = prediction.select("label", "content", "probability", "prediction")

    # step9 验证准确性
    evaluator = MulticlassClassificationEvaluator(labelCol="label", predictionCol="prediction",
                                                  metricName="accuracy")
    accuracy = evaluator.evaluate(prediction)
    # step10 打印模型准确率
    print("Test set accuracy = " + str(accuracy))

    result = {'appName': appName, 'accuracy': accuracy}
    return Response(json.dumps(result), mimetype='application/json')

if __name__ == '__main__':
    app.run(port=3001, host='0.0.0.0',debug=True)