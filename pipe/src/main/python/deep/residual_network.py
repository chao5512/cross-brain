import tflearn
import tflearn.data_utils as du

from deep.neuralnetwork import NeuralNetwork

class Residual_network(NeuralNetwork):
    train_set = "/Users/mengxin/Desktop/vgg/data/list.txt"
    test_set = "/Users/mengxin/Desktop/vgg/data/list.txt"

    model_path = "/Users/mengxin/Desktop/vgg/vgg_model"

    shape = [None, 224, 224, 3]

    checkpoint_path = 'vgg-finetuning'

    def setParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,
                  model_path,train_set,test_set,shape,learning_rate,
                  checkpoint_path):
        self.setJobParams(run_id,n_epoch,batch_size,num_class,optimizer,loss,model_path)
        self.train_set = train_set
        self.test_set = test_set
        self.shape = shape
        self.learning_rate = learning_rate
        self.checkpoint_path = checkpoint_path

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
        print('checkpoint_path:',self.checkpoint_path)

    def loadImage(self):
        X, Y = du.image_preloader(self.train_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                          categorical_labels=True, normalize=False,
                          files_extension=['.jpg', '.png'], filter_channel=True)
        testX, testY = du.image_preloader(self.test_set, image_shape=(self.shape[1], self.shape[2]), mode='file',
                                  categorical_labels=True, normalize=False,
                                  files_extension=['.jpg', '.png'], filter_channel=True)
        #X = X.reshape([-1, 28, 28, 1])
        #testX = testX.reshape([-1, 28, 28, 1])
        X, mean = du.featurewise_zero_center(X)
        testX = du.featurewise_zero_center(testX, mean)
        return X,Y,testX,testY

    def buildNetwork(self):
        # Building Residual Network
        net = tflearn.input_data(shape=self.shape)
        net = tflearn.conv_2d(net, 64, 3, activation='relu', bias=False)
        # Residual blocks
        net = tflearn.residual_bottleneck(net, 3, 16, 64)
        net = tflearn.residual_bottleneck(net, 1, 32, 128, downsample=True)
        net = tflearn.residual_bottleneck(net, 2, 32, 128)
        net = tflearn.residual_bottleneck(net, 1, 64, 256, downsample=True)
        net = tflearn.residual_bottleneck(net, 2, 64, 256)
        net = tflearn.batch_normalization(net)
        net = tflearn.activation(net, 'relu')
        net = tflearn.global_avg_pool(net)
        # Regression
        net = tflearn.fully_connected(net, self.num_class, activation='softmax')
        net = tflearn.regression(net, optimizer=self.optimizer,
                                      loss=self.loss,
                                      learning_rate=self.learning_rate)
        return net

    def createModel(self,net):
        model = tflearn.DNN(net, checkpoint_path=self.checkpoint_path,
                            max_checkpoints=10, tensorboard_verbose=0)
        return model

    def train(self,model,X,Y,testX,testY):
        model.fit(X, Y, n_epoch=self.n_epoch, validation_set=(testX, testY),
                  show_metric=True, batch_size=self.batch_size, run_id=self.run_id)

    def save(self,model):
        model.save(self.model_path)

    def run(self):
        self.printParams()
        X,Y,testX,testY = self.loadImage()
        net = self.buildNetwork()
        model = self.createModel(net)
        self.train(model,X,Y,testX,testY)
        self.save(model)
