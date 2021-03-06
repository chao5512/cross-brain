#coding=utf-8
import json
from flask import Flask, Response,jsonify, request, abort
from dlpipeline import DLPipeline

import logging
from logging.config import fileConfig


import threading
import requests

import configparser

import ctypes
import inspect
import sys

from util.HDFSUtil import HDFSUtil

fileConfig(sys.path[0]+'/conf/logging.conf')
logger=logging.getLogger('pipline')

conf = configparser.ConfigParser()
conf.read(sys.path[0]+"/conf/conf.ini")

dlthreads = {}

app = Flask(__name__)

# 服务健康状况检查
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

# 深度学习任务执行服务
@app.route("/deeplearning/job/execute",methods=['POST'])
def execute():
    try:
        data = json.loads(request.get_data())
        job_id = data['jobId']
        logger.info("job_id:"+job_id)
        # step 1 create job thread
        # 创建深度学习任务线程
        t =threading.Thread(target=submit,kwargs=(data))
        # 启动
        t.start()
        # 实例变量保存线程信息
        dlthreads[job_id] = t
    except BaseException as e:
        logger.exception(e)
        result = {'status': 2,'msg':'任务提交失败!','jobId':job_id,'applicationId':""}
    else:
        result = {'status': 1,'msg':'任务提交成功!','jobId':job_id,'applicationId':""}
    return Response(json.dumps(result), mimetype='application/json')

# 深度学习模型调用服务
@app.route("/deeplearning/model/predict",methods=['POST'])
def predict():
    data = json.loads(request.get_data())
    pipe = DLPipeline(data)
    pipe.predict()
    result = {'status': 1}
    return Response(json.dumps(result), mimetype='application/json')

# 终止正在执行的深度学习任务
@app.route("/deeplearning/job/kill",methods=['POST'])
def kill():
    print('kill job!')
    data = json.loads(request.get_data())
    job_id = data['jobId']
    print(dlthreads[job_id])
    _stop_thread(dlthreads[job_id])#根据jobid停止执行中线程
    result = {'status': 1}
    return Response(json.dumps(result), mimetype='application/json')

# 任务执行
def submit(**kwaggs):
    logger.info("thread execute!")
    req_job_address = conf.get("Job","jobService")+"Job/updatejob"
    # 任务日志位置
    data = kwaggs
    pipe = DLPipeline(data)
    rootPath = conf.get("Job","jobHdfsPath")
    job_path = rootPath+str(data["userId"])+"/"+data["modelId"]+"/"+data["jobId"]+"/logs/process.log"
    logger.info("logfile location:"+job_path)
    HDFSUtil.append(job_path,"",False) #创建日志文件
    try:
        HDFSUtil.append(job_path,"开始执行深度学习任务!job_id:"+data["jobId"]+"\n",True)
        pipe.run()
        HDFSUtil.append(job_path,"任务执行成功!\n",True)
        HDFSUtil.append(job_path,"更新任务状态!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':"",'status':1})
        HDFSUtil.append(job_path,"完成任务状态更新!\n",True)
    except BaseException as e:
        logger.exception(e)
        HDFSUtil.append(job_path,"任务执行失败!\n",True)
        HDFSUtil.append(job_path,"更新任务状态!\n",True)
        res = requests.post(req_job_address,params={'jobId':data['jobId'],'taskId':"",'status':1})
        HDFSUtil.append(job_path,"完成任务状态更新!\n",True)

def _async_raise(tid, exctype):
    """raises the exception, performs cleanup if needed"""
    tid = ctypes.c_long(tid)
    print(tid)
    if not inspect.isclass(exctype):
        exctype = type(exctype)
    res = ctypes.pythonapi.PyThreadState_SetAsyncExc(tid, ctypes.py_object(exctype))
    if res == 0:
        raise ValueError("invalid thread id")
    elif res != 1:
        # """if it returns a number greater than one, you're in trouble,
        # and you should call it again with exc=NULL to revert the effect"""
        ctypes.pythonapi.PyThreadState_SetAsyncExc(tid, None)
        raise SystemError("PyThreadState_SetAsyncExc failed")

def _stop_thread(thread):
    _async_raise(thread.ident, SystemExit)


if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0',debug=True)