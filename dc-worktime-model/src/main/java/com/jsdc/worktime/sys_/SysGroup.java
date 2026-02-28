package com.jsdc.worktime.sys_;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sys_group")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_group",
        comment = "开发组"
)
public class SysGroup extends Model<SysGroup> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 组名称
     */
    @Comment("组名称")
    private String name;

    /**
     * 组长,用户表id
     */
    @Comment("组长,用户表id")
    private Integer leader;

    /**
     * 所属工作组（实际以数字字典表为主）  1前端工作组 2后端工作组 3产品工作组 4UI工作组 5运维工作组 6测试工作组
     */
    @Comment("工作组类型，数据字典 sysGroup")
    private Integer type;

    //组长 姓名
    @Transient
    @TableField(exist = false)
    private String leader_name;

    private Integer create_user;      //创建人
    private Date create_time;      //创建时间
    private Integer update_user;      //创建用户
    private Date update_time;      //创建时间
    private String is_del;          //删除标志

    //组员
    @Transient
    @TableField(exist = false)
    List<Integer> sysGroupUserList;

    //组员
    @Transient
    @TableField(exist = false)
    List<SysGroupUser> sysGroupUser;

    //组员
    @Transient
    @TableField(exist = false)
    private String type_name;

    //创建人 姓名
    @Transient
    @TableField(exist = false)
    private String create_user_name;

    @Transient
    @TableField(exist = false)
    private Integer pageNo ;
    @Transient
    @TableField(exist = false)
    private Integer pageSize;
    //查询条件 创建时间
    @Transient
    @TableField(exist = false)
    private String query_create_time_start;
    //查询条件 创建时间
    @Transient
    @TableField(exist = false)
    private String query_create_time_end;
}
