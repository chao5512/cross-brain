import requests
import json



pipe = {"tableName":"150_titanic","number":2}
r = requests.post("http://localhost:3002/head", data=json.dumps(pipe))
print(r.text)