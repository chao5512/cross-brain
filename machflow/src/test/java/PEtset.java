

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

public class PEtset {
    /**
     * 会默认按照Resources目录下的activiti.cfg.xml创建流程引擎
     */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    @Test
    public void test() {
        //以下两种方式选择一种创建引擎方式：1.配置写在程序里 2.读对应的配置文件
        //1
        testCreateProcessEngine();
        //2
//        testCreateProcessEngineByCfgXml();
//        deployProcess();
//        processDefinition_zip();
//        startProcess();
//        findProcess();
//        findLastVersionProcessDefinition();
//        findHistoryTask();
//        delteProcessDefinition();
//        queryTask();
//        handleTask();
//        isProcessEnd();
//        setVariables();
        getHisVariablebles();

    }

    /**
     * 测试activiti环境
     */
    @Test
    public void testCreateProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        cfg.setJdbcDriver("com.mysql.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://172.16.31.91/activiti_test?useUnicode=true&amp;characterEncoding=utf-8");
        cfg.setJdbcUsername("root");
        cfg.setJdbcPassword("123456");
        //配置建表策略
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();
    }

    /**
     * 根据配置文件activiti.cfg.xml创建ProcessEngine
     */
    @Test
    public void testCreateProcessEngineByCfgXml() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        processEngine = cfg.buildProcessEngine();
    }

    /**
     * 发布流程
     * RepositoryService
     */
    @Test
    public void deployProcess() {
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("machen.bpmn")
                .name("请假流程")
                .addClasspathResource("machen.png")
                .deploy();
        System.out.println(deploy.getId()+"    "+deploy.getName());
    }

    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void processDefinition_zip(){
        InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream("machen.zip");  //
        ZipInputStream zipInputStream=new ZipInputStream(inputStream);

        Deployment deployment=processEngine.getRepositoryService()
                .createDeployment()
                .name("流程名称")
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("部署Id："+deployment.getId());
        System.out.println("部署名称："+deployment.getName());
    }

    /**
     * 启动流程
     * RuntimeService
     */
    @Test
    public void startProcess() {

        ProcessInstance runtimeService = processEngine.getRuntimeService()
                .startProcessInstanceByKey("myProcess_1");
//                .startProcessInstanceByKey("myProcess_1");
        //可根据id、key、message启动流程
        System.out.println(runtimeService.getId()+"    "+
                runtimeService.getBusinessKey()+"   "+
                runtimeService.getActivityId()+"  "+
                runtimeService.getProcessDefinitionId());
    }

    /**
     *   对应表： ACT_RE_PROCDEF
     * */
    @Test
    public void findProcess(){
        /**
         * 单个查询
         * */
        ProcessDefinition process = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId("myProcess_1:1:904").singleResult();
        System.out.println("流程定义的名称:"+process.getName());
        System.out.println("-----------------------------------------");

        /**
         * 多个查询
         * */
        List<ProcessDefinition>  list =  processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByDeploymentId().asc()
                .list();

        if(list!=null && list.size()>0){
            for (ProcessDefinition processDefinition : list) {
                System.out.println("流程定义ID:"+processDefinition.getId());
                System.out.println("流程定义的名称:"+processDefinition.getName());
                System.out.println("流程定义的key:"+processDefinition.getKey());
                System.out.println("流程定义的版本:"+processDefinition.getVersion());
                System.out.println("资源名称bpmn文件:"+processDefinition.getResourceName());
                System.out.println("资源名称png文件:"+processDefinition.getDiagramResourceName());
                System.out.println("部署对象ID:"+processDefinition.getDeploymentId());
                System.out.println("---------------------------------------------------");
            }
        }
    }

    /**
     * 附加功能：查询最新版本的流程定义
     */
	@Test
	public void findLastVersionProcessDefinition(){
		List<ProcessDefinition> list=processEngine.getRepositoryService()
				                     .createProcessDefinitionQuery()
				                     .orderByProcessDefinitionVersion()  //使用流程定义的版本升序排列
				                     .asc()
				                     .list();
		Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
		if(list!=null && list.size()>0){
			for (ProcessDefinition pd : list) {
				map.put(pd.getKey(), pd);
			}
		}
		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
		if(pdList!=null && pdList.size()>0){
			for(ProcessDefinition pd:pdList){
				System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数
				System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
				System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
				System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
				System.out.println("资源名称bpmn文件:"+pd.getResourceName());
				System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
				System.out.println("部署对象ID："+pd.getDeploymentId());
				System.out.println("#########################################################");
			}
		}

	}
    /**
     * 删除流程定义
     */
    @Test
    public void delteProcessDefinition(){
        // 使用部署ID，完成删除
        String deploymentId="701"; //601
        /**
         * 不带级联的操作删除、
         * 只能删除没有启动的流程，如果流程启动，就会抛出异常
         */
        // processEngine.getRepositoryService().deleteDeployment(deploymentId);

        /**
         * 级联操作
         *  不管是流程是否启动、都可以删除
         */
        processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
        System.out.println("删除成功！");
    }

    /**
     * 查看任务
     * TaskService
     */
    @Test
    public void queryTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据assignee(代理人)查询任务
        String assignee = "manager";

        Task task1 = taskService.createTaskQuery()
                .taskId("1302").singleResult();

        System.out.println("单个查询       id");
        System.out.println("taskId:" + task1.getId() +
                ",taskName:" + task1.getName() +
                ",assignee:" + task1.getAssignee() +
                ",createTime:" + task1.getCreateTime());


        List<Task> tasks = taskService
                .createTaskQuery()
                /**查询条件（where部分）*/
                .taskAssignee(assignee)//指定个人任务查询，指定办理人
