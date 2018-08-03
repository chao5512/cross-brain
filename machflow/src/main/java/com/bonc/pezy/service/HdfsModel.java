package com.bonc.pezy.service;

import java.io.IOException;
import java.io.InputStream;

public interface HdfsModel {
    public InputStream downLoadFile(String hdfsUrl)throws IOException;
}
