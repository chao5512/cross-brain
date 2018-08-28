package com.bonc.pezy.action;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.pezy.config.HdfsConfig;
import com.bonc.pezy.config.PathConfig;
import com.bonc.pezy.constants.Constants;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.entity.Task;
import com.bonc.pezy.pyapi.JavaRequestPythonService;
import com.bonc.pezy.service.HdfsModel;
import com.bonc.pezy.service.JobService;
import com.bonc.pezy.service.TaskService;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.util.Upload;
import com.bonc.pezy.vo.JobQuery;
import com.bonc.pezy.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;

@Controller
@RequestMapping("Job")
@Api(value = "AI任务管理", description = "任务管理")

public class JobController extends HttpServlet{

    private final Logger logger = LoggerFactory.getLogger(JobController.class);
    @Autowired
    private JobService jobService;

    @Autowired
    private TaskService taskService;

    @Autowired
    HdfsConfig hdfsConfig;

    @Autowired
    HdfsModel hdfsModel;

    @Autowired
    private PathConfig pathConfig; //读取路径的配置文件类

    @ApiOperation(value = "下载模型文件", httpMethod = "POST")
    @RequestMapping(value = "/downModelFile", method = RequestMethod.POST)
    @ResponseBody
    public Result DownModelFile(@RequestParam(name = "jobId") String jobId,
            HttpServletResponse res) throws IOException {
        Result result =null;
        Job thisjob= jobService.findByJobId(jobId);
        logger.info(thisjob.getJobName());
//
        //获取模型地址  暂时默认
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        Properties properties = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/conf.properties");
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hdfsPath = properties.getProperty("hdfspath");

        //测试专用
        String fileNamePath ="hdfs://172.16.11.222:9000/machen/mmm/";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
//        res.setHeader("Content-Disposition", "attachment;filename="
//                + URLEncoder.encode(fileName, "UTF-8"));
        res.setHeader("Content-Type", "application/zip");

        //测试专用
        OutputStream out = new FileOutputStream("D:\\machen\\a.zip");
        //还是说不需要指定 目录
//        OutputStream out_02 = res.getOutputStream();
        BufferedOutputStream dest = new BufferedOutputStream(out);
        ZipOutputStream outZip = new ZipOutputStream(new BufferedOutputStream(dest));
        FileStatus[] fileStatuses = hdfsModel.allFiles(fileNamePath);
        FileSystem newfs =hdfsModel.fs(fileNamePath);
        //此处的 fileNamePath  最后的字符一定是  “ / ”
        int bytesRead;
        Path sourceFilePath;
        byte[] buffer = new byte[1024];
        try {
            for (int i = 0; i < fileStatuses.length; i++) {
                sourceFilePath = new Path(fileNamePath + fileStatuses[i].getPath().getName());
                FSDataInputStream in = newfs.open(sourceFilePath);

                //建立檔案的 entry
                ZipEntry entry = new ZipEntry(fileStatuses[i].getPath().toString());
                outZip.putNextEntry(entry);

                while ((bytesRead = in.read(buffer)) != -1) {
                    outZip.write(buffer, 0, bytesRead);
                    logger.info("下载文件");
                }
                in.close();
            }
            outZip.flush();
            outZip.close();
            logger.info("全部关闭");
            result = ResultUtil.success("niceok:"+"OK");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"error");
        }
    }

    @ApiOperation(value = "获取节点运行日志", httpMethod = "POST")
    @RequestMapping(value = "/qryTaskLog", method = RequestMethod.POST)
    @ResponseBody
    public Result qryLog(@RequestParam(name = "jobId") String jobId,
            @RequestParam(name = "taskId") String taskId, @RequestParam(name = "type") int type,
            HttpServletResponse res) throws IOException{
        Result result = null;
        String fileName = "aa.txt";  //模型文件

        switch(type){
            case 0:
                fileName = "datasource.log";
                break;
            case 1:
                fileName = "splitdata.log";
                break;
            case 2:
                fileName = "process.log";
                break;
            case 3:
                fileName = "pipeline.log";
                break;
            case 4:
                fileName = "evaluator.log";
                break;

        }
        Job thisjob= jobService.findByJobId(jobId);

        Properties properties = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/conf.properties");
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hdfsPath = properties.getProperty("hdfspath");
        logger.info(hdfsPath);

        //测试使用
        String fileNamePath ="hdfs://172.16.11.222:9000/machen/mmm/aa.txt";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        logger.info("开始读取文件");
        FSDataInputStream fsdis = hdfsModel.readHdfsFiles(fileNamePath);
        String ss =null;
        int line;
        byte[] buff = new byte[1024];
        try {
            line = fsdis.read(buff);
            while (line != -1) {
                ss = new String(buff, 0, buff.length,"UTF-8");
//                System.out.write(buff, 0, buff.length);
                line = fsdis.read(buff);
            }
            fsdis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"读取失败");
        } finally {
            if (fsdis != null) {
                try {
                    fsdis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result =ResultUtil.success(ss);
        return result;
    }



    @ApiOperation(value = "获取任务结果数据", httpMethod = "POST")
    @RequestMapping(value = "/qryResultData", method = RequestMethod.POST)
    @ResponseBody
    public Result qryResultData(@RequestParam(name = "jobId") String jobId,
            HttpServletResponse res) throws IOException{
        Result result = null;
        String fileName = "aa.txt";  //模型文件
        Job thisjob= jobService.findByJobId(jobId);
        String modelId = thisjob.getModelId();
        Long userId = thisjob.getOwner();

        Properties properties = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/conf.properties");
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hdfsPath = properties.getProperty("hdfspath");

        //测试专用
        String fileNamePath ="hdfs://172.16.11.222:9000/machen/mmm/aa.txt";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        FSDataInputStream fsdis = hdfsModel.readHdfsFiles(fileNamePath);

        String ss = null;
        //第一种
//        int line;
//        byte[] buff = new byte[1024];
        //第二种
        int len =0;
        int bufftemp =0;
        byte b[] = new byte[1024];
        try {
            while ((bufftemp = fsdis.read())!=-1){
                b[len] = (byte) bufftemp;
                len++;
            }
            //第一种
//            line = fsdis.read(buff);
//            while (line != -1) {
//                ss = new String(buff, 0, buff.length,"UTF-8");
//                line = fsdis.read(buff);
//            }
            fsdis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"读取文件失败");
        } finally {
            if (fsdis != null) {
                try {
                    fsdis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ss = new String(b,0,len);
        result =ResultUtil.success(ss);
        return result;
    }

    @ApiOperation(value = "调用模型(消息)", httpMethod = "POST")
    @RequestMapping(value = "/callModelByMessage", method = RequestMethod.POST)
    public Result callModelByMessage(@RequestParam(name = "jobId") String jobId,
            @RequestParam(name = "content") String content,
            HttpServletResponse res) {
        Result result = null;
        String fileName = "1.png";
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(new File("d://"
                    + fileName)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result = ResultUtil.success();
        return result;
    }

    @ApiOperation(value = "调用模型(文件)", httpMethod = "POST")
    @RequestMapping(value = "/callModelByFile", method = RequestMethod.POST)
    @ResponseBody
    public Result callModelByFile(@RequestParam(value = "file",required = false) MultipartFile file,
            @RequestParam(name = "jobId") String jobId) {
        //向指定的文件路径上传文件
        //获取路径
        String jobHdfsPath = pathConfig.getHadooppath();
        if(!jobHdfsPath.isEmpty()){
            if(!"".equals(jobId) && !"".equals(jobId.trim())){
                //jobId存在，并不为空格符
                jobId = jobId.trim();
                Job job = jobService.findByJobId(jobId);
                if(job!=null){
                    long userId = job.getOwner();
                    String modelId = job.getModelId();
                    StringBuffer path = new StringBuffer("");
                    path.append(jobHdfsPath);
                    path.append("/");
                    path.append(userId);
                    path.append("/");
                    path.append(modelId);
                    path.append("/");
                    path.append(jobId);
                    path.append("/");
                    path.append("data");
                    String remoteHdfsPath = path.toString();
                    try {
                        if (null != file){
                            hdfsModel.fileUpload(file,remoteHdfsPath);
                        }
                        //携带数据向python发送请求
                        Map<String,Object> param = new LinkedHashMap();
                        Map map = new LinkedHashMap();
                        String pipe = null;
                        List<Task> tasks = taskService.findByJobId(job.getJobId());
                        param.put("appName",job.getJobName());
                        param.put("jobId",job.getJobId());
                        param.put("modelId",job.getModelId());
                        param.put("userId", String.valueOf(job.getOwner()));
                        String url = Constants.PY_SERVER_PREDICT;
                        for(Task task:tasks){
                            Map<String,Object> tmp = new HashMap();
                            tmp.put("taskId",task.getTaskId());
                            tmp.put("type",task.getTaskType());
                            param.put(task.getTaskName(),JSON.parse(task.getParam()));
                            map.put(task.getTaskName(),tmp);
                        }
                        param.put("tasks",map);
                        pipe = JSONUtils.toJSONString(param);
                        if (!"".equals(pipe)){
                            JavaRequestPythonService jrps = new JavaRequestPythonService();
                            String result = jrps.requestPythonService(pipe,url);
                            JSONObject resultjson = JSON.parseObject(result);
                            String applicationid = resultjson.get("applicationId").toString();
                            int status = Integer.parseInt(resultjson.get("status").toString());
                            String message = resultjson.get("msg").toString();
                            if(status == 1){
                                return ResultUtil.success("applicationid:"+applicationid);
                            }else if(status == 2){
                                return ResultUtil.error(-1,message+" applicationId:"+applicationid);
                            }
                        }
                        return ResultUtil.error(-1,"python服务异常");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResultUtil.error(-1,"上传文件失败");
                    }
                }else {
                    return ResultUtil.error(-1,"job未找到");
                }

            }else{
                return ResultUtil.error(-1,"jobId错误");
            }
        }else{
            return ResultUtil.error(-1,"hdfs路径获取失败");
        }
    }

    @ApiOperation(value = "分页查询job，可附加各种条件(创建日期范围、名称模糊查询等)", httpMethod = "GET")
    @RequestMapping(value = "/findJobs", method = RequestMethod.GET)
    @ResponseBody
    public Result findJobs(@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size, @Validated JobQuery jobQuery) {
        Page<Job> jobs = jobService.findJobs(page, size, jobQuery);
        Result result = ResultUtil.success(jobs);
        return result;
    }

    @ApiOperation(value = "根据ModelID查询Job", httpMethod = "POST")
    @RequestMapping(value = "/findJobByModelId", method = RequestMethod.POST)
    @ResponseBody
    public Result findJobsByModelID(@RequestParam(value = "modelId") String modelId) {
        List<Job> jobs = jobService.findByModelId(modelId);
        Result result = ResultUtil.success(jobs.get(0));
        return result;
    }

    @ApiOperation(value = "更新任务状态", httpMethod = "POST")
    @RequestMapping(value = "/updatejob", method = RequestMethod.POST)
    @ResponseBody
    public void scanJob(@RequestParam("jobId") String jobId,
                        @RequestParam("taskId") List<String> taskIds,
                        @RequestParam("status") int status, HttpServletResponse respons){

        logger.info("jobid:"+jobId+"=====taskId:"+taskIds+"======status"+status);
        if(taskIds.size()==0){
            jobService.updateByJobId(status,jobId);
            logger.info("成功状态--更新任务状态成功！！！");

        }else {
            for (int i = 0; i < taskIds.size() - 1; i++) {
                taskService.updateByJobIdAndTaskId(status, jobId, taskIds.get(i));
                logger.info("失败状态--更新任务状态成功！！！");
            }
            jobService.updateByJobId(status,jobId);
            logger.info("失败状态--更新任务状态成功！！！");
        }

    }
}
