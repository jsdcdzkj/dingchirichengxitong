package com.jsdc.worktime.sys_;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sys_group_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_group_user",
        comment = "组成员"
)
public class SysGroupUser extends Model<SysGroupUser> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 开发组Id
     */
    @Comment(value = "开发组Id")
    private Integer groupId;
    /**
     * 用户ID
     */
    @Comment(value = "用户ID")
    private Integer userId;
    /**
     * 组员 姓名
     */
    @Comment(value = "组员 姓名")
    private String userName;

}
