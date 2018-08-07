# -*- coding: utf-8 -*-
"""
Simple example using LSTM recurrent neural network to classify IMDB
sentiment dataset.

该数据集包含了电影的评论以及评论对应的情感分类的标签(0,1分类)。作者的初衷是希望该数据集会成为情绪分类的一个基准。


References:
    - Long Short Term Memory, Sepp Hochreiter & Jurgen Schmidhuber, Neural
    Computation 9(8): 1735-1780, 1997.
    - Andrew L. Maas, Raymond E. Daly, Peter T. Pham, Dan Huang, Andrew Y. Ng,
    and Christopher Potts. (2011). Learning Word Vectors for Sentiment
    Analysis. The 49th Annual Meeting of the Association for Computational
    Linguistics (ACL 2011).

Links:
    - http://deeplearning.cs.cmu.edu/pdfs/Hochreiter97_lstm.pdf
    - http://ai.stanford.edu/~amaas/data/sentiment/

"""
from __future__ import division, print_function, absolute_import

import tflearn
from tflearn.data_utils import to_categorical, pad_sequences
from tflearn.datasets import imdb

from deep.neuralnetwork import NeuralNetwork

import configparser

from hdfs.client import Client

class LSTM(NeuralNetwork):
    def loadImage(self):
        # IMDB Dataset loading: 数据下载/加载
        #train, test, _ = imdb.load_data(path='imdb.pkl', n_words=10000,
        #                                valid_portion=0.1)
        #1.1 数据加载：pickle格式
        data_file = "/Users/didi/deepinthought/tflearn/examples/nlp/imdb.pkl"
        train, test, _ = imdb.nlp_pkl_preloader(data_file, n_words=10000, valid_portion=0.1)

        #1.2 数据加载：txt格式
        # data_file 数据文件 纯文本 libsvm格式(更通用): [81, 77, 515, 3019, 6873, 84] 1
        # #vocab_file 字典文件 纯文本 一行一单词，行号是字典序号
        data_file = "/Users/didi/deepinthought/tflearn/examples/nlp/imdb.bow.feat"
        vocab_file = "/Users/didi/deepinthought/tflearn/examples/nlp/imdb.vocab"
        train, test, _ = imdb.nlp_txt_preloader(data_file, vocab_file, n_words=10000, valid_portion=0.1)

        trainX, trainY = train
        testX, testY = test

        # 2 Data preprocessing: 数据预处理
        # Sequence padding: 将 inputs 转化成矩阵形式，并用 0 补齐到最大维度，这样可以保持维度的一致性
        trainX = pad_sequences(trainX, maxlen=100, value=0.)
        testX = pad_sequences(testX, maxlen=100, value=0.)
        # Converting labels to binary vectors: 将lable转化为二进制向量
        trainY = to_categorical(trainY, nb_classes=2)
        testY = to_categorical(testY, nb_classes=2)

    def buildNetwork(self):
        # 3 Network building: 构建网络
        net = tflearn.input_data([None, 100])
        net = tflearn.embedding(net, input_dim=10000, output_dim=128)
        net = tflearn.lstm(net, 128, dropout=0.8)
        net = tflearn.fully_connected(net, 2, activation='softmax')
        net = tflearn.regression(net, optimizer='adam', learning_rate=0.001,
                                 loss='categorical_crossentropy')

    def createmodel(self,network):
        # 4 Training: 训练模型
        model = tflearn.DNN(network, tensorboard_verbose=0)

    def train(self,model,X,Y):
        model.fit(trainX, trainY, validation_set=(testX, testY), show_metric=True,
                  batch_size=32)

    def save(self,model):
        # 5 Save: 保存模型
        model.save('your-task-model-retrained-by-lstm')

    def run(self):
        conf = configparser.ConfigParser()
        conf.read("conf.ini")
        client = Client(conf.get('cluster','hadoopMaster'))
        imgProcessPath = "/imgProcess/task.log"
        trainPath = "/train/task.log"
        try:
            with client.write(imgProcessPath,
                              overwrite=False,append=True,encoding='utf-8') as writer:
                writer.write("开始预处理数据!\n")
            self.printParams()
            X,Y = self.loadImage()
        except:
            with client.write(imgProcessPath,
                              overwrite=False,append=True,encoding='utf-8') as writer:
                writer.write("数据处理失败!\n")
        try:
            with client.write(trainPath,
                              overwrite=False,append=True,encoding='utf-8') as writer:
                writer.write("开始训练模型!\n")
            network = self.buildNetwork()
            model = self.createmodel(network)
            self.train(model,X,Y)
            self.save(model)
        except:
            with client.write(trainPath,
                              overwrite=False,append=True,encoding='utf-8') as writer:
                writer.write("模型训练失败!\n")
        self.predict(network)