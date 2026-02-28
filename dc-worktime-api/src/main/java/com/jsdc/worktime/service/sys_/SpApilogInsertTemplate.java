package com.jsdc.worktime.service.sys_;

import com.jsdc.worktime.sys_.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * API日志插入策略  
 * @author kong
 *
 */
@Component
public class SpApilogInsertTemplate {

	/**
	 * sql操作对象 
	 */
	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	/**
	 * 异步插入，不影响请求响应速度 
	 * @param a API日志对象
	 */
	@Async
	public void saveObj(SysLog a) {
		String sql = "insert into sys_log ( "
				+ " reqIp, reqApi, reqParame, reqType, "
				+ "reqToken, reqHeader, userId, adminId, "
				+ "resCode, resMsg, resString, "
				+ "startTime, endTime, costTime"
				+ ") values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		
		Object[] args = {
				 a.getReqIp(), a.getReqApi(), a.getReqParame(), a.getReqType(),
				a.getReqToken(), a.getReqHeader(), a.getUserId(), a.getAdminId(), 
				a.getResCode(), a.getResMsg(), a.getResString(),
				a.getStartTime(), a.getEndTime(), a.getCostTime()
				};
				
		jdbcTemplate.update(sql, args);
	}
	
}
