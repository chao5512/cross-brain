# -*- coding: utf-8 -*-
#!/usr/bin/python3
from pipeline import Pipe

import tflearn
from tflearn.data_preprocessing import ImagePreprocessing

from tflearn.data_utils import image_preloader

from deep.alexnet import Alexnet
from deep.cnn import Convnet
from deep.dnn import Dnn
from deep.googlenet import GoogleNet
from deep.residual_network import Residual_Network
from deep.vgg16 import Vgg16
from deep.lstm import LSTM

from factory import Factory

import sys
import logging
from logging.config import fileConfig

fileConfig(sys.path[0]+'/conf/logging.conf')
logger=logging.getLogger('pipline')

class DLPipeline():
    jsonData = '{}'

    def __init__(self,jsonData):
        self.jsonData = jsonData

    def predict(self):
        logger.info("running")
        network = Factory.network(str(self.jsonData['vgg16']['networktype']))
        logger.info(self.jsonData['vgg16']['networktype'])
        spape = self.jsonData['picture']['shape'].split(',')
        s = []
        for i in spape:
            if(i=='None'):
                s.append(None)
                continue
            s.append(int(i))

        if(isinstance(network,Alexnet)):
            logger.info('execute alexnet!')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['tensorboard_dir']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Convnet)):
            logger.info('execute convnet')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Dnn)):
            logger.info('execute dnn')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['lr_decay']),
                              float(self.jsonData['decay_step']))
        elif(isinstance(network,GoogleNet)):
            logger.info('execute GoogleNet')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Residual_Network)):
            logger.info('execute residual')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),str(self.jsonData['checkpoint_path']))
        elif(isinstance(network,Vgg16)):
            logger.info('execute vgg16')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['tensorboard_dir']),
                              str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(s),
                              float(self.jsonData['learning_rate']),
                              int(self.jsonData['snapshot_step']),float(self.jsonData['validation_set']))
        elif(isinstance(network,LSTM)):
            logger.info('execute lstm')
            network.setParams()
        network.predict()

    def run(self):
        logger.info("running")
        network = Factory.network(str(self.jsonData['vgg16']['networktype']))
        logger.info(self.jsonData['vgg16']['networktype'])
        spape = self.jsonData['picture']['shape'].split(',')
        s = []
        for i in spape:
            if(i=='None'):
                s.append(None)
                continue
            s.append(int(i))

        if(isinstance(network,Alexnet)):
            logger.info('execute alexnet!')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['tensorboard_dir']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Convnet)):
            logger.info('execute convnet')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Dnn)):
            logger.info('execute dnn')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['lr_decay']),
                              float(self.jsonData['decay_step']))
        elif(isinstance(network,GoogleNet)):
            logger.info('execute GoogleNet')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(s),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Residual_Network)):
            logger.info('execute residual')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(s),
                              float(self.jsonData['learning_rate']),str(self.jsonData['checkpoint_path']))
        elif(isinstance(network,Vgg16)):
            logger.info('execute vgg16')
            print('vgg16')
            network.setParams(str(self.jsonData['jobId']),int(self.jsonData['vgg16']['n_epoch']),
                              int(self.jsonData['vgg16']['batch_size']),int(self.jsonData['vgg16']['num_class']),
                              str(self.jsonData['vgg16']['optimizer']),str(self.jsonData['vgg16']['loss']),
                              str(self.jsonData['vgg16']['checkpoint_path']),str(self.jsonData['vgg16']['tensorboard_dir']),
                              str(self.jsonData['vgg16']['model_path']),
                              str(self.jsonData['picture']['train_set']),list(s),
                              float(self.jsonData['vgg16']['learning_rate']),
                              int(self.jsonData['vgg16']['snapshot_step']),float(self.jsonData['vgg16']['validation_set']))
        elif(isinstance(network,LSTM)):
            logger.info('execute lstm')
            network.setParams()

        network.run()
