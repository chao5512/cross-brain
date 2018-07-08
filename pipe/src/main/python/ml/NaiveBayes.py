#!usr/bin/python
#coding:utf-8

from pyspark.sql import SparkSession
from pyspark.sql import Row

from pyspark.sql import SparkSession
from pyspark.sql import Row

from pyspark.ml.feature import HashingTF, IDF, Tokenizer
from pyspark.ml.linalg import Vectors
from pyspark.ml.classification import NaiveBayes
from pyspark.ml.classification import NaiveBayesModel
from pyspark.ml.evaluation import MulticlassClassificationEvaluator

SPARK_MASTER_HOST = "yarn"

def parse_line(p):
    cols = p.split(' ')
    label = cols[0]
    if label not in ('Y', 'N'):
        return None

    label = 1.0 if label=='Y' else 0.0
    fname = ' '.join(cols[1:])

    return Row(label=label, sentence=fname)

def words_classify_main(spark):
    sc = spark.sparkContext
    # Tokenizer将输入的字符串格式化为小写，并按空格进行分割
    tokenizer = Tokenizer(inputCol="sentence", outputCol="words")
    # 自定义使用numFeatures个hash桶来存储特征值
    hashingTF = HashingTF(inputCol="words", outputCol="rawFeatures", numFeatures=8000)
    # Inverse Document Frequency(计算逆文本频率指数)
    idf = IDF(inputCol="rawFeatures", outputCol="features")

    # 从HDFS中加载输入源到dataframe中
    srcdf = sc.textFile('/yourpath/text_classified3_*'). \
        map(parse_line).filter(lambda x: x is not None).toDF()
    # 这里按80%-20%的比例分成训练集和测试集
    training, testing = srcdf.randomSplit([0.8, 0.2])

    # 得到训练集的词条集合
    wordsData = tokenizer.transform(training)
    # 将词条集合转换为特征向量集合
    featurizedData = hashingTF.transform(wordsData)
    # 在特征向量上应用fit()来得到model
    idfModel = idf.fit(featurizedData)
    # 得到每个单词对应的TF-IDF度量值
    rescaledData = idfModel.transform(featurizedData)
    # 持久化，避免重复加载
    rescaledData.persist()

    # 转换数据集用于NaiveBayes的输入
    trainDF = rescaledData.select("features", "label").rdd.map(
            lambda x:Row(label=float(x['label']), features=Vectors.dense(x['features']))
    ).toDF()
    # NaiveBayes分类器
    naivebayes = NaiveBayes(smoothing=1.0, modelType="multinomial")
    # 通过训练集得到NaiveBayesModel
    model = naivebayes.fit(trainDF)

    # 得到测试集的词条集合
    testWordsData = tokenizer.transform(testing)
    # 将词条集合转换为特征向量集合
    testFeaturizedData = hashingTF.transform(testWordsData)
    # 在特征向量上应用fit()来得到model
    testIDFModel = idf.fit(testFeaturizedData)
    # 得到每个单词对应的TF-IDF度量值
    testRescaledData = testIDFModel.transform(testFeaturizedData)
    # 持久化，避免重复加载
    testRescaledData.persist()

    testDF = testRescaledData.select("features", "label").rdd.map(
            lambda x:Row(label=float(x['label']), features=Vectors.dense(x['features']))
    ).toDF()
    # 使用训练模型对测试集进行预测
    predictions = model.transform(testDF)
    predictions.show()

    # 计算model在测试集上的准确性
    evaluator = MulticlassClassificationEvaluator(
            labelCol="label", predictionCol="prediction", metricName="accuracy")
    accuracy = evaluator.evaluate(predictions)
    print("The accuracy on test-set is " + str(accuracy))

if __name__ == "__main__":
    spark = SparkSession \
        .builder \
        .master('spark://172.16.31.231:7077') \
        .appName("spark_naivebayes_classify") \
        .getOrCreate()

    words_classify_main(spark)

    spark.stop()


