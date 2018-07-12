from pyspark.sql.functions import split
from pyspark.ml.fpm import FPGrowth
from pyspark.sql import SparkSession
from mlpipeline import MLPipeline
from flask import Flask, Response,jsonify, request, abort
import json

# spark = SparkSession \
#     .builder \
#     .master("spark://172.16.31.231:7077") \
#     .appName("FPgrowth test") \
#     .config("executor-memory", "512m") \
#     .getOrCreate()

app = Flask(__name__)
@app.route("/FPGrowth/health")
def health():
        result = {'status': 'UP'}
        return Response(json.dumps(result), mimetype='application/json')

@app.route("/FPGrowth/submit",methods=['POST'])
def submit():
        data = json.loads(request.get_data())
        appName = data['appName']
        pipe = MLPipeline(appName)
        pipe.create()
        pipe.buildStages(data['originalStages'])
        result = {'appName': appName}
        return Response(json.dumps(result), mimetype='application/json')

@app.route("/FPGrowth/loaddata",methods=['POST'])
def loadDataSet():
        # print(request.get_data())
        # data = json.loads(request.get_data())
        # appName = data['appName']
        appName ="mmm"
        pipe = MLPipeline(appName)
        spark = pipe.create()
        # dataRdd = (spark.read.text(data['dataPath'])   实际参数
        # split("value","\s+")  只要i有空格就当作切分符
        # .alias  命名
        dataRdd = (spark.read.text("/machen/data/sample_fpgrowth.txt")
                .select(split("value","\s+").alias("items")))
        # 数据展示
        print ("数据展示")
        dataRdd.show(truncate=False)
        #由 传入的data 获取相关参数变量
        # minSupport = data["minSupport"]
        # minConfidence = data["minConfidence"]
        fp = FPGrowth(minSupport=0.2, minConfidence=0.7)
        # 属性设置参数
        # fp = FPGrowth()
        # fp.setItemsCol("FPGrowthitems")
        # fp.setNumPartitions(2)
        # fp.setMinConfidence()
        # fp.setMinSupport()
        fpm = fp.fit(dataRdd)

        print ("查看所有频繁项集")
        fpm.freqItemsets.show(5)

        print ("产生关联规则")
        fpm.associationRules.show(5)

        print ("条件关联度查询: "+"关联条件为  [p] ")
        # new_data = spark.createDataFrame([(["t", "s"], )], ["items"])
        # new_data.show()
        # dataFramaFrom = data['dataFramaFrom']
        new_data2 = spark.createDataFrame([(["p"], )], ["items"])
        new_data2.show()

        print ("执行关联结果：")
        (fpm.transform(new_data2)).show()
        finaldata = fpm.transform(new_data2).first().prediction
        sorted(finaldata)
        print (finaldata)

        result = {'appName': appName}
        return Response(json.dumps(result), mimetype='application/json')
if __name__ == '__main__':
        app.run(port=3002, host='172.16.31.231',debug=True)