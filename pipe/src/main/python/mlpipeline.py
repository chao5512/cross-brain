#!/usr/bin/python3
from pipeline import Pipeline
from pyspark.sql import SparkSession,DataFrame
from pyspark.ml.classification import LogisticRegression,LogisticRegressionModel

class MLPipeline(Pipeline):

    """初始化参数 appName:任务名称"""
    def __init__(self,appName):
        super.__init__(appName)

    """返回SparkSession对象"""
    def create(self,master):
        """create SparkSession"""
        self.spark = SparkSession.builder.master(master).appName(self.appName).getOrCreate()
        return self.spark

    """加载数据 pyspark.sql.DataFrame"""
    def loadDataSet(self,dataFrame):
        if not (isinstance(dataFrame, DataFrame)):
            raise TypeError(
                "DataType Error!,Is not a pyspark.sql.DataFrame!")
        self.dataFrame = dataFrame

    """参数ratio类型为列表，两个元素构成表示train和test数据集比例"""
    def split(self,ratio):
        element0 = ratio[0]/(ratio[0]+ratio[1])
        size = len(self.dataFrame);
        position = element0/size
        self.trainSet = self.dataFrame[0:position]
        self.testSet = self.dataFrame[position+1]


    """Pipeline执行"""
    def run(self,stages):
        pipeline = Pipeline(stages=stages)
        model = pipeline.fit(self.trainSet)
        prediction = model.transform(self.testSet)
        prediction.select("probability","prediction")
        return model

    def evaluator(self,evaluator,predictions):
        accuracy =evaluator.setMetricName("accuracy").evaluate(predictions);
        weightedPrecision=evaluator.setMetricName("weightedPrecision").evaluate(predictions);
        weightedRecall=evaluator.setMetricName("weightedRecall").evaluate(predictions);
        f1=evaluator.setMetricName("f1").evaluate(predictions);

    """保存Model文件"""
    def saveModel(self,model,modelPath):
        model.save(modelPath);

    """加载Model文件"""
    def loadModel(self,stages,modelPath):
        if isinstance(stages[len(stages)], LogisticRegressionModel):
            return LogisticRegressionModel.load(self.spark,modelPath)

    """关闭SparkSession"""
    def stop(self):
        self.spark.stop()