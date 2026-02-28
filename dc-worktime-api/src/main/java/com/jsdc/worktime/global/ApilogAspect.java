//package com.jsdc.worktime.global;
//
//import com.alibaba.fastjson.JSON;
//import com.jsdc.worktime.utils.AjaxJson;
//import com.jsdc.worktime.utils.SysLogUtil;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
///**
// * API全局日志, Controller 层切面
// *
// * @author kong
// */
//@Aspect
//@Component
//public class ApilogAspect {
//
//	/**
//	 * 定义AOP签名 --> 项目代码(所有class名成带有Controller字符的)
//	 */
//	@Pointcut("execution(* com.jsdc..*Api*.get*(..))")
//    public void webLogProject(){}
//
//    /**
//     * 环绕日志
//     * @param pjp
//     * @return
//     * @throws Throwable
//     */
//    @Around("webLogProject()")
//    public Object surround(ProceedingJoinPoint pjp) throws Throwable {
//        try {
//        	// 1、执行
//            Object obj =  pjp.proceed();
//            String args = JSON.toJSONString(pjp.getArgs());
//            // 2、解析返回结果
//            // 如果是 AjaxJson
//            if(obj instanceof AjaxJson){
//            	SysLogUtil.endRequest(args,(AjaxJson)obj);
//            }
//            // 如果是 String
//            else if (obj instanceof String) {
//                SysLogUtil.endRequest(args,AjaxJson.get(901, String.valueOf(obj)));
//            }
//            // 如果都不是
//            else {
//                SysLogUtil.endRequest(args,AjaxJson.get(902, String.valueOf(obj)));
//            }
//            return obj;
//        } catch (Throwable e) {
//        	throw e;
//        }
//    }
//
//}