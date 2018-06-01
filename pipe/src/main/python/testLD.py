from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline
from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.feature import HashingTF, Tokenizer
from pyspark.ml import Pipeline
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

pipe = MLPipeline("syd")
spark = pipe.create()
textRDD = spark.sparkContext.textFile("E:/tmp/pythonData/sougou-train")
lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])
schema = StructType([
    StructField("label", StringType(), True),
    StructField("content", StringType(), True)])
textDF = spark.createDataFrame(lastRDD, schema)
lastDF = textDF.withColumn("label", textDF["label"].cast("Double"))
# lastDF.show()
# print(lastDF.schema())

# step2 load dataset
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

stages = [tokenizer, hashingTF, lr]
# step6组装pipeline
pipe.buildPipeline(stages)

pipe.trainSet.show()