package com.jsdc.worktime.sys_;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_user_post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_user_post",
        comment = "用户岗位"
)
public class SysUserPost extends Model<SysUserPost> implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment(value = "主键自增ID")
    private Integer id;

    @Comment(value = "UUID")
    private String uuid;

    /** 用户ID */
    @Comment(value = "用户ID")
    private Integer userId;

    /** 岗位ID */
    @Comment(value = "岗位ID")
    private Integer postId;

    /** 岗位性质（1：主岗，2：副岗） */
    @Comment(value = "岗位性质（1：主岗，2：副岗）")
    private Integer type;

    /** 版本号 */
    @Comment(value = "版本号")
    private Long version;

    /** 删除标记（0：否，1：是）*/
    @Comment(value = "删除标记（0：否，1：是）")
    private Integer isDel;

    @Comment(value = "创建者")
    private Integer createUser;

    @Comment(value = "修改者")
    private Integer updateUser;

    @Comment(value = "创建时间")
    private Date createTime;

    @Comment(value = "修改时间")
    private Date updateTime;

    @Comment(value = "用户UID")
    private String userUId;

    @Comment(value = "岗位UID")
    private String postUId;
}
