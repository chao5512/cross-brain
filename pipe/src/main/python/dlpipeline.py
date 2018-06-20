# -*- coding: utf-8 -*-
#!/usr/bin/python3
from pipeline import Pipe

import tflearn
from tflearn.data_preprocessing import ImagePreprocessing

from tflearn.data_utils import image_preloader

from deep.vgg16 import Vgg16
from deep.residual_network import Residual_network
from deep.dnn import dnn
from deep.googlenet import googlenet
from deep.alexnet import Alexnet
from deep.cnn import cnn

from factory import Factory
from deep.neuralnetwork import NeuralNetwork

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
        network.run()
