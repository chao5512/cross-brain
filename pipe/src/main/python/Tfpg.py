from pyspark.sql.functions import split
from pyspark.ml.fpm import FPGrowth
from mlpipeline import MLPipeline
from pyspark.sql import SparkSession
from pyspark.ml.fpm import FPGrowth, SparkContext
from pyspark.sql.context import SQLContext
from pyspark.conf import SparkConf
from pyspark.sql.types import *

spark = SparkSession \
    .builder \
    .master("spark://172.16.31.231:7077") \
    .appName("FPgrowth test ") \
    .config("executor-memory", "512m") \
    .getOrCreate()

print("第一种读取方式初始结构")
#  data = spark.read.csv("/machen/data/movie/ratings.csv", header=True)  #DF
# data.printSchema()  # 自动分解，展示结构

print("创建schema 结构")
schema = StructType([StructField('personId',LongType(),True),
                     StructField('movieId',LongType(),True),
                     StructField("rating", FloatType(), True)])
# data.show(truncate=False)
#
# fp = FPGrowth(minSupport=0.2, minConfidence=0.7)
#
# fpm = fp.fit(data)
#
# fpm.freqItemsets.show(5)
#
# fpm.associationRules.show(5)
#
# new_data = spark.createDataFrame([(["t", "s"], )], ["items"])
#
# sorted(fpm.transform(new_data).first().prediction)

print("第二种读取方式的结构模式，直接更改列结构")
# newdata = spark.read.format('com.databricks.spark.csv')\
#     .options(header='true', inferschema='true')\
#     .schema(schema)\
#     .load('/machen/data/movie/ratings.csv')
# newdata.printSchema()






print("第三种 创建sc方式")
conf = SparkConf().setMaster("spark://172.16.31.231:7077").setAppName("fpg  sc")
sc = SparkContext.getOrCreate(conf)
#  直接转df
# sqlContest = SQLContext(sc)
# sqlContest.read.format('com.databricks.spark.csv')\
#     .options(header='true', inferschema='true')\
#     .load('/machen/data/movie/ratings.csv')
# rdd 后再转
print("先读取成rdd")
data = sc.textFile("/machen/data/movie/ratings.csv")
head = data.first()
print("rdd第一行（表头）："+head)
lines = data.filter(lambda row:row != head)#删除第一行
parts = lines.map(lambda l: l.split(","))
em = parts.map(lambda p:[int(p[0]),int(p[1]),float(p[2])])
print("转化后的Rdd 结构：")
print(em.first())
# print(lines.first())
# sqlContest = SQLContext(sc)
# RddToDF = sqlContest.createDataFrame(em,schema)
# print("转化成DF 的类型结构：")
# RddToDF.printSchema()
# print("转化成DF的数据样例：")
# RddToDF.show(4)
# print("选取 获得5星 好评的电影ID，以及给与评分的用户ID （前5行样本数据）")
# choiceDF = RddToDF.filter(RddToDF['rating']==5).select('personId','movieId').show(5)

