package com.bonc.pezy.algorithmmodel.classification;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/6/28.
 */
public class LRUserTask implements Serializable, TaskListener{
    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("=================");

    }
}
