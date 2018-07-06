from sqlalchemy import *
import pandas as pd

class StringTolist:
    def __init__(self):
        super(StringTolist, self).__init__()
    @staticmethod
    def toList(files):
        files = files.replace('[', '').replace(']', '')
        files = files.split(',')
        files_List = []
        for i in range(len(files)):
            files[i] = files[i].replace("\"", '')
            files_List.append(files[i])
        return files_List
if __name__ == '__main__':
    files="[\"age\",\"survived\"]"
    datas = StringTolist.toList(files)
    print(datas)
    # number = HiveClient.queryRowNumber(tablename='studentno')
    # list=number.head().to_dict(orient='split')['data']
    # print(list)
