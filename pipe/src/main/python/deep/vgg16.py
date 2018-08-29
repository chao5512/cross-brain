# -*- coding: utf-8 -*

import tflearn

from tflearn.data_utils import image_preloader
from tflearn.data_preprocessing import ImagePreprocessing
from PIL import Image

from deep.neuralnetwork import NeuralNetwork

import sys
import logging
from logging.config import fileConfig

import numpy as np

from deep.MetricCallback import MetricCallback

fileConfig(sys.path[0]+'/conf/logging.conf')
logger=logging.getLogger('pipline')

class Vgg16(NeuralNetwork):
    train_set = ""

    model_path = ""

    shape = [None, 224, 224, 3]

    checkpoint_path = ''

    tensorboard_dir = ''

    snapshot_step = 200

    validation_set = 0.1

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  checkpoint_path,tensorboard_dir,model_path,train_set,shape,learning_rate,
                  snapshot_step,validation_set,evaluator_path):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path,evaluator_path)
        self.train_set = train_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.snapshot_step = snapshot_step
        self.checkpoint_path = checkpoint_path
        self.tensorboard_dir = tensorboard_dir
        self.validation_set = validation_set

    def printParams(self):
        logger.info('run_id:',self.run_id)
        logger.info('n_epoch:',self.n_epoch)
        logger.info('batch_size:',self.batch_size)
        logger.info('num_class:',self.num_class)
        logger.info('optimizer:',self.optimizer)
        logger.info('loss:',self.loss)
        logger.info('checkpoint_path:',self.checkpoint_path)
        logger.info('tensorboard_dir:',self.tensorboard_dir)
        logger.info('model_path:',self.model_path)
        logger.info('train_set:',self.train_set)
        logger.info('shape:',self.shape)
        logger.info('shape1:',self.shape[1])
        logger.info('shape2:',self.shape[2])
        logger.info('learning_rate:',self.learning_rate)
        logger.info('snapshot_step:',self.snapshot_step)
        logger.info('validation_set:',self.validation_set)

    def loadImage(self):
        logger.info(self.model_path)
        try:
            X, Y = image_preloader(self.train_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                               categorical_labels=True, normalize=False,
                               files_extension=['.jpg', '.png'], filter_channel=True)
        except BaseException as e:
            logger.exception(e)
        return X,Y

    def imageProcess(self):
        img_prep = ImagePreprocessing()
        img_prep.add_featurewise_zero_center(mean=[123.68, 116.779, 103.939],
                                             per_channel=True)
        return img_prep

    def buildNetwork(self,img_prep):
        try:
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
        except BaseException as e:
            print(e)
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
        try:
            early_stopping_cb = MetricCallback(self.evaluator_path)
            model.fit(X, Y, n_epoch=self.n_epoch, validation_set=self.validation_set, shuffle=True,
                  show_metric=True, batch_size=self.batch_size, snapshot_epoch=False,
                  snapshot_step=self.snapshot_step, run_id=self.run_id,callbacks=early_stopping_cb)
        except BaseException as e:
            print(e)

    def save(self,model):
        model.save(self.model_path)

    def predict(self):
        try:
            print('预测数据')
            logger.info("prediction")
            img1 = Image.open("/Users/mengxin/Desktop/vgg/data/image_0001.jpg")
            img1 = img1.resize((224, 224), Image.ANTIALIAS)
            img2 = Image.open("/Users/mengxin/Desktop/vgg/data/image_0002.jpg")
            img2 = img2.resize((224, 224), Image.ANTIALIAS)
            print('图像转换')
            imgarray1 = np.asarray(img1, dtype="float32")
            imgarray2 = np.asarray(img2, dtype="float32")
            imgs = []
            imgs.append(imgarray1)
            #imgs.append(imgarray2)
            print('转换完成')
            model = self.createModel(self.buildNetwork(self.imageProcess()))
            model.load("/Users/mengxin/Desktop/vgg/vgg_model/vgg16")
            prediction = model.predict(imgs)
            print(prediction)
        except BaseException as e:
            print(e.args)
            logger.exception(e)

    def run(self):
        print('params')
        self.printParams()
        print('loadImage')
        X,Y = self.loadImage()
        print('network')
        softmax = self.buildNetwork(self.imageProcess())
        print('model')
        model = self.createModel(softmax)
        print('train')
        self.train(model,X,Y)
        self.save(model)
        print('end')


