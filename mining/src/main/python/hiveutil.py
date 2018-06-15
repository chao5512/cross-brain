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
        datas=pd.read_sql(sql, engine).head(10)
        return datas


if __name__ == '__main__':
    results = HiveClient.queryForAll(tablename="studentno")
    print(results)
    # conection = HiveClient.getConection(database="default")
    # sql="select * from %s limit 10"
    # with conection.cursor() as cursor:
    #     cursor.execute(sql % "studentno")
    #     results=cursor.fetchall()
    #     for result in results:
    #         print(result)
        # time.sleep(1)
        # time.sleep(1000)
