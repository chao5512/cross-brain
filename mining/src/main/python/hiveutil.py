from sqlalchemy import *
import pandas as pd
import configparser
import sys


class HiveClient:
    engine = None

    def __init__(self):
        super(HiveClient, self).__init__()

    @staticmethod
    def getEngine(database):
        """
        从配置文件中读取配置
        """
        cf = configparser.ConfigParser()
        cf.read(sys.path[0]+"/../resources/ini.cfg")
        """
        创建 engine
        """
        url = "hive://{}:{}/%s"
        url = url.format(cf.get("hive","db_host"),cf.get("hive","port"))
        HiveClient.engine = create_engine(url % (database))
        return HiveClient.engine

    @staticmethod
    def queryByRowNums(tablename, rownums=1000, database="default"):
        engine = HiveClient.getEngine(database=database)
        sql = "select * from %s limit %s" % (tablename, rownums)
        datas = pd.read_sql(sql, engine, coerce_float=False)
        return datas

    @staticmethod
    def queryForAll(tablename, database="default"):
        engine = HiveClient.getEngine(database=database)
        sql = "select * from %s" % (tablename)
        datas = pd.read_sql(sql, engine)
        return datas

    @staticmethod
    def queryRowNumber(tablename, database="default"):
        engine = HiveClient.getEngine(database=database)
        sql = "select count(*) count from  %s" % (tablename)
        number = pd.read_sql(sql, engine)
        return number


if __name__ == '__main__':
    print(HiveClient.queryRowNumber(tablename="150_titanic"))
    # cf = configparser.ConfigParser()
    # print(cf.read("../resources/ini.cfg"))
    # print(cf.get("hive","db_host"))
    # print(cf.get("hive","port"))
    # print(cf.get("hive","user"))
    # str="hive://{}:{}/%s"
    # str1=str.format(cf.get("hive","db_host"),cf.get("hive","port"))
    # print(str1)
# datas = HiveClient.queryByRowNums(tablename="150_titanic", rownums=10)
    #     print(HiveClient.queryRowNumber(tablename="150_titanic"))
    # number = HiveClient.queryRowNumber(tablename='studentno')
    # list=number.head().to_dict(orient='split')['data']
    # print(list)
