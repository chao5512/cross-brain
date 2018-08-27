package com.dataset.management.aop.aspect;

import com.dataset.management.common.ResultUtil;
import com.dataset.management.util.JedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @ClassName PreventRepetitionAspect
 * @Description Aspect用于防止重复提交
 * @Auther: 王培文
 * @Date: 2018/8/1 
 * @Version 1.0
 **/
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class PreventRepetitionAspect {

    @Autowired
    private JedisUtils jedisUtils;

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_TOKEN_FLAG = "tokenFlag";

//    切入点
    @Pointcut("execution(public * com.dataset.management.controller.HiveTableController.*(..))")
    public void repeatLock(){

    }

    /**
     * around 环绕通知
     * @throws Throwable
     */
    @Around(value = "repeatLock() && @annotation(com.dataset.management.aop.annotation.PreventRepetitionAnnotation)")
    public Object excute(ProceedingJoinPoint joinPoint) throws Throwable{
        try {
            Object result = null;
            Object[] args = joinPoint.getArgs();
//            注意这里固定最后一个参数是为防止重复提交而设置的token
            int length = args.length;
            String functionName = joinPoint.getSignature().getName();
            System.out.println("方法名："+functionName);
            if("toUuid".equals(functionName)){
//                        这是生成token方法
                result = generate(joinPoint, PARAM_TOKEN_FLAG);
            }else if("createOrUpdateTable".equals(functionName)){
//               获取token信息，该接口最后一个参数为token
                String token = (String) args[length-1];
                result = validation(joinPoint, PARAM_TOKEN_FLAG, token);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("执行防止重复提交功能AOP失败，原因：" + e.getMessage());
            return ResultUtil.error(-1,"执行防止重复提交功能AOP失败，原因：" + e.getMessage());
        }
    }

    public Object generate(ProceedingJoinPoint joinPoint,String tokenFlag) throws Throwable {
        String uuid = UUID.randomUUID().toString();
        joinPoint.proceed();
        return ResultUtil.success(uuid);
    }

    public Object validation(ProceedingJoinPoint joinPoint,String tokenFlag, String token) throws Throwable {
        String requestFlag = token;
        System.out.println("token: "+token);
        //redis加锁
        boolean lock = jedisUtils.tryGetDistributedLock(tokenFlag + requestFlag, requestFlag, 60000);
        System.out.println("lock:" + lock + "," + Thread.currentThread().getName());
        if(lock){
            //加锁成功
            //执行方法
            System.out.println("加锁成功。。。");
            Object funcResult = joinPoint.proceed();
            //方法执行完之后进行解锁
            jedisUtils.releaseDistributedLock(tokenFlag + requestFlag, requestFlag);
            System.out.println("最终结果："+funcResult);
            return funcResult;
        }else{
            //锁已存在
            return ResultUtil.error(-1,"不能重复提交！");
        }
    }


}
