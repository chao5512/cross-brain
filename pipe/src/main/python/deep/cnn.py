from __future__ import division, print_function, absolute_import

import tflearn
from tflearn.layers.core import input_data, dropout, fully_connected
from tflearn.layers.conv import conv_2d, max_pool_2d
from tflearn.layers.normalization import local_response_normalization
from tflearn.layers.estimator import regression

import tflearn.data_utils as du

from deep.neuralnetwork import NeuralNetwork

class Convnet(NeuralNetwork):
    train_set = "/Users/mengxin/Desktop/vgg/data/list.txt"
    test_set = "/Users/mengxin/Desktop/vgg/data/list.txt"

    model_path = "/Users/mengxin/Desktop/vgg/vgg_model"

    shape = [None, 224, 224, 3]

    learning_rate = 0.01

    snapshot_step = 100

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  model_path,train_set,test_set,shape,learning_rate,
                  snapshot_step):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path)
        self.train_set = train_set
        self.test_set = test_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.snapshot_step = snapshot_step

    def printParams(self):
        print('run_id:',self.run_id)
        print('n_epoch:',self.n_epoch)
        print('batch_size:',self.batch_size)
        print('optimizer:',self.optimizer)
        print('loss:',self.loss)
        print('model_path:',self.model_path)
        print('train_set:',self.train_set)
        print('test_set:',self.test_set)
        print('shape:',self.shape)
        print('learning_rate:',self.learning_rate)
        print('snapshot_step:',self.snapshot_step)

    def loadImage(self):
        X, Y = du.image_preloader(self.train_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                                  categorical_labels=True, normalize=False,
                                  files_extension=['.jpg', '.png'], filter_channel=True)
        testX, testY = du.image_preloader(self.test_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                                          categorical_labels=True, normalize=False,
                                          files_extension=['.jpg', '.png'], filter_channel=True)
        return X,Y,testX,testY

    def buildNetwork(self):
        network = input_data(shape=self.shape, name='input')
        network = conv_2d(network, 32, 3, activation='relu', regularizer="L2")
        network = max_pool_2d(network, 2)
        network = local_response_normalization(network)
        network = conv_2d(network, 64, 3, activation='relu', regularizer="L2")
        network = max_pool_2d(network, 2)
        network = local_response_normalization(network)
        network = fully_connected(network, 128, activation='tanh')
        network = dropout(network, 0.8)
        network = fully_connected(network, 256, activation='tanh')
        network = dropout(network, 0.8)
        network = fully_connected(network, self.num_class, activation='softmax')
        network = regression(network, optimizer=self.optimizer, learning_rate=self.learning_rate,
                     loss=self.loss, name='target')

        return network

    def createModel(self,network):
        model = tflearn.DNN(network, tensorboard_verbose=0)
        return model

    def train(self,model,X,Y,testX,testY):
        model.fit({'input': X}, {'target': Y}, n_epoch=self.n_epoch,
                  validation_set=({'input': testX}, {'target': testY}),
                  snapshot_step=self.snapshot_step, show_metric=True, run_id=self.run_id)

    def save(self,model):
        model.save(self.model_path)

    def run(self):
        self.printParams()
        X,Y,testX,testY = self.loadImage()
        network = self.buildNetwork()
        model = self.createModel(network)
        self.train(model,X,Y,testX,testY)
        self.save(model)