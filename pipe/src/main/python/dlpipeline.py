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

from factory import Factory

class DLPipeline():
    jsonData = '{}'

    def __init__(self,jsonData):
        self.jsonData = jsonData

    def run(self):
        network = Factory.network(str(self.jsonData['networktype']))
        if(isinstance(network,Alexnet)):
            print('execute alexnet!')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['tensorboard_dir']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Convnet)):
            print('execute convnet')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Dnn)):
            print('execute dnn')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),float(self.jsonData['lr_decay']),
                              float(self.jsonData['decay_step']))
        elif(isinstance(network,GoogleNet)):
            print('execute GoogleNet')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),float(self.jsonData['validation_set']),
                              int(self.jsonData['snapshot_step']))
        elif(isinstance(network,Residual_Network)):
            print('execute residual')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['model_path']),str(self.jsonData['train_set']),
                              str(self.jsonData['test_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),str(self.jsonData['checkpoint_path']))
        elif(isinstance(network,Vgg16)):
            print('execute vgg16')
            network.setParams(str(self.jsonData['run_id']),int(self.jsonData['n_epoch']),
                              int(self.jsonData['batch_size']),int(self.jsonData['num_class']),
                              str(self.jsonData['optimizer']),str(self.jsonData['loss']),
                              str(self.jsonData['checkpoint_path']),str(self.jsonData['tensorboard_dir']),
                              str(self.jsonData['model_path']),
                              str(self.jsonData['train_set']),list(self.jsonData['shape']),
                              float(self.jsonData['learning_rate']),
                              int(self.jsonData['snapshot_step']),float(self.jsonData['validation_set']))
        network.run()
