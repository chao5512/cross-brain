package com.bonc.pezy.algorithmmodel.deeplearn;

import com.bonc.pezy.dataconfig.DataConfig;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;

/**
 * Created by 冯刚 on 2018/6/21.
 */
public class VggModel implements Serializable, ExecutionListener {

    private DataConfig dataConfig = DataConfig.getDataConfig();
    @Override
    public void notify(DelegateExecution execution) throws Exception {



    }
}
