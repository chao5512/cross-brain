# -*- coding: utf-8 -*-
#!/usr/bin/python3

import requests
import json

def testML():
    mlpipe = {"appName": "testLD", "filePath": "hdfs://172.16.31.231:9000/data","isSplitSample":1,"trainRatio":0.6,"evaluator":"MulticlassClassificationEvaluator","originalStages": {"Tokenizer": {"inputCol": "content","outputCol": "words"},"HashingTF": {"inputCol": "words","outputCol": "features"},"LogisticRegression": {"maxIter": 10,"regParam": 0.001}}}
    r = requests.post("http://localhost:3001/LRDemo", data=json.dumps(mlpipe))
    print(r.text)

def testDL():
    dlpipe = {
        "run_id": "testDL",
        "networktype":"alexnet",
        "n_epoch":"1",
        "batch_size":"1",
        "num_class":"2",
        "optimizer":"adam",
        "loss":"categorical_crossentropy",
        "checkpoint_path":"vgg-finetuning",
        "tensorboard_dir":"./logs",
        "model_path":"/Users/mengxin/Desktop/vgg/vgg_model",
        "train_set":"/Users/mengxin/Desktop/vgg/data/list.txt",
        "shape":[None, 224, 224, 3],
        "learning_rate":"0.001",
        "validation_set":"0.1",
        "snapshot_step":"200"
    }
    r = requests.post("http://localhost:3002/deeplearning/execute", data=json.dumps(dlpipe))
    print(r.text)

if __name__ == '__main__':
    testDL()