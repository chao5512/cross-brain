import json
import numpy as np
from result import Result
class JsonCustomEncoder(json.JSONEncoder):
    def default(self, obj):
        d = {}
        if isinstance(obj, np.int64):
            # obj = obj.astype('int32')
            obj = float(obj)
            return obj
        elif isinstance(obj, Result):
            item = obj.data;
            for key,value in item.items():
                if key == "content":
                    for listInner in value:
                        for tmp in listInner:
                            if isinstance(tmp,float):
                                if np.isnan(tmp):
                                    # 当为nan类型时，替换为“”
                                    listInner[listInner.index(tmp)] = "";
            return obj.__dict__;
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