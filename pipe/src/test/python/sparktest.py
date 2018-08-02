#coding=utf-8
#!/usr/bin/python3

from mlpipeline import MLPipeline

pipe = MLPipeline('test')
pipe.create()
pipe.spark.sql("show tables").show()