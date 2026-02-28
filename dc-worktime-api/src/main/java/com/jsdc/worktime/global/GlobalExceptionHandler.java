package com.jsdc.worktime.global;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.core.date.DateUtil;
import com.jsdc.worktime.config.SystemObject;
import com.jsdc.worktime.utils.AjaxError;
import com.jsdc.worktime.utils.AjaxJson;
import com.jsdc.worktime.utils.SysLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Date;
import java.sql.SQLException;

/**
 * 全局异常处理
 *
 * <p> @ControllerAdvice 可指定包前缀，例如：(basePackages = "com.pj.controller.admin")
 * @author kong
 *
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)  // 确保最先执行
@Slf4j
public class GlobalExceptionHandler {

	/** 全局异常拦截  */
	@ExceptionHandler
	public AjaxJson handlerException(Exception e) {

		// 打印堆栈，以供调试
		e.printStackTrace();
		log.error("Error：", e);
    	// 记录日志信息
    	AjaxJson aj = null;
		Throwable e2 = e.getCause();

		// ------------- 判断异常类型，提供个性化提示信息

    	// 如果是未登录异常
		if(e instanceof NotLoginException){
			aj = AjaxJson.getNotLogin();
		}
		// 如果是权限异常
		else if(e instanceof NotPermissionException) {
			NotPermissionException ee = (NotPermissionException) e;
			aj = AjaxJson.getNotJur("无此权限：" + ee.getCode());
		}
		// 如果是AjaxError，则获取其具体code码
		else if(e instanceof AjaxError) {
			AjaxError ee = (AjaxError) e;
			aj = AjaxJson.get(ee.getCode(), ee.getMessage());
		}
		// 如果是SQLException，并且指定了hideSql，则只返回sql error
		else if((e instanceof SQLException || e2 instanceof SQLException) && SystemObject.config.getThrowSql() == false) {
			// 无论是否打开隐藏sql，日志表记录的都是真实异常信息
			aj = AjaxJson.getError(e2.getMessage());
			SysLogUtil.endRequest("",aj);
			return AjaxJson.getError("Sql Error").set("reqId", SysLogUtil.getCurrReqId());
		}
		// 如果是redis连接异常 ( 由于redis连接异常，系统已经无法正常工作，所以此处需要立即返回 )
		else if(e instanceof RedisConnectionFailureException) {
			aj = AjaxJson.getError("Redis异常，请检查连接信息");
			aj.set("reqId", SysLogUtil.getCurrReqId());
			return aj;
		}
		// 普通异常输出：500 + 异常信息
		else {
			aj = AjaxJson.getError("系统异常，请联系管理员");
		}

		// 插入到日志表
//		SysLogUtil.endRequest("",aj);

		// 返回到前台
		aj.set("reqId", SysLogUtil.getCurrReqId());
		return aj;
	}

}
