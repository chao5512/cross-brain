package com.bonc.pezy.action;

import com.bonc.pezy.config.HdfsConfig;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.service.HdfsModel;
import com.bonc.pezy.service.JobService;
import com.bonc.pezy.util.ResultUtil;
import com.bonc.pezy.vo.JobQuery;
import com.bonc.pezy.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.*;
import java.net.URLEncoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
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
import sun.nio.ch.IOUtil;

@Controller
@RequestMapping("Job")
@Api(value = "AI任务管理", description = "任务管理")

public class JobController extends HttpServlet{

    private final Logger logger = LoggerFactory.getLogger(JobController.class);
    @Autowired
    private JobService jobService;

    @Autowired
    HdfsConfig hdfsConfig;

    @Autowired
    HdfsModel hdfsModel;

    @ApiOperation(value = "下载模型文件", httpMethod = "POST")
    @RequestMapping(value = "/downModelFile", method = RequestMethod.POST)
    public Result DownModelFile(@RequestParam(name = "jobid") String jobid,
            HttpServletResponse res) throws IOException {
        Result result = null;
        String fileName = "aa.txt";  //模型文件
        Job thisjob= jobService.findByJobId(jobid);
        String modelId = thisjob.getModelId();
        Long userId = thisjob.getOwner();

        //获取模型地址  暂时默认  http://182.92.82.3:8020/DATASYSTEM/1234/Models/MDL88888/马晨.txt
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"DATASYSTEM/"+userId+"/"+"Models/"+modelId+"/"+fileName;
        //TEST
//        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"machen/mmm/aa.txt";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        byte[] buff = new byte[1024];
        InputStream inputStream = hdfsModel.downLoadFile(fileNamePath);
        OutputStream out = new FileOutputStream("D:\\machen\\aa.txt");
        IOUtils.copyBytes(inputStream,out,1024);
        out.close();
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            //数据读取的操作
            int i = inputStream.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = inputStream.read(buff);
            }
            inputStream.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result = ResultUtil.success();
        return result;
    }

    @ApiOperation(value = "获取节点运行日志", httpMethod = "GET")
    @RequestMapping(value = "/qryLog", method = RequestMethod.GET)
    public Result qryLog(@RequestParam(name = "jobId") String jobId,
            @RequestParam(name = "nodeId") String nodeId,
            HttpServletResponse res) throws IOException{
        Result result = null;
        String fileName = "aa.txt";  //模型文件
        Job thisjob= jobService.findByJobId(jobId);
        String modelId = thisjob.getModelId();
        Long userId = thisjob.getOwner();

        //获取模型地址  暂时默认  http://182.92.82.3:8020/DATASYSTEM/1234/Models/MDL88888/马晨.txt
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"DATASYSTEM/"+userId+"/"+"Models/"+modelId+"/"+fileName;
        //TEST  测试专用
//        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"machen/mmm/aa.txt";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        FSDataInputStream fsdis = hdfsModel.readHdfsFiles(fileNamePath);
        OutputStream out = null;
        int line;
        byte[] buff = new byte[1024];
        try {
            out = res.getOutputStream();
            line = fsdis.read(buff);
            while (line != -1) {
                out.write(buff, 0, buff.length);
                System.out.write(buff, 0, buff.length);
                out.flush();
                line = fsdis.read(buff);
            }
            fsdis.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fsdis != null) {
                try {
                    fsdis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result =ResultUtil.success();
        return result;
    }

    @ApiOperation(value = "获取任务结果数据", httpMethod = "GET")
    @RequestMapping(value = "/qryResultData", method = RequestMethod.GET)
    public Result qryResultData(@RequestParam(name = "jobId") String jobId,
            HttpServletResponse res) throws IOException{
        Result result = null;
        String fileName = "aa.txt";  //模型文件
        Job thisjob= jobService.findByJobId(jobId);
        String modelId = thisjob.getModelId();
        Long userId = thisjob.getOwner();

        //获取模型地址  暂时默认  http://182.92.82.3:8020/DATASYSTEM/1234/Models/MDL88888/马晨.txt
        String hdfsUrl = hdfsConfig.getHdfsUrl();
        Long hdfsPort = hdfsConfig.getHdfsProt();
        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"DATASYSTEM/"+userId+"/"+"Models/"+modelId+"/"+fileName;
        //TEST
//        String fileNamePath =hdfsUrl+":"+hdfsPort+ "/"+"machen/mmm/aa.txt";
        logger.info(fileNamePath);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        FSDataInputStream fsdis = hdfsModel.readHdfsFiles(fileNamePath);
        OutputStream out = null;
        int line;
        byte[] buff = new byte[1024];
        try {
            out = res.getOutputStream();
            line = fsdis.read(buff);
            while (line != -1) {
                out.write(buff, 0, buff.length);
                out.flush();
                line = fsdis.read(buff);
            }
            fsdis.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fsdis != null) {
                try {
                    fsdis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result =ResultUtil.success();
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
    public Result callModelByFile(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "jobId") String jobId) {
        return ResultUtil.success();
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

    @ApiOperation(value = "更新任务状态", httpMethod = "GET")
    @RequestMapping(value = "/updatejob", method = RequestMethod.GET)
    @ResponseBody
    public void scanJob(@RequestParam("jobId") String jobId,
                        @RequestParam("status") short status,HttpServletResponse respons){
        jobService.updateByJobId(status,jobId);
    }
}