//                      .taskCandidateUser(candidateUser)//组任务的办理人查询
//                      .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                      .processInstanceId(processInstanceId)//使用流程实例ID查询
//                      .executionId(executionId)//使用执行对象ID查询
                /**排序*/
                .orderByTaskCreateTime().asc()//使用创建时间的升序排列
                /**返回结果集*/
//                      .singleResult()//返回惟一结果集
//                      .count()//返回结果集的数量
//                      .listPage(firstResult, maxResults);//分页查询
                .list();
        System.out.println("多个查询     assignee");
        for (Task task : tasks) {
            System.out.println("任务ID:"+task.getId());
            System.out.println("任务名称:"+task.getName());
            System.out.println("任务的创建时间:"+task.getCreateTime());
            System.out.println("任务的办理人:"+task.getAssignee());
            System.out.println("流程实例ID："+task.getProcessInstanceId());
            System.out.println("执行对象ID:"+task.getExecutionId());
            System.out.println("流程定义ID:"+task.getProcessDefinitionId());
            System.out.println("########################################################");
        }



    }

    /**
     * 办理任务
     */
    @Test
    public void handleTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据上一步生成的taskId执行任务

        String assignee = "emp";
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee).list();

        for (Task task : tasks) {
            String taskId = task.getId();
            task.setName("尝试改下名字");
            System.out.println("taskId:" + task.getId() +
                    ",taskName:" + task.getName() +
                    ",assignee:" + task.getAssignee() +
                    ",createTime:" + task.getCreateTime());

            taskService.complete(taskId);
        }
    }

    /**
     * 查询流程状态
     * */
    @Test
    public void isProcessEnd() {
        String processInstanceId = "1001";
        ProcessInstance pi = processEngine.getRuntimeService()// 表示正在执行的流程实例和执行对象
                .createProcessInstanceQuery()// 创建流程实例查询
                .processInstanceId(processInstanceId)// 使用流程实例ID查询
                .singleResult();
        if (pi == null) {
            System.out.println("流程已经结束");
        } else {
            System.out.println("流程没有结束");
        }
    }

    /**
     * 查询历史流程实例
     * */
    @Test
    public void findHistoryTask(){
        String taskAssignee = "emp";
        System.out.println("###########  多个  #####################");
        List<HistoricTaskInstance> list = processEngine.getHistoryService()//与历史数据（历史表）相关的Service
                .createHistoricTaskInstanceQuery()//创建历史任务实例查询
                .taskAssignee(taskAssignee)//指定历史任务的办理人
                .list();
        if(list!=null && list.size()>0){
            for(HistoricTaskInstance hti:list){
                System.out.println(hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());
            }
        }

        System.out.println("###########  单个  #####################");
        HistoricTaskInstance taskInstance = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskId("1104")
                .singleResult();
        System.out.println(taskInstance.getName()+"  "+taskInstance.getFormKey());
    }

    /**
     * ************************************ 流程变量 ******************************************
     * */
    @Test
    public void setVariables(){
        TaskService taskService = processEngine.getTaskService();

        String assigine = "manager";

        String processInstanceId = "1001";

        Task task = taskService.createTaskQuery()
                .taskAssignee(assigine)
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println(task.getId());
        taskService.setVariable(task.getId(),"know","yes");
        taskService.setVariableLocal(task.getId(),"datas",6);
        taskService.setVariable(task.getId(),"riqi",new Date());
        System.out.println(taskService.getVariable(task.getId(),"know"));


    }


    @Test
    public void getHisVariablebles(){
        List<HistoricVariableInstance> list =processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .variableName("know")
                .list();
        if(list!=null){
            for(HistoricVariableInstance variableInstance : list){
                System.out.println(variableInstance.getVariableName()+"    "+
                variableInstance.getValue());
            }
        }
    }



}
