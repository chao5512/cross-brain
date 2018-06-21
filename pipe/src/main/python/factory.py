from deep.alexnet import Alexnet
from deep.cnn import Convnet
from deep.dnn import Dnn
from deep.googlenet import GoogleNet
from deep.residual_network import Residual_Network
from deep.vgg16 import Vgg16


class Factory():

    @classmethod
    def network(self,networktype):
        if(cmp(str(networktype),"alexnet")==0):
            return Alexnet()
        if(cmp(str(networktype),"cnn")==0):
            return Convnet()
        if(cmp(str(networktype),"dnn")==0):
            return Dnn()
        if(cmp(str(networktype),"googlenet")==0):
            return GoogleNet()
        if(cmp(str(networktype),"residual")==0):
            return Residual_Network()
        if(cmp(str(networktype),"vgg16")==0):
            return Vgg16()

