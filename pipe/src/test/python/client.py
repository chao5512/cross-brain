import requests

pipe = {'appName': 'testLD', 'filePath': 'E:/tmp/pythonData/sougou-train'}
# r = requests.post("http://localhost:3001/submit", data=pipe)
r = requests.post("http://localhost:3001/LRDemo", data=pipe)

print(r.text)