import requests
import json

pipe = {"appName": "testLD", "filePath": "/Users/hanwei/Desktop/tmp/sougou-train","originalStages": {"Tokenizer": {"inputCol": "content","outputCol": "words"},"HashingTF": {"inputCol": "words","outputCol": "features"},"LogisticRegression": {"maxIter": 10,"regParam": 0.001}}}
r = requests.post("http://localhost:3001/LRDemo", data=json.dumps(pipe))
print(r.text)