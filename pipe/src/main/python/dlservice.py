#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
from dlpipeline import DLPipeline

from hdfs.client import Client
import logging
from logging.config import fileConfig

fileConfig('logging.conf')
logger=logging.getLogger('pipline')

import threading
import requests

app = Flask(__name__)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

@app.route("/deeplearning/execute",methods=['POST'])
def execute():
    data = json.loads(request.get_data())
    # step 1 create run tflearn
    jobId = data['jobId']
    try:
        t =threading.Thread(target=submit,kwargs=(data))
        t.start()
    except:
        result = {'status': 2,'msg':'任务提交失败!','jobId':jobId,'applicationId':""}
    else:
        result = {'status': 1,'msg':'任务提交成功!','jobId':jobId,'applicationId':""}
    print(result)
    return Response(json.dumps(result), mimetype='application/json')

def submit(**kwaggs):
    # = Client("http://172.16.81.22:50070")
    data = kwaggs
    pipe = DLPipeline(data)
    #try:
        #with client.write("/datasource.log",
        #              overwrite=False,append=True,encoding='utf-8') as writer:
            #writer.write("开始加载数据!\n")
    pipe.run()
        #res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})
    #except:
        #print('fail')
        # with client.write("/datasource.log",
        #                  overwrite=False,append=True,encoding='utf-8') as writer:
            # writer.write("数据加载失败!\n")
        #res = requests.post("http://httpbin.org/get",params={'a':'v1','b':'v2'})

if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0',debug=True)