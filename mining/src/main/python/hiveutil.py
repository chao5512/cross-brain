from pyhive import hive
from configparser import ConfigParser

class HiveClient:
    conn=None
    def __init__(self):
        super(HiveClient, self).__init__()
    @staticmethod
    def getConection(database):
        """
        创建 hive server2 连接
        """
        try:
            config = ConfigParser()
            config.read("../resources/ini.cfg")
        except IOError:
            print("连接hive异常")
        db_host=config.get("hive","db_host")
        port=config.get("hive","port")
        user=config.get("hive","user")
        HiveClient.conn = hive.Connection(host=db_host,
                                    port=port,
                                    username=user,
                                    database=database)
        return HiveClient.conn

if __name__ == '__main__':
    conection = HiveClient.getConection(database="default")
    sql="select * from studentno limit 10"
    with conection.cursor() as cursor:
        cursor.execute(sql)
        results=cursor.fetchall()
        for result in results:
            print(result)
        # time.sleep(1)
        # time.sleep(1000)
