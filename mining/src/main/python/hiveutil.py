from sqlalchemy import *
import pandas as pd

class HiveClient:
    engine=None
    def __init__(self):
        super(HiveClient, self).__init__()
    @staticmethod
    def getEngine(database):
        """
        创建 engine
        """
        HiveClient.engine = create_engine('hive://172.16.31.91:10000/%s'%(database))
        return HiveClient.engine
    @staticmethod
    def queryForAll(tablename,database="default"):
        engine = HiveClient.getEngine(database=database)
        sql="select * from %s"%(tablename)
        datas=pd.read_sql(sql, engine)
        return datas


if __name__ == '__main__':
    results = HiveClient.queryForAll(tablename="studentno")
    print(results)
