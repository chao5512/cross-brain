from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.feature import HashingTF, Tokenizer
from pyspark.ml import Pipeline
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
spark = SparkSession\
    .builder\
    .appName("Segment Test")\
    .config("executor-memory", "512m") \
    .getOrCreate();

filePath = "E:/tmp/pythonData/sougou-train";
# hdfs://172.16.31.231:9000/data
# .master("spark://172.16.31.231:7077")\
# filePath = "D:/beh/ckoocML/data/classnews/train/culture.txt";
textRDD = spark.sparkContext.textFile(filePath)
# 每条RDD均变为String数组类型（should）
# lastRDD = textRDD.map(lambda x: x.split('\u00EF'))
# schema = StructType([
#     StructField("lable",StringType(),True),
#     StructField("title",StringType(),True),
#     StructField("time",StringType(),True),
#     StructField("content",StringType(),True)
# ])
# textDF = spark.createDataFrame(lastRDD,schema)
# textDF.show()

# step1 输入原始数据
lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])

schema = StructType([
    StructField("label", StringType(), True),
    StructField("content", StringType(), True)])

textRDD = spark.createDataFrame(lastRDD, schema)
lastDF = textRDD.withColumn("label", textRDD["label"].cast("Double"))
# lastDF.show()

# step2 切分数据集
splits = lastDF.randomSplit([0.6, 0.4], 1234)
train = splits[0]
test = splits[1]

# step3 分词器分词
tokenizer = Tokenizer(inputCol="content", outputCol="words")
wordsData = tokenizer.transform(lastDF)
# wordsData.show()

# step4 计算词频
hashingTF = HashingTF(inputCol=tokenizer.getOutputCol(), outputCol="features")
featurizedData = hashingTF.transform(wordsData)
featurizedData.show()

# step5 逻辑回归
lr = LogisticRegression(maxIter=10, regParam=0.001)

# step6组装pipeline
pipeline = Pipeline(stages=[tokenizer, hashingTF, lr])

# step7 训练模型
model = pipeline.fit(train)

# step8 评估模型
prediction = model.transform(test)
selected = prediction.select("label", "content", "probability", "prediction")
# for row in selected.collect():
#     label, content, prob, prediction = row
#     print("(%d, %s) --> prob=%s, prediction=%f" % (label, content, str(prob), prediction))
# step9 验证准确性
evaluator = MulticlassClassificationEvaluator(labelCol="label", predictionCol="prediction",
                                              metricName="accuracy")
accuracy = evaluator.evaluate(prediction)
# step10 打印模型准确率
print("Test set accuracy = " + str(accuracy))



