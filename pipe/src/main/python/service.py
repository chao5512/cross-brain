import json
from flask import Flask, Response,jsonify, request, abort
import json
from mlpipeline import MLPipeline

app = Flask(__name__)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')
@app.route("/submit",methods=['POST'])
def submit():
    print(request.form.get('appName'))
    appName = request.form.get('appName')
    pipe = MLPipeline(appName)
    pipe.create()
    result = {'appName': appName}
    return Response(json.dumps(result), mimetype='application/json')
app.run(port=3001, host='0.0.0.0',debug=True)