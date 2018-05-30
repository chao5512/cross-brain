import requests

pipe = {'appName': 'test', 'inputPath': '/weather'}
r = requests.post("http://localhost:3001/submit", data=pipe)

print(r.text)