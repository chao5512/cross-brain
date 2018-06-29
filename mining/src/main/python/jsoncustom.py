import json
import numpy as np

class JsonCustomEncoder(json.JSONEncoder):
    def default(self, obj):
        d = {}
        if isinstance(obj, np.int64):
            # obj = obj.astype('int32')
            obj = float(obj)
            return obj
        else:
            d['__class__'] = obj.__class__.__name__
            d['__module__'] = obj.__module__
            d.update(obj.__dict__)
            return d

# d = {}
# d['__class__'] = obj.__class__.__name__
# d['__module__'] = obj.__module__
# d.update(obj.__dict__)
# return d