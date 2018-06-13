from flask import Flask, Response
from result import Result
import json
app = Flask(__name__)


@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')


# 显示数据前n行
@app.route("/head", methods=['POST'])
def head():
    result = Result(data="test")
    return Response(json.dumps(result,default=lambda obj: obj.__dict__ ), mimetype='application/json')


# 简单统计量表
@app.route("/sheet", methods=['POST'])
def sheet():
    result = Result(data="test")
    return Response(json.dumps(result,default=lambda obj: obj.__dict__ ), mimetype='application/json')


# 箱线图
@app.route("/boxplot", methods=['POST'])
def boxplot():
    result = Result(data="test")
    return Response(json.dumps(result,default=lambda obj: obj.__dict__ ), mimetype='application/json')


if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0', debug=True)
