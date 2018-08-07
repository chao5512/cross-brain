# -*- coding: utf-8 -*-

from hdfs.client import Client
from util.config import Config

class HDFSUtil():
    @staticmethod
    def append(job_path,content,append):
        conf = Config.loadConfig()
        # 连接HDFS集群
        client = Client(conf.get('cluster','hadoopMaster'))
        with client.write(job_path,
                          overwrite=False,append=append,encoding='utf-8') as writer:
            writer.write(content)

if __name__ == '__main__':
    HDFSUtil.append("/test.log","",False)
    HDFSUtil.append("/test.log","123",True)