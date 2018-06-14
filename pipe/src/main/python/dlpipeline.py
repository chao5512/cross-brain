#!/usr/bin/python3
from pipeline import Pipe

import tensorflow as tf
import configparser

class DLPipeline(Pipe):
    def __init__(self,appName):
        self.conf = configparser.ConfigParser()
        self.conf.read("conf.ini")
        super(DLPipeline,self).__init__(appName)

        return tf.estimator.Estimator


    """运行"""
    def run(self,train,isgpu,loop,weight,biases):
        init=tf.initialize_all_variables()
        with tf.Session as sess:
            try:
                if isgpu:
                    with tf.device("/gpu:1"):
                        for step in range(loop): #循环训练400次
                            sess.run(train)  #使用训练器根据训练结构进行训练
                            if step%(loop/10)==0:  #每20次打印一次训练结果
                                print(step,sess.run(weight),sess.run(biases)) #训练次数，A值，B值
                            sess.run(init)
                else:
                    for step in range(loop): #循环训练400次
                        sess.run(train)  #使用训练器根据训练结构进行训练
                        if step%(loop/10)==0:  #每20次打印一次训练结果
                            print(step,sess.run(weight),sess.run(biases)) #训练次数，A值，B值
                        sess.run(init)
            except tf.errors.OutOfRangeError:
                print('done!')
            finally:
                sess.close()

    def evaluation(logits, labels):
        with tf.variable_scope("accuracy") as scope:
            correct = tf.nn.in_top_k(logits, labels, 1)
            correct = tf.cast(correct, tf.float16)
            accuracy = tf.reduce_mean(correct)
            tf.summary.scalar(scope.name + "accuracy", accuracy)
        return accuracy

    def losses(logits, labels):
        with tf.variable_scope("loss") as scope:
            cross_entropy = tf.nn.sparse_softmax_cross_entropy_with_logits(logits=logits,
                                                                           labels=labels, name="xentropy_per_example")
            loss = tf.reduce_mean(cross_entropy, name="loss")
            tf.summary.scalar(scope.name + "loss", loss)
            return loss