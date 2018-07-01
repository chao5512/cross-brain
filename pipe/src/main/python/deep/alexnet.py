from __future__ import division, print_function, absolute_import

import tflearn
from tflearn.layers.core import input_data, dropout, fully_connected
from tflearn.layers.conv import conv_2d, max_pool_2d
from tflearn.layers.normalization import local_response_normalization
from tflearn.layers.estimator import regression
from tflearn.data_utils import image_preloader

from deep.neuralnetwork import NeuralNetwork

class Alexnet(NeuralNetwork):
    train_set = "/Users/mengxin/Desktop/vgg/data/list.txt"

    shape = [None, 224, 224, 3]

    learning_rate = 0.001

    validation_set = 0.1

    snapshot_step = 200

    checkpoint_path = 'vgg-finetuning'

    tensorboard_dir = './logs'

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  checkpoint_path,tensorboard_dir,model_path,train_set,shape,learning_rate,
                  validation_set,snapshot_step):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path)
        self.train_set = train_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.validation_set = validation_set
        self.snapshot_step = snapshot_step
        self.checkpoint_path = checkpoint_path
        self.tensorboard_dir = tensorboard_dir

    def printParams(self):
        print('run_id:',self.run_id)
        print('n_epoch:',self.n_epoch)
        print('batch_size:',self.batch_size)
        print('optimizer:',self.optimizer)
        print('loss:',self.loss)
        print('checkpoint_path:',self.checkpoint_path)
        print('tensorboard_dir:',self.tensorboard_dir)
        print('model_path:',self.model_path)
        print('train_set:',self.train_set)
        print('shape:',self.shape)
        print('learning_rate:',self.learning_rate)
        print('validation_set:',self.validation_set)
        print('snapshot_step:',self.snapshot_step)


    def loadImage(self):
        if(len(self.shape)>2):
            X, Y = image_preloader(self.train_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                               categorical_labels=True, normalize=False,
                               files_extension=['.jpg', '.png'], filter_channel=True)
        return X,Y

    def buildNetwork(self):
        # Building 'AlexNet'
        network = input_data(shape=self.shape)
        network = conv_2d(network, 96, 11, strides=4, activation='relu')
        network = max_pool_2d(network, 3, strides=2)
        network = local_response_normalization(network)
        network = conv_2d(network, 256, 5, activation='relu')
        network = max_pool_2d(network, 3, strides=2)
        network = local_response_normalization(network)
        network = conv_2d(network, 384, 3, activation='relu')
        network = conv_2d(network, 384, 3, activation='relu')
        network = conv_2d(network, 256, 3, activation='relu')
        network = max_pool_2d(network, 3, strides=2)
        network = local_response_normalization(network)
        network = fully_connected(network, 4096, activation='tanh')
        network = dropout(network, 0.5)
        network = fully_connected(network, 4096, activation='tanh')
        network = dropout(network, 0.5)
        network = fully_connected(network, self.num_class, activation='softmax')
        network = regression(network, optimizer=self.optimizer,
                             loss=self.loss,
                             learning_rate=self.learning_rate)
        return network

    def createmodel(self,network):
        model = tflearn.DNN(network, checkpoint_path=self.checkpoint_path,
                            max_checkpoints=1, tensorboard_verbose=2)
        return model

    def train(self,model,X,Y):
        model.fit(X, Y, n_epoch=self.n_epoch, validation_set=self.validation_set, shuffle=True,
                  show_metric=True, batch_size=self.batch_size, snapshot_step=self.snapshot_step,
                  snapshot_epoch=False, run_id=self.run_id)

    def save(self,model):
        model.save(self.model_path)

    def run(self):
        print('alexnet')
        self.printParams()
        X,Y = self.loadImage()
        network = self.buildNetwork()
        model = self.createmodel(network)
        self.train(model,X,Y)
        self.save(model)

        self.predict(network)
