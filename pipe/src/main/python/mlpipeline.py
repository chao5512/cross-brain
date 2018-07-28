#coding=utf-8
#!/usr/bin/python3
from pipeline import Pipe
from pyspark.ml import Pipeline
from pyspark.ml.feature import HashingTF, Tokenizer, basestring
from pyspark.sql import SparkSession,DataFrame
from pyspark.ml.classification import LogisticRegression,LogisticRegressionModel
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
import configparser

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
            .getOrCreate()
        self.applicationId = self.spark.sparkContext.applicationId
        print(self.spark.version)
        print(self.applicationId)
        return self.spark

    """加载数据 pyspark.sql.DataFrame"""
    def loadDataSet(self,dataFrame):
        if not (isinstance(dataFrame, DataFrame)):
            raise TypeError(
                "DataType Error!,Is not a pyspark.sql.DataFrame!")
        self.dataFrame = dataFrame

    """参数ratio类型为列表，两个元素构成表示train和test数据集比例权重"""
    def split(self, ratio):
        element0 = self.dataFrame.randomSplit(ratio)
        self.trainSet = element0[0]
        self.testSet = element0[1]

    def buildPipeline(self, originalStages):
        stages = self.buildStages(originalStages)
        self.pipeline = Pipeline(stages=stages)
        self.pipeline.write.save(self.pipePath)#保存pipeline
        model = self.pipeline.fit(self.trainSet)
        return model

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


    def validator(self, model):
        prediction = model.transform(self.testSet)
        return prediction

    """Pipeline执行"""
    def run(self,stages):
        model = self.buildPipeline(stages)
        prediction = self.validator(model)

    def evaluator(self, e, predictions, labelCol):
        if e == 'MulticlassClassificationEvaluator':
            evaluator = MulticlassClassificationEvaluator(labelCol=labelCol, predictionCol="prediction",
                                                          metricName="accuracy")
            accuracy = evaluator.evaluate(predictions)
            return accuracy
            # evaluator = MulticlassClassificationEvaluator()
            # accuracy =evaluator.setMetricName("accuracy").evaluate(predictions);
            # weightedPrecision=evaluator.setMetricName("weightedPrecision").evaluate(predictions);
            # weightedRecall=evaluator.setMetricName("weightedRecall").evaluate(predictions);
            # f1=evaluator.setMetricName("f1").evaluate(predictions);

    """保存Model文件"""
    def saveModel(self,model):
        model.save(self.modelPath);

    """加载Model文件"""
    def loadModel(self,stages,modelPath,pipePath):
        self.pipeline.read().load(pipePath)
        if isinstance(stages[len(stages)], LogisticRegressionModel):
            return LogisticRegressionModel.load(self.spark,modelPath)

    """关闭SparkSession"""
    def stop(self):
        self.spark.stop()

    def setParams(self,pipePath,modelPath):
        self.pipePath = pipePath
        self.modelPath = modelPath
