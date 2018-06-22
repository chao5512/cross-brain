import json
from flask import Flask, Response,jsonify, request, abort
from dlpipeline import DLPipeline

app = Flask(__name__)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

@app.route("/deeplearning/execute",methods=['POST'])
def execute():
    print(request.get_data())
    data = json.loads(request.get_data())
    print(data)
    pipe = DLPipeline(data)
    pipe.run()
    result = {'result': 'sucess'}
    return Response(json.dumps(result), mimetype='application/json')

if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0',debug=True)