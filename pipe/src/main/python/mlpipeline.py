#coding=utf-8
#!/usr/bin/python3
from pipeline import Pipe
from pyspark.ml import Pipeline,PipelineModel
from pyspark.ml.feature import HashingTF, Tokenizer, basestring
from pyspark.sql import SparkSession,DataFrame
from pyspark.ml.classification import LogisticRegression,LogisticRegressionModel
from pyspark.ml.evaluation import MulticlassClassificationEvaluator,BinaryClassificationEvaluator,RegressionEvaluator,ClusteringEvaluator
import configparser
from pyspark.sql.types import *

from ml.process.TypeTransfer import TypeTransfer

from pyspark.sql import HiveContext

#基于SparkML Pipeline
class MLPipeline(Pipe):

    """初始化参数 appName:任务名称"""
    def __init__(self,appName):
        self.conf = configparser.ConfigParser()
        self.conf.read("conf.ini")
        super(MLPipeline,self).__init__(appName)

    """返回SparkSession对象"""
    def create(self):
        """create SparkSession"""
        print(self.conf.get('config','sparkMaster'))
        self.spark = SparkSession.builder \
            .master(self.conf.get('config','sparkMaster')) \
            .appName(self.appName)\
            .enableHiveSupport()\
            .getOrCreate()
        return self.spark

    """加载数据 pyspark.sql.DataFrame"""
    def loadDataSetFromTable(self,tableName):
        """
        filePath = "hdfs://172.16.31.232:9000/data"
        textRDD = self.spark.sparkContext.textFile(filePath)

        lastRDD = textRDD.map(lambda x: [x[0:1], x[2:]])

        schema = StructType([
            StructField("label", StringType(), True),
            StructField("content", StringType(), True)])

        self.dataFrame = self.spark.createDataFrame(lastRDD, schema)
        """
        self.dataFrame = self.spark.table(tableName)

    def preProcess(self):
        return

    """参数ratio类型为列表，两个元素构成表示train和test数据集比例权重"""
    def split(self, ratio):
        print("开始split")
        print(self.dataFrame)
        element = self.dataFrame.randomSplit(ratio)
        print(element)
        self.trainSet = element[0]
        self.testSet = element[1]

    def buildProcess(self, originalProcess):
        print('originalProcess')
        print(originalProcess)
        for className in originalProcess:
            params = ""
            for (param,paramValue) in originalProcess[className].items():
                if isinstance(paramValue, basestring):
                    params +="," +param+"='"+paramValue+"'"
                else:
                    params +="," +param+"="+str(paramValue)
            instance = eval(className+"("+params[1:]+")")
            instance.df = self.dataFrame
            self.dataFrame = instance.transform()

    def buildStages(self, originalStages):
        stages = []
        for className in originalStages:
            params = ""
            for (param,paramValue) in originalStages[className].items():
                if isinstance(paramValue, basestring):
                    params +="," +param+"='"+paramValue+"'"
                else:
                    params +="," +param+"="+str(paramValue)
            stages.append(eval(className+"("+params[1:]+")"))
        return stages

    def buildPipeline(self, originalStages):
        stages = self.buildStages(originalStages)
        self.pipeline = Pipeline(stages=stages)
        model = self.pipeline.fit(self.trainSet)
        model.save(self.modelPath) #保存模型文件
        return model

    def validator(self, model):
        prediction = model.transform(self.testSet)
        return prediction

    def evaluator(self, e, predictions, labelCol):
        # accuracy =evaluator.setMetricName("accuracy").evaluate(predictions);
        # weightedPrecision=evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        # weightedRecall=evaluator.setMetricName("weightedRecall").evaluate(predictions);
        # f1=evaluator.setMetricName("f1").evaluate(predictions);
        if e == 'MulticlassClassificationEvaluator':
            evaluator = MulticlassClassificationEvaluator(labelCol=labelCol, predictionCol="prediction")
            accuracy = evaluator.evaluate(predictions)
            return accuracy
        elif e == 'BinaryClassificationEvaluator':
            evaluator = BinaryClassificationEvaluator(rawPredictionCol='rawPrediction', labelCol='label')
            accuracy = evaluator.evaluate(predictions)
            return accuracy
        elif e == 'RegressionEvaluator':
            evaluator = RegressionEvaluator(predictionCol='prediction', labelCol='label')
            accuracy = evaluator.evaluate(predictions)
            return accuracy
        elif e == 'ClusteringEvaluator':
            evaluator = ClusteringEvaluator(predictionCol='prediction', featuresCol='features')
            accuracy = evaluator.evaluate(predictions)
            return accuracy

    """加载Model文件"""
    def loadModel(self,test):
        model = PipelineModel.load(self.modelPath)
        model.transform(test).select("probability", "prediction")

    """关闭SparkSession"""
    def stop(self):
        self.spark.stop()

    def setParams(self,pipePath,modelPath):
        self.pipePath = pipePath
        self.modelPath = modelPath
