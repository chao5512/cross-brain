import configparser
import sys

class Config():
    @staticmethod
    def loadConfig():
        conf = configparser.ConfigParser()
        conf.read(sys.path[0]+"/conf/conf.ini")
        return conf
