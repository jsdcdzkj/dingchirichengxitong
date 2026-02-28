package com.jsdc.worktime.sys_;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_role",
        comment = "角色"
)
public class SysRole extends Model<SysRole> implements Serializable {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 名称
     */
    private String role_name;
    /**
     * 权限标志
     */
    private String role_symbol;
    /**
     * 描述
     */
    private String memo;
    @Transient
    @TableField(exist = false)
    private Integer[] menuIds;

    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;


    private Integer create_user;      //创建人
    private String create_user_name;      //创建人
    private Date create_time;      //创建时间
    private Integer update_user;      //创建用户
    private Date update_time;      //创建时间
    private String is_del;          //删除标志
}
