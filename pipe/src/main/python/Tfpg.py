from pyspark.sql.functions import split
from pyspark.ml.fpm import FPGrowth
from mlpipeline import MLPipeline
from pyspark.sql import SparkSession

spark = SparkSession \
    .builder \
    .master("spark://172.16.31.231:7077") \
    .appName("FPgrowth test") \
    .config("executor-memory", "512m") \
    .getOrCreate()
data=(spark.read.text("/machen/data/sample_fpgrowth.txt")
      .select(split("value","\s+")
      .alias("items")))

data.show(truncate=False)

fp = FPGrowth(minSupport=0.2, minConfidence=0.7)

fpm = fp.fit(data)

fpm.freqItemsets.show(5)

fpm.associationRules.show(5)

new_data = spark.createDataFrame([(["t", "s"], )], ["items"])

sorted(fpm.transform(new_data).first().prediction)