import configparser
import os

class Config():
    @staticmethod
    def loadConfig():
        conf = configparser.ConfigParser()
        conf.read(os.getcwd()+"../conf.ini")
        return conf
