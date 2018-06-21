from __future__ import division, print_function, absolute_import

import tflearn
import tflearn.data_utils as du

from deep.neuralnetwork import NeuralNetwork

class Dnn(NeuralNetwork):
    train_set = "/Users/mengxin/Desktop/vgg/data/list.txt"
    test_set = "/Users/mengxin/Desktop/vgg/data/list.txt"

    model_path = "/Users/mengxin/Desktop/vgg/vgg_model"

    shape = [None, 28, 28, 3 ]

    learning_rate = 0.001

    lr_decay=0.96

    decay_step=1000

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  model_path,train_set,test_set,shape,learning_rate,lr_decay,decay_step):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path)
        self.train_set = train_set
        self.test_set = test_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.lr_decay = lr_decay
        self.decay_step = decay_step

    def printParams(self):
        print('run_id:',self.run_id)
        print('n_epoch:',self.n_epoch)
        print('batch_size:',self.batch_size)
        print('num_class:',self.num_class)
        print('optimizer:',self.optimizer)
        print('loss:',self.loss)
        print('model_path:',self.model_path)
        print('train_set:',self.train_set)
        print('test_set:',self.test_set)
        print('shape:',self.shape)
        print('learning_rate:',self.learning_rate)
        print('lr_decay:',self.lr_decay)
        print('decay_step:',self.decay_step)

    def loadImage(self):
        X, Y = du.image_preloader(self.train_set, image_shape=(self.shape[1],self.shape[2]), mode='file',
                                  categorical_labels=True, normalize=False,
                                  files_extension=['.jpg', '.png'], filter_channel=True)
        testX, testY = du.image_preloader(self.test_set, image_shape=(self.shape[1],self.shape[2]), mode='file',
                                          categorical_labels=True, normalize=False,
                                          files_extension=['.jpg', '.png'], filter_channel=True)
        return X,Y,testX,testY

    def buildNetwork(self):
        # Building deep neural network
        input_layer = tflearn.input_data(shape=self.shape)
        dense1 = tflearn.fully_connected(input_layer, 64, activation='tanh',
                                         regularizer='L2', weight_decay=0.001)
        dropout1 = tflearn.dropout(dense1, 0.8)
        dense2 = tflearn.fully_connected(dropout1, 64, activation='tanh',
                                         regularizer='L2', weight_decay=0.001)
        dropout2 = tflearn.dropout(dense2, 0.8)
        softmax = tflearn.fully_connected(dropout2, self.num_class, activation='softmax')

        # Regression using SGD with learning rate decay and Top-3 accuracy
        sgd = tflearn.SGD(learning_rate=self.learning_rate, lr_decay=self.lr_decay, decay_step=self.decay_step)
        top_k = tflearn.metrics.Top_k(3)
        net = tflearn.regression(softmax, optimizer=sgd, metric=top_k,
                                 loss=self.loss)

        return net

    def createModel(self,net):
        model = tflearn.DNN(net, tensorboard_verbose=0)
        return model

    def train(self,model,X,Y,testX,testY):
        model.fit(X, Y, n_epoch=self.n_epoch, validation_set=(testX, testY),
                  show_metric=True, run_id=self.run_id)

    def save(self,model):
        model.save(self.model_path)

    def run(self):
        self.printParams()
        X,Y,testX,testY = self.loadImage()
        net = self.buildNetwork()
        model = self.createModel(net)
        self.train(model,X,Y,testX,testY)
        self.save(model)