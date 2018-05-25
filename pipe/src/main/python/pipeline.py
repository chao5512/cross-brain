#!/usr/bin/python3

class Pipeline:
    def __init__(self,appName):
        self._appName = appName


    @property
    def appName(self):
        return self._appName

    @appName.setter
    def appName(self,appName):
        self._appName = appName
