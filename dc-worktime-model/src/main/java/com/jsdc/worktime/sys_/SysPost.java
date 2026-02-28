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
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_post",
        comment = "岗位"
)
public class SysPost extends Model<SysPost> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment(value = "主键自增ID")
    private Integer id;

    //所属部门
    @Comment(value = "所属部门")
    private Integer deptId;

    //岗位名称
    @Comment(value = "岗位名称")
    private String name;

    //版本号
    @Comment(value = "版本号")
    private Long version;

    //删除标记（0：否，1：是）
    @Comment(value = "删除标记（0：否，1：是）")
    private Integer isDel;

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

    @Comment(value = "岗位编码")
    private String postCode;

    @Comment(value = "岗位类型 0：内部，1：外部")
    private String postType;

    @Comment(value = "上级岗位")
    private Integer parentId;

    @Comment(value = "备注")
    private String remark;

    @Comment(value = "UUID")
    private String UUID;

    @Comment(value = "parentUUID")
    private String parentUUID;

    @Comment(value = "M3系统ID")
    private String m3Id;
}
