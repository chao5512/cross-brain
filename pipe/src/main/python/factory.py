from deep.alexnet import Alexnet

class Factory():

    @classmethod
    def network(self,networktype):
        if(cmp(str(networktype),"alexnet")==0):
            return Alexnet()

