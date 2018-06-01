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

pipe.trainSet.show()