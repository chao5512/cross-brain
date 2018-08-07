import configparser

class Config():
    @staticmethod
    def loadConfig():
        conf = configparser.ConfigParser()
        conf.read("conf.ini")
