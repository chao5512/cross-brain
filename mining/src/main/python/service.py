import base64

from flask import Flask, Response, request
from io import BytesIO
from result import Result
import seaborn as sns
import matplotlib.pyplot as plt
from hiveutil import HiveClient
from jsoncustom import JsonCustomEncoder
import numpy as np
from stringutils import StringTolist
from flask_cors import *
import json
import logging
import re

plt.switch_backend('agg')

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d]  %(message)s',
                    datefmt='%a, %d %b %Y %H:%M:%S',
                    filename='../work.log',
                    filemode='a')
app = Flask(__name__)
# CORS(app)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')


# 显示数据总行数
@app.route("/count", methods=['POST'])
def count():
    data = request.form.to_dict()
    tableName = data['tableName']
    logging.info("tableName:" + tableName)
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            datas = HiveClient.queryRowNumber(tablename=tableName)
            title = datas.columns.values.tolist()
            result = Result(data={'type': 'table',
                                  'title': title,
                                  'content': datas.to_dict(orient='split')['data']})
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')


# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            logging.info("tableName:" + tableName)
            number = requestData['number']
            if number.strip() != "":
                if re.match(r'\d+$',number):
                    datas = HiveClient.queryByRowNums(tablename=tableName, rownums=number)
                    title = datas.columns.values.tolist()
                    result = Result(data={'type': 'table',
                                          'title': title,
                                          'content': datas.to_dict(orient='split')['data']})
                    # return Response(simplejson.dumps(result,default=lambda obj: obj.__dict__),
                    #                  mimetype='application/json')
                    return Response(json.dumps(result, cls=JsonCustomEncoder),
                                    mimetype='application/json')
                else:
                    result= Result(code=3003, data=None, message="行号格式必须为正整数")
                    return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                    mimetype='application/json')
            else:
                raise AttributeError("行号不能为空")
        except AttributeError :
            result= Result(code=3004, data=None, message="行号不能为空")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
        except BaseException as e:
            logging.exception(e)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')

# 简单统计量表
@app.route("/sheet", methods=['POST'])
def sheet():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
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
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')


# 直方图
@app.route("/hist", methods=['POST'])
def hist():
    requestData = request.form.to_dict()
    tableName = requestData['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = requestData['x']
            x=StringTolist.toList(x)
            logging.info(x)
            if (len(x) == 2):
                xlabel = x[0]
                row = x[1]
                col = None
                hue = None
            elif (len(x) == 3):
                xlabel = x[0]
                row = x[1]
                col = x[2]
                hue = None
            elif (len(x) == 4):
                xlabel = x[0]
                row = x[1]
                col = x[2]
                hue = x[3]
            else :
                result= Result(code=3005, data=None, message="横坐标值不能超过3个或者少于2个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            # 设置主题，可选项有darkgrid , whitegrid , dark , white ,和 ticks
            sns.set(style="dark", palette="muted", color_codes=True)
            g = sns.FacetGrid(row=row, col=col, hue=hue,data=train_df)
            # alpha颜色对比度
            g.map(plt.hist, xlabel, alpha=0.7, bins=20)
            g.add_legend()
            return base64image()
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')

# 箱线图
@app.route("/boxplot", methods=['POST'])
def boxplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            y = data.get("y",'')
            x=StringTolist.toList(x)
            y=StringTolist.toList(y)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
            if (len(y) == 1 and y[0]!=''):
                y = y[0]
            elif (len(y) == 1 and y[0]==''):
                y = None
            else:
                result= Result(code=3007, data=None, message="纵坐标值最多选一个值")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.boxplot(y=y, x=x, hue=hue, data=train_df, palette="muted")
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()


# 小提琴图
@app.route("/violinplot", methods=['POST'])
def violinplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            y = data.get("y",'')
            x=StringTolist.toList(x)
            y=StringTolist.toList(y)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            if (len(y) == 1 and y[0]!=''):
                y = y[0]
            elif (len(y) == 1 and y[0]==''):
                y = None
            else:
                result= Result(code=3007, data=None, message="纵坐标值最多选一个值")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.violinplot(y=y, x=x, hue=hue, data=train_df,
                       palette="muted")
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()


# 适合分类数据的散点图
@app.route("/swarmplot", methods=['POST'])
def swarmplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            y = data.get("y",'')
            x=StringTolist.toList(x)
            y=StringTolist.toList(y)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            if (len(y) == 1 and y[0]!=''):
                y = y[0]
            elif (len(y) == 1 and y[0]==''):
                y = None
            else:
                result= Result(code=3007, data=None, message="纵坐标值最多选一个值")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.swarmplot(y=y, x=x, hue=hue, data=train_df,
                      palette="muted")
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()


# 柱状图
@app.route("/barplot", methods=['POST'])
def barplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            y = data.get("y",'')
            x=StringTolist.toList(x)
            y=StringTolist.toList(y)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            if (len(y) == 1 and y[0]!=''):
                y = y[0]
            elif (len(y) == 1 and y[0]==''):
                y = None
            else:
                result= Result(code=3007, data=None, message="纵坐标值最多选一个值")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            # estimator=median,mean
            sns.barplot(y=y, x=x, hue=hue, data=train_df, palette="muted",
                    estimator=np.median, ci=0)
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()


# 数量统计图
@app.route("/countplot", methods=['POST'])
def countplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            x=StringTolist.toList(x)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.countplot(x=x, hue=hue, data=train_df, palette="muted")
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()



@app.route("/factorplot", methods=['POST'])
def factorplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            x=StringTolist.toList(x)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                col = None
            elif (len(x) == 2):
                x, col = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.factorplot(x=x, col=col, data=train_df, kind="count")
        except BaseException as e:
            logging.info(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
        return base64image()


# 线性回归图
@app.route("/lmplot", methods=['POST'])
def lmplot():
    data = request.form.to_dict()
    tableName = data['tableName']
    # 未选择表名时，取到的是默认值0
    if tableName == '':
        result= Result(code=3001, data=None, message="请选择表名")
        return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                        mimetype='application/json')
    else:
        try:
            x = data['x']
            y = data.get("y",'')
            x=StringTolist.toList(x)
            y=StringTolist.toList(y)
            if (len(x) == 1 and x[0]!=''):
                x = x[0]
                hue = None
            elif (len(x) == 2):
                x, hue = x
            else:
                result= Result(code=3006, data=None, message="横坐标值不能超过两个或者少于一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            if (len(y) == 1 and y[0]!=''):
                y = y[0]
            else:
                result= Result(code=3007, data=None, message="纵坐标值不能少于一个或多余一个")
                return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                                mimetype='application/json')
            train_df = HiveClient.queryForAll(tablename=tableName)
            sns.set_style("whitegrid")
            sns.lmplot(y=y, x=x, hue=hue,
                   data=train_df, palette="muted", order=2)
        except BaseException as e:
            print(e.args)
            result= Result(code=3002, data=None, message="服务暂不可用")
            return Response(json.dumps(result, default=lambda obj: obj.__dict__),
                            mimetype='application/json')
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
    app.run(port=3003, host='0.0.0.0', debug=True)
