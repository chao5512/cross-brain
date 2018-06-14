import base64

from flask import Flask, Response
from io import BytesIO
from result import Result
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

import json

app = Flask(__name__)


@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')


# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    train_df = pd.read_csv(
            '/Users/hanwei/Documents/notebook/input/titanic/train.csv')
    result = Result(data={'type': 'table',
                          'content': train_df.head().to_dict(orient='split')})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


@app.route("/tail", methods=['POST'])
def tail():
    train_df = pd.read_csv(
            '/Users/hanwei/Documents/notebook/input/titanic/train.csv')
    result = Result(data={'type': 'table',
                          'content': train_df.tail().to_dict(orient='split')})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 简单统计量表
@app.route("/sheet", methods=['POST'])
def sheet():
    train_df = pd.read_csv(
            '/Users/hanwei/Documents/notebook/input/titanic/train.csv')
    test = train_df.describe()
    print(test)
    result = Result(data={'type': 'table',
                          'content': train_df.describe().to_dict(
                                  orient='split')})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 直方图
@app.route("/hist", methods=['POST'])
def hist():
    train_df = pd.read_csv(
            '/Users/hanwei/Documents/notebook/input/titanic/train.csv')
    g = sns.FacetGrid(train_df, col='Survived')
    g.map(plt.hist, 'Age', bins=20)
    # plt.savefig("examples.jpg")
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
