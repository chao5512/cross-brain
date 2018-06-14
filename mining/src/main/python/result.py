class Result(object):
    def __init__(self, code=0, data=None, message="OK"):
        self.code = code
        self.data = data
        self.message = message
