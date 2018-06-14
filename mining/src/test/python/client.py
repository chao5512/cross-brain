import requests
import json

pipe = {"appName": "testLD", "filePath": "hdfs://172.16.31.231:9000/data","isSplitSample":1,"trainRatio":0.6,"evaluator":"MulticlassClassificationEvaluator","originalStages": {"Tokenizer": {"inputCol": "content","outputCol": "words"},"HashingTF": {"inputCol": "words","outputCol": "features"},"LogisticRegression": {"maxIter": 10,"regParam": 0.001}}}
r = requests.post("http://localhost:3002/hist", data=json.dumps(pipe))
print(r.text)