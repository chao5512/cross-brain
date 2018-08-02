package com.dataset.management.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName PreventRepetitionAnnotation
 * @Description 用户防止表单重复提交注解
 * @Auther: 王培文
 * @Date: 2018/8/1
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.TYPE})
public @interface PreventRepetitionAnnotation {

}
