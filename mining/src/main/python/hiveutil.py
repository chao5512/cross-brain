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
        HiveClient.engine = create_engine('hive://182.92.82.3:10000/%s'%(database))
        return HiveClient.engine
    @staticmethod
    def queryByRowNums(tablename,rownums=1000,database="default"):
        engine = HiveClient.getEngine(database=database)
        sql="select * from %s limit %s"%(tablename,rownums)
        datas=pd.read_sql(sql, engine,coerce_float=False)
        return datas
    @staticmethod
    def queryForAll(tablename,database="default"):
        engine = HiveClient.getEngine(database=database)
        sql="select * from %s"%(tablename)
        datas=pd.read_sql(sql, engine)
        return datas
    @staticmethod
    def queryRowNumber(tablename,database="default"):
        engine = HiveClient.getEngine(database=database)
        sql="select count(*) from  %s"%(tablename)
        number=pd.read_sql(sql,engine)
        return number

if __name__ == '__main__':
    datas = HiveClient.queryByRowNums(tablename="titanic_orc",rownums=10)
    # number = HiveClient.queryRowNumber(tablename='studentno')
    # list=number.head().to_dict(orient='split')['data']
    # print(list)
