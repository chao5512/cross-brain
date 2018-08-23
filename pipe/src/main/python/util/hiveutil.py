from pyhive import hive
import configparser
from util.config import Config

class HiveUtil:
    @staticmethod
    def queryBySql(sql, hasResult=False):
        conf = Config.loadConfig()

        conn = hive.Connection(host=conf.get('hive','host'), port=conf.get('hive','port'), username=conf.get('hive','user'),
                               database='default')
        cursor = conn.cursor()
        cursor.execute(sql)
        if(hasResult):
            result = cursor.fetchall()
        else:
            result = None
        cursor.close()
        return result
