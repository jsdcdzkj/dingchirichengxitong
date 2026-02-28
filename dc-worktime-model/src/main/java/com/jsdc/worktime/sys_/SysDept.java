package com.jsdc.worktime.sys_;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "sys_dept")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_dept",
        comment = "部门"
)
public class SysDept extends Model<SysDept> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment(value = "主键自增ID")
    private Integer id;

    //父级部门
    @Comment(value = "父级部门")
    private Integer parentId;

    //部门名称
    @Comment(value = "部门名称")
    private String name;

    //版本号
    @Comment(value = "版本号")
    private Long version;

    //删除标记（0：否，1：是）
    @Comment(value = "删除标记（0：否，1：是）")
    private Integer isDel;

    //删除标记（0：否，1：是）
    @Comment(value = "创建者")
    private Integer createUser;

    @Comment(value = "修改者")
    private Integer updateUser;

    @Comment(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Comment(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Comment(value = "部门编号")
    private String deptCode;

    @Comment(value = "部门类型 0：内部，1：外部")
    private String deptType;

    @Comment(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Transient
    private List<SysDept> DeptChild;

}
