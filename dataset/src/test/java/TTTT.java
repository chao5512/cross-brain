import com.alibaba.fastjson.JSON;
import com.dataset.management.config.HdfsConfig;
import com.dataset.management.config.HiveConfig;
import com.dataset.management.entity.DataSetFile;
import net.bytebuddy.matcher.FilterableList;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TTTT {
    @Autowired
    HdfsConfig hdfsConfig;

    @Autowired
    HiveConfig hiveConfig;

    @Test
    public void url(){
        hdfsConfig.getHdfsUrl();
        System.out.println(hdfsConfig.getHdfsUrl());
        System.out.println(hiveConfig);
    }
//    @Test
//    public static void main(String args[]){
//        long t2=System.currentTimeMillis();
//        System.out.println(t2+"machen");
//        long time = System.currentTimeMillis();     //当前时间戳
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
//        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));   // 时间戳转换成时间
//        System.out.println(sd);

//        String columnName = "cc1";
//        String columnType = "string";
//        String columnDesc = "desc";
//        String aa = columnName +" "+columnType+" "+"comment "+" \""+columnDesc+"\",\n";
//        String columnName1 = "cc1";
//        String columnType1= "string";
//        String columnDes1 = "desc";
//        String aa1 = columnName1 +" "+columnType1+" "+"comment "+" \""+columnDes1+"\"";
//        String ftem = "/t";
//        String ltem = "/n";
//        String terStr = "row format delimited fields terminated by \""+ftem+"\""+" lines terminated by \""+ltem+"\"";
//        System.out.println(aa+aa1+")"+terStr);
//        List<String> dd = new ArrayList<>();
//        dd.add("ssss");
//        dd.add("2222");
//        System.out.println(dd.get(0));

//        String  ss = "我是马晨aa";
//        try {
//            byte[] bytes = ss.getBytes("UTF-8");
//            long time = 23;
//            String BB = bytes+"_"+time;
//            System.out.println(BB);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        List<String > ss = new ArrayList<>();
//        ss.add("aa");
//        System.out.println(ss.get(0));
//
//        Path path = new File("D:\\QQ\\config.xml.txd").toPath();
//        Path name = path.getFileName();
//        String nn = path.toFile().getName();
//        System.out.println(name);
//        System.out.println(nn);
//        System.out.println(GetFileSize(new File("D:\\QQ\\config.xml.txd")));

        /**
         config.xml.txd
         config.xml.txd
         855.30KB
         * */


//        DataSetFile dd =new DataSetFile();
//        dd.setFileSortType("asc");
//        String json = JSON.toJSONString(dd);
//        String json2 = JSON.toJSONString(dd);
//        String jj ="["+json+","+json2+"]";
//        System.out.println(jj);

//    }


    private static String GetFileSize(String Path){
        return GetFileSize(new File(Path));
    }


    private static String GetFileSize(File file){
        String size = "";
        if(file.exists() && file.isFile()){
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) +"GB";
            }
        }else if(file.exists() && file.isDirectory()){
            size = "";
        }else{
            size = "0BT";
        }
        return size;
    }
}
