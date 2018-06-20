import json
from flask import Flask, Response,jsonify, request, abort
import json
from dlpipeline import DLPipeline

app = Flask(__name__)
@app.route("/health")
def health():
    result = {'status': 'UP'}
    return Response(json.dumps(result), mimetype='application/json')

@app.route("/deeplearning/execute",methods=['POST'])
def execute():
    data = json.loads(request.get_data())
    pipe = DLPipeline(data)
    pipe.run()
    result = {'result': 'sucess'}
    return Response(json.dumps(result), mimetype='application/json')

if __name__ == '__main__':
    app.run(port=3002, host='0.0.0.0',debug=True)