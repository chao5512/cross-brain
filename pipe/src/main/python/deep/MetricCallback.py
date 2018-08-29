from tflearn.callbacks import Callback

from util.HDFSUtil import HDFSUtil

class MetricCallback(Callback):
    def __init__(self,evaluator_path):
        self.evaluator_path = evaluator_path
        """ Note: We are free to define our init function however we please. """
        # Store a validation accuracy threshold, which we can compare against
        # the current validation accuracy at, say, each epoch, each batch step, etc.

    def on_train_end(self, training_state):
        """
        Furthermore, tflearn will then immediately call this method after we terminate training,
        (or when training ends regardless). This would be a good time to store any additional
        information that tflearn doesn't store already.
        """
        print("record result "+self.evaluator_path)
        HDFSUtil.append(self.evaluator_path,"",False) #创建日志文件
        HDFSUtil.append(self.evaluator_path,"{'accuracy':"+training_state.acc_value,True)