from PIL import Image

class NeuralNetwork(object):
    run_id = ""

    n_epoch = 1

    batch_size = 1

    num_class = 1

    optimizer = 'adam'

    loss = 'categorical_crossentropy'

    model_path = ""

    def setJobParams(self,run_id,
                  n_epoch,batch_size,num_class,optimizer,loss,model_path):
        self.run_id = run_id
        self.n_epoch = n_epoch
        self.batch_size = batch_size
        self.num_class = num_class
        self.optimizer = optimizer
        self.loss = loss
        self.model_path = model_path

    def predict(self,network):
        img = Image.open("/home/hadoop")
        model = self.createmodel(network)
        model.load("")
        prediction = model.predict(img)
        print(prediction)
