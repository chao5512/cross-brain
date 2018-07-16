from pyspark.sql.functions import split
from pyspark.ml.fpm import FPGrowth
from mlpipeline import MLPipeline
from pyspark.sql import SparkSession
from pyspark.ml.fpm import FPGrowth, SparkContext
from pyspark.sql.context import SQLContext
from pyspark.conf import SparkConf
from pyspark.sql.types import *
from pyspark.sql.functions import regexp_replace

spark = SparkSession \
    .builder \
    .master("spark://172.16.31.231:7077") \
    .appName("FPgrowth movie") \
    .config("executor-memory", "512m") \
    .getOrCreate()

# print("第一种读取方式初始结构")
#  data = spark.read.csv("/machen/data/movie/ratings.csv", header=True)  #DF
# data.printSchema()  # 自动分解，展示结构

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
data = sc.textFile("/machen/data/movie/test.csv")
head = data.first()
print("rdd第一行（表头）："+head)
lines = data.filter(lambda row:row != head)#删除第一行
parts = lines.map(lambda l: l.split(","))
em = parts.map(lambda p:(int(p[0]),int(p[1]),float(p[2])))

print("#----------------------------------------------------------")
print("获取  rating 等于5的数据")
choice = em.filter(lambda x:x[2]==5.0)
print("分组非聚合----选取")
make = choice.map(lambda p:(p[0],p[1]))

# print("分组非聚合----aggregateBykey--分组归并")
# arrayBykey = make.aggregateByKey([], lambda seq, elem: seq + [elem], lambda a, b: a + b)\
#     .sortByKey()

print("rdd---groupBykey----返回 seq（）")
group = make.groupByKey().mapValues(list).sortByKey()
mydata = group.map(lambda x:x[1])
print(mydata.first())
moviedata = group.toDF()
moviedata.show()
moviedata.printSchema()

newdata = spark.createDataFrame(group).toDF('userId','moviesId')
# +------+--------------------+
# |userId|            moviesId|
# +------+--------------------+
# |     1|[4993, 5952, 7153...|
# |     2|[62, 70, 260, 266...|
# |     3|[50, 175, 223, 26...|
# +------+--------------------+
movie = newdata.select(newdata['moviesId'].alias('items'))
movie.show()
movie.printSchema()

#  最后的的  df 结构：
# +--------------------+
# |               items|
# +--------------------+
# |[4993, 5952, 7153...|
# |[62, 70, 260, 266...|
# |[50, 175, 223, 26...|
# +--------------------+
print("开始执行关联规则运算：")
fp = FPGrowth(minSupport=0.2, minConfidence=0.7)
# 属性设置参数
# fp = FPGrowth()
# fp.setItemsCol("FPGrowthitems")
# fp.setNumPartitions(2)
# fp.setMinConfidence()
# fp.setMinSupport()
fpm = fp.fit(movie)

print ("查看所有频繁项集")
fpm.freqItemsets.show(5)
# +--------------------+----+
# |               items|freq|
# +--------------------+----+
# |               [924]|   1|
# |          [924, 480]|   1|
# |     [924, 480, 490]|   1|
# |[924, 480, 490, 1...|   1|
# |[924, 480, 490, 1...|   1|
# +--------------------+----+

print ("产生关联规则")
fpm.associationRules.show(5)
# +--------------------+----------+----------+
# |          antecedent|consequent|confidence|
# +--------------------+----------+----------+
# |[1197, 1073, 541,...|    [1060]|       1.0|
# |[1197, 1073, 541,...|    [1196]|       1.0|
# |[1197, 1073, 541,...|     [593]|       1.0|
# |[1197, 1073, 541,...|     [318]|       1.0|
# |[1197, 1073, 541,...|     [953]|       1.0|
# +--------------------+----------+----------+
print("对大相关度电影数量：")
print(fpm.associationRules.select('antecedent').first())

print ("条件关联度查询: "+"关联条件为  [1197, 1073, 541, 1213, 329, 316, 924, 490, 1214] 编号的电影关联度 ")
# new_data = spark.createDataFrame([(["t", "s"], )], ["items"])
# new_data.show()
# dataFramaFrom = data['dataFramaFrom']
new_data2 = spark.createDataFrame([([1197, 1073, 541, 1213, 329, 316, 924, 490, 1214], )], ["items"])

print ("执行关联结果：")
(fpm.transform(new_data2)).show()
# +--------------------+--------------------+
# |               items|          prediction|
# +--------------------+--------------------+
# |[1197, 1073, 541,...|[1060, 1196, 593,...|
# +--------------------+--------------------+
finaldata = fpm.transform(new_data2).first().prediction

# [1060, 1196, 593, 318, 953, 480, 260, 50]

sorted(finaldata)
print (finaldata)