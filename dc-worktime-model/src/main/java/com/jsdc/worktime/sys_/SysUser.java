package com.jsdc.worktime.sys_;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_user",
        comment = "用户"
)
public class SysUser extends Model<SysUser> implements Serializable {
    /** 记录id  */
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 登录账号
     */
    @Comment("登录账号")
    private String username;
    /**
     * 登陆密码
     */
    @Comment("登陆密码")
    private String password;
    /**
     * 真实姓名
     */
    @Comment("真实姓名")
    private String realName;
    /**
     * 部门
     */
    @Comment("部门")
    private String dept_id;
    /**
     * 工号
     */
    @Comment("工号")
    private String workId;
    /**
     * 姓名全拼
     */
    @Comment("姓名全拼")
    private String nameSpelling;
    /**
     * 手机号
     */
    @Comment("手机号")
    private String phone;
    /**
     * 身份证号
     */
    @Comment("身份证号")
    private String card_no;
    /**
     * 头像地址
     */
    @Comment("头像地址")
    private String avatar;
    /**
     * 状态 0启用1禁用
     */
    @Comment("状态 0启用1禁用")
    private String status;

    /**
     * 是否同步统一登录 0,否, 1,是
     */
    @ApiModelProperty(value = "是否同步统一登录")
    private String isloginty;

    @Transient
    @TableField(exist = false)
    private String tempPass;//旧密码

    @Transient
    @TableField(exist = false)
    private Integer[] roleIds;


    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;
    private Integer create_user;      //创建人
    private Date create_time;      //创建时间
    private Integer update_user;      //创建用户
    private Date update_time;      //创建时间
    private String is_del;          //删除标志

    @Transient
    @TableField(exist = false)
    private String loginName ;

    @Transient
    @TableField(exist = false)
    private Integer groupId;
}
