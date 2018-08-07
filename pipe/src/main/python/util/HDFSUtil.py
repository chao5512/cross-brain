from hdfs.client import Client
from config import Config

class HDFSUtil():
    @staticmethod
    def append(job_path,content):
        conf = Config.loadConfig()
        # 连接HDFS集群
        client = Client(conf.get('cluster','hadoopMaster'))
        with client.write(job_path,
                          overwrite=False,append=True,encoding='utf-8') as writer:
            writer.write(content)