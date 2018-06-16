import base64

from flask import Flask, Response
from io import BytesIO
from result import Result
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from flask_cors import *
from hiveutil import HiveClient


import json

app = Flask(__name__)
CORS(app, supports_credentials=True)


@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')


# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    datas = HiveClient.queryForAll(tablename="titanic_orc")
    title=datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                           'content': datas.head().to_dict(orient='split')['data']})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


@app.route("/tail", methods=['POST'])
def tail():
    datas = HiveClient.queryForAll(tablename="titanic_orc")
    title=datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                          'content': datas.tail().to_dict(orient='split')['data']})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 简单统计量表
@app.route("/sheet", methods=['POST'])
def sheet():
    datas = HiveClient.queryForAll(tablename="titanic_orc")
    title=datas.describe().columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                          'content': datas.describe().to_dict(
                                  orient='split')['data']})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 直方图
@app.route("/hist", methods=['POST'])
def hist():
    train_df = HiveClient.queryForAll(tablename="titanic_orc")
    g = sns.FacetGrid(train_df, col='survived')
    g.map(plt.hist, 'age', bins=20)
    #plt.savefig("examples1.jpg")
    sio = BytesIO()
    plt.savefig(sio, format='png')
    image = base64.encodebytes(sio.getvalue()).decode()
    print(image)
    imagehtml = '<img src="data:image/png;base64,{}" />'
    plt.close()
    result = Result(data={'type': 'image', 'content': imagehtml.format(image)})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0', debug=True)
