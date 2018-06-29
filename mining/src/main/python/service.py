import base64

from flask import Flask, Response,request
from io import BytesIO
from result import Result
import seaborn as sns
import matplotlib.pyplot as plt
from flask_cors import *
from hiveutil import HiveClient
from jsoncustom import JsonCustomEncoder


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
    print("tableName:"+tableName)
    datas = HiveClient.queryRowNumber(tablename=tableName)
    title=datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                          'content': datas.to_dict(orient='split')['data']})
    return Response(json.dumps(result,default=lambda obj: obj.__dict__),
                    mimetype='application/json')

# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    print("tableName:"+tableName)
    number = requestData['number']
    print("number:"+number)
    datas = HiveClient.queryByRowNums(tablename="titanic_orc",rownums=number)
    title=datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                           'content': datas.to_dict(orient='split')['data']})
    return Response(simplejson.dumps(result,default=lambda obj: obj.__dict__),
                    mimetype='application/json')


@app.route("/tail", methods=['POST'])
def tail():
    datas = HiveClient.queryByRowNums(tablename="titanic_orc")
    title=datas.columns.values.tolist()
    result = Result(data={'type': 'table',
                          'title':title,
                          'content': datas.tail().to_dict(orient='split')['data']})
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
        tempData=datas.describe()
        title=tempData.columns.values.tolist()
        index=tempData.to_dict(orient='split')['index']
        result = Result(data={'type': 'table',
                              'title':title,
                              'index':index,
                              'content': tempData.to_dict(
                                  orient='split')['data']})
    else:
        tempData = datas.describe(include=['O'])
        title=tempData.columns.values.tolist()
        index=tempData.to_dict(orient='split')['index']
        result = Result(data={'type': 'table',
                              'title':title,
                              'index':index,
                              'content': tempData.to_dict(
                                  orient='split')['data']});
    return Response(json.dumps(result,cls= JsonCustomEncoder),
                    mimetype='application/json')


# 直方图
@app.route("/hist", methods=['POST'])
def hist():
    train_df = HiveClient.queryForAll(tablename="titanic_orc")
    # 设置主题，可选项有darkgrid , whitegrid , dark , white ,和 ticks
    sns.set(style="dark", palette="muted", color_codes=True)
    g = sns.FacetGrid(train_df, hue='survived')
    # alpha颜色对比度
    g.map(plt.hist,'age',alpha=0.7,bins=20)
    g.add_legend()
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


@app.route("/hist1", methods=['POST'])
def hist1():
    train_df = HiveClient.queryForAll(tablename="titanic_orc")
    # 设置主题，可选项有darkgrid , whitegrid , dark , white ,和 ticks
    sns.set(style="dark", palette="muted", color_codes=True)
    g = sns.FacetGrid(train_df, col='survived',row='pclass',size=2.2,aspect=1.6)
    # alpha颜色对比度
    g.map(plt.hist,'age',alpha=0.7,bins=20)
    g.add_legend()
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
