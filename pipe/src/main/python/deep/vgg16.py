# -*- coding: utf-8 -*

import tflearn

from tflearn.data_utils import image_preloader
from tflearn.data_preprocessing import ImagePreprocessing

from deep.neuralnetwork import NeuralNetwork

class Vgg16(NeuralNetwork):
    train_set = "/Users/mengxin/Desktop/vgg/data/list.txt"

    model_path = "/Users/mengxin/Desktop/vgg/vgg_model"

    shape = [None, 224, 224, 3]

    checkpoint_path = 'vgg-finetuning'

    tensorboard_dir = './logs'

    snapshot_step = 200

    validation_set = 0.1

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  checkpoint_path,tensorboard_dir,model_path,train_set,shape,learning_rate,
                  snapshot_step,validation_set):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path)
        self.train_set = train_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.snapshot_step = snapshot_step
        self.checkpoint_path = checkpoint_path
        self.tensorboard_dir = tensorboard_dir
        self.validation_set = validation_set

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
        print('snapshot_step:',self.snapshot_step)

    def loadImage(self):
        X, Y = image_preloader(self.train_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                               categorical_labels=True, normalize=False,
                               files_extension=['.jpg', '.png'], filter_channel=True)
        return X,Y

    def imageProcess(self):
        img_prep = ImagePreprocessing()
        img_prep.add_featurewise_zero_center(mean=[123.68, 116.779, 103.939],
                                             per_channel=True)
        return img_prep

    def buildNetwork(self,img_prep):
        input = tflearn.input_data(shape=self.shape, name='input',
                                   data_preprocessing=img_prep)
        x = tflearn.conv_2d(input, 64, 3, activation='relu', scope='conv1_1')
        x = tflearn.conv_2d(x, 64, 3, activation='relu', scope='conv1_2')
        x = tflearn.max_pool_2d(x, 2, strides=2, name='maxpool1')

        x = tflearn.conv_2d(x, 128, 3, activation='relu', scope='conv2_1')
        x = tflearn.conv_2d(x, 128, 3, activation='relu', scope='conv2_2')
        x = tflearn.max_pool_2d(x, 2, strides=2, name='maxpool2')

        x = tflearn.conv_2d(x, 256, 3, activation='relu', scope='conv3_1')
        x = tflearn.conv_2d(x, 256, 3, activation='relu', scope='conv3_2')
        x = tflearn.conv_2d(x, 256, 3, activation='relu', scope='conv3_3')
        x = tflearn.max_pool_2d(x, 2, strides=2, name='maxpool3')

        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv4_1')
        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv4_2')
        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv4_3')
        x = tflearn.max_pool_2d(x, 2, strides=2, name='maxpool4')

        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv5_1')
        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv5_2')
        x = tflearn.conv_2d(x, 512, 3, activation='relu', scope='conv5_3')
        x = tflearn.max_pool_2d(x, 2, strides=2, name='maxpool5')

        x = tflearn.fully_connected(x, 4096, activation='relu', scope='fc6')
        x = tflearn.dropout(x, 0.5, name='dropout1')

        x = tflearn.fully_connected(x, 4096, activation='relu', scope='fc7')
        x = tflearn.dropout(x, 0.5, name='dropout2')

        x = tflearn.fully_connected(x, self.num_class, activation='softmax', scope='fc8',
                                restore=False)

        return x

    def createModel(self,softmax):
        regression = tflearn.regression(softmax, optimizer=self.optimizer,
                                loss=self.loss,
                                learning_rate=self.learning_rate, restore=False)
        model = tflearn.DNN(regression, checkpoint_path=self.checkpoint_path,
                            max_checkpoints=3, tensorboard_verbose=2,
                            tensorboard_dir=self.tensorboard_dir)
        return model

    def train(self,model,X,Y):
        model.fit(X, Y, n_epoch=self.n_epoch, validation_set=self.validation_set, shuffle=True,
                  show_metric=True, batch_size=self.batch_size, snapshot_epoch=False,
                  snapshot_step=self.snapshot_step, run_id=self.run_id)

    def save(self,model):
        model.save(self.model_path)

    def run(self):
        self.printParams()
        X,Y = self.loadImage()
        softmax = self.buildNetwork(self.imageProcess())
        model = self.createModel(softmax)
        self.train(model,X,Y)
        self.save(model)

