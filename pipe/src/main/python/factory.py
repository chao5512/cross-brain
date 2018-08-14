from deep.alexnet import Alexnet
from deep.cnn import Convnet
from deep.dnn import Dnn
from deep.googlenet import GoogleNet
from deep.residual_network import Residual_Network
from deep.vgg16 import Vgg16
from deep.lstm import LSTM

class Factory():

    @classmethod
    def network(self,networktype):
        if(str(networktype)=="alexnet"):
            return Alexnet()
        if(str(networktype)=="cnn"):
            return Convnet()
        if(str(networktype)=="dnn"):
            return Dnn()
        if(str(networktype)=="googlenet"):
            return GoogleNet()
        if(str(networktype)=="residual"):
            return Residual_Network()
        if(str(networktype)=="vgg16"):
            return Vgg16()
        if(str(networktype)=="lstm"):
            return LSTM()

