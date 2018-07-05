import base64

from flask import Flask, Response, request
from io import BytesIO
from result import Result
import seaborn as sns
import matplotlib.pyplot as plt
from flask_cors import *
from hiveutil import HiveClient
from jsoncustom import JsonCustomEncoder
import numpy as np

import json
import simplejson

app = Flask(__name__)
CORS(app, supports_credentials=True)


@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')


# 显示数据总行数
@app.route("/count", methods=['POST'])
def count():
    data = request.form.to_dict()
    tableName = data['tableName']
    print("tableName:" + tableName)
    datas = HiveClient.queryRowNumber(tablename=tableName)
    title = datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title': title,
                          'content': datas.to_dict(orient='split')['data']})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    print("tableName:" + tableName)
    number = requestData['number']
    print("number:" + number)
    datas = HiveClient.queryByRowNums(tablename="titanic_orc", rownums=number)
    title = datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title': title,
                          'content': datas.to_dict(orient='split')['data']})
    return Response(simplejson.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


@app.route("/tail", methods=['POST'])
def tail():
    datas = HiveClient.queryByRowNums(tablename="titanic_orc")
    title = datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title': title,
                          'content': datas.tail().to_dict(orient='split')[
                              'data']})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


# 简单统计量表
@app.route("/sheet", methods=['POST'])
def sheet():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    type = requestData['type']
    datas = HiveClient.queryForAll(tablename=tableName)
    if type == '1':
        tempData = datas.describe()
        title = tempData.columns.values.tolist()
        index = tempData.to_dict(orient='split')['index']
        result = Result(data={'type': 'table',
                              'title': title,
                              'index': index,
                              'content': tempData.to_dict(
                                      orient='split')['data']})
    else:
        tempData = datas.describe(include=['O'])
        title = tempData.columns.values.tolist()
        index = tempData.to_dict(orient='split')['index']
        result = Result(data={'type': 'table',
                              'title': title,
                              'index': index,
                              'content': tempData.to_dict(
                                      orient='split')['data']});
    return Response(json.dumps(result, cls=JsonCustomEncoder),
                    mimetype='application/json')


# 直方图
@app.route("/hist", methods=['POST'])
def hist():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    x = requestData['x']
    y = requestData['y']
    train_df = HiveClient.queryForAll(tablename="titanic_orc")
    # 设置主题，可选项有darkgrid , whitegrid , dark , white ,和 ticks
    sns.set(style="dark", palette="muted", color_codes=True)
    g = sns.FacetGrid(train_df, hue='survived')
    # alpha颜色对比度
    g.map(plt.hist, 'age', alpha=0.7, bins=20)
    g.add_legend()
    return base64image()


@app.route("/hist1", methods=['POST'])
def hist1():
    train_df = HiveClient.queryForAll(tablename="titanic_orc")
    # 设置主题，可选项有darkgrid , whitegrid , dark , white ,和 ticks
    sns.set(style="dark", palette="muted", color_codes=True)
    g = sns.FacetGrid(train_df, col='survived', row='pclass', size=2.2,
                      aspect=1.6)
    # alpha颜色对比度
    g.map(plt.hist, 'age', alpha=0.7, bins=20)
    g.add_legend()
    # plt.savefig("examples1.jpg")
    return base64image()


# 箱线图
@app.route("/boxplot", methods=['POST'])
def boxplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    if (len(y) == 1):
        y = y[0]
    else:
        return Result(code=1001, data=None, message="纵坐标值不能超过1个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.boxplot(y=y, x=x, hue=hue, data=train_df, palette="muted")
    return base64image()


# 小提琴图
@app.route("/violinplot", methods=['POST'])
def violinplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    if (len(y) == 1):
        y = y[0]
    else:
        return Result(code=1001, data=None, message="纵坐标值不能超过1个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.violinplot(y=y, x=x, hue=hue, data=train_df,
                   palette="muted")
    return base64image()


# 适合分类数据的散点图
@app.route("/swarmplot", methods=['POST'])
def swarmplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    if (len(y) == 1):
        y = y[0]
    else:
        return Result(code=1001, data=None, message="纵坐标值不能超过1个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.swarmplot(y=y, x=x, hue=hue, data=train_df,
                  palette="muted")
    return base64image()


# 柱状图
@app.route("/barplot", methods=['POST'])
def barplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    if (len(y) == 1):
        y = y[0]
    else:
        return Result(code=1001, data=None, message="纵坐标值不能超过1个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    # estimator=median,mean
    sns.barplot(y=y, x=x, hue=hue, data=train_df, palette="muted",
                estimator=np.median, ci=0)
    return base64image()


# 数量统计图
@app.route("/countplot", methods=['POST'])
def countplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.countplot(x=x, hue=hue, data=train_df, palette="muted")
    return base64image()



@app.route("/factorplot", methods=['POST'])
def factorplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        col = None
    elif (len(x) == 2):
        x, col = x
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.factorplot(x=x, col=col, data=train_df, kind="count")
    return base64image()


# 线性回归图
@app.route("/lmplot", methods=['POST'])
def lmplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    x = data['x']
    y = data['y']
    if (len(x) == 1):
        x = x[0]
        hue = None
    elif (len(x) == 2):
        x, hue = x
    else:
        return Result(code=1001, data=None, message="横坐标值不能超过2个")
    if (len(y) == 1):
        y = y[0]
    else:
        return Result(code=1001, data=None, message="纵坐标值不能超过1个")
    train_df = HiveClient.queryForAll(tablename=tableName)
    sns.set_style("whitegrid")
    sns.lmplot(y=y, x=x, hue=hue,
               data=train_df, palette="muted", order=2)
    return base64image()


def base64image():
    sio = BytesIO()
    plt.savefig(sio, format='png')
    image = base64.encodebytes(sio.getvalue()).decode()
    imagehtml = '<img src="data:image/png;base64,{}" />'
    plt.close()
    result = Result(data={'type': 'image', 'content': imagehtml.format(image)})
    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                    mimetype='application/json')


if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0', debug=True)
