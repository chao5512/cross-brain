package com.bonc.pezy.util;

import com.bonc.pezy.constants.Constants;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 冯刚 on 2018/6/28.
 */
public class BpmnToXml {

    public void createXMLStream(BpmnModel bpmnModel,String filename){

        BpmnXMLConverter bpmnXMLConverter=new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String filepath = Constants.RESOURCE_PATH+filename;
        File file = new File(filepath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(convertToXML);
        } catch (Exception e) {

        }

    }
}
