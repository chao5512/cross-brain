package com.bonc.pezy.service;

import org.apache.hadoop.fs.FSDataInputStream;

import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.io.InputStream;

public interface HdfsModel {
    public InputStream downLoadFile(String hdfsUrl)throws IOException;

    public FSDataInputStream readHdfsFiles(String hdfsUrl)throws IOException;
}
