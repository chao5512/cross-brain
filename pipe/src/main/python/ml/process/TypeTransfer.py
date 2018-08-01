class TypeTransfer:

    def __init__(self,outputCol,inputCol,castType):
        self.outputCol = outputCol
        self.inputCol = inputCol
        self.castType = castType

    def transform(self):
        self.df.withColumn(self.outputCol, self.df[self.inputCol].cast(self.castType))