package com.jsdc.worktime.sys_;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
		appliesTo = "sys_log",
		comment = "日志"
)
public class SysLog extends Model<SysLog> implements Serializable {


	/** 记录id  */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** 客户端ip  */
	private String reqIp;

	/** 请求api  */
	private String reqApi;

	/** 请求参数  */
	@Column(length = 5000)
	private String reqParame;

	/** 请求方式 */
	private String reqType;

	/** 请求token */
	private String reqToken;

	/** 请求header */
	@Column(length = 1000)
	private String reqHeader;


	/** 返回-状态码  */
	private int resCode;

	/** 返回-信息描述  */
	private String resMsg;

	/** 返回-整个信息字符串形式  */
	@Column(length = 5000)
	private String resString;


	/**  user_id  */
	private long userId;

	/** admin_id  */
	private long adminId;

	/** 请求开始时间   */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone="GMT+8")
	private Date startTime;
	/** 请求结束时间   */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone="GMT+8")
	private Date endTime;
	/** 花费时间，单位ms   */
	private int costTime;

}
