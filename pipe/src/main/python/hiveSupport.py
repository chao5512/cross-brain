from pyspark import SparkConf, SparkContext
from pyspark.sql import HiveContext

conf = (SparkConf()
       .setMaster("spark://172.16.31.231:7077")
       .setAppName("Myapp")
       .set("spark.executor.memory","2g"))
sc = SparkContext(conf=conf)
sqlContext = HiveContext(sc)
my_dataframe = sqlContext.sql("show tables")
my_dataframe.show()