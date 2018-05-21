import net.bytebuddy.matcher.FilterableList;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TTTT {
    public static void main(String[] args) {
//        long t2=System.currentTimeMillis();
//        System.out.println(t2+"machen");
//        long time = System.currentTimeMillis();     //当前时间戳
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
//        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));   // 时间戳转换成时间
//        System.out.println(sd);

        String columnName = "cc1";
        String columnType = "string";
        String columnDesc = "desc";
        String aa = columnName +" "+columnType+" "+"comment "+" \""+columnDesc+"\",\n";
        String columnName1 = "cc1";
        String columnType1= "string";
        String columnDes1 = "desc";
        String aa1 = columnName1 +" "+columnType1+" "+"comment "+" \""+columnDes1+"\"";
        String ftem = "/t";
        String ltem = "/n";
        String terStr = "row format delimited fields terminated by \""+ftem+"\""+" lines terminated by \""+ltem+"\"";
        System.out.println(aa+aa1+")"+terStr);
//        List<String> dd = new ArrayList<>();
//        dd.add("ssss");
//        dd.add("2222");
//        System.out.println(dd.get(0));
    }
}
