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
@Table(name = "sys_dict")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_dict",
        comment = "系统字典"
)
public class SysDict extends Model<SysDict> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 父类id
     */
    @Comment("父类id")
    private Integer parentId;
    /**
     * 字典值
     */
    @Comment("字典值")
    private String dictValue;
    /**
     * 字典标识
     */
    @Comment("字典标识")
    private String dictSign;
    /**
     * 字典名称
     */
    @Comment("字典名称")
    private String dictLabel;
    /**
     * 描述
     */
    @Comment("描述")
    private String memo;
    /**
     * 排序
     */
    @Comment("排序")
    private Integer sort;

    private Integer createUser;
    private Integer updateUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Integer isDel;//是否删除 0:正常  1：删除

    @Transient
    @TableField(exist = false)
    private String parentName;

    @Transient
    @TableField(exist = false)
    private List<SysDict> children;
    @Transient
    @TableField(exist = false)
    private List<SysGroup> groups;
}
