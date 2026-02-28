package com.jsdc.worktime.sys_;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Table(name = "sys_file")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Table(
        appliesTo = "sys_file",
        comment = "文件"
)
public class SysFile extends Model<SysFile> implements Serializable {
    @TableId
    @Id
    @Comment("主键ID")
    private String id;
    @Comment("文件名称")
    private String fileName;
    @Comment("文件类型")
    private String contentType;
    @Comment("文件分类  1：图片 2：文件 3：链接")
    private String fileType;
    @Comment("文件大小")
    private String fileSize;
    @Comment("文件地址")
    private String fileUrl;
    @Comment("上传时间")
    private String uploadTime;
    @Comment("业务类型  1.应用图标 2:项目管理文件 3：交付物 4 领导交办上传文件 5 领导交办打回时上传的文件 6 领导交办挂起文件 7阶段性成果 8完成任务成果附件 9. 优化建议, 11.功能点附件 12、手动上传")
    private String bizType;
    @Comment("业务数据ID")
    private Integer bizId;
    @Comment("上传人ID")
    private Integer userId;
    @Comment("项目ID，只有手动创建的时候会新增")
    private Integer projectId;
    @Comment("文件描述")
    private String description;
    @Comment("fastDfs 文件md5 用于删除查看等操作")
    private String fileMd5;
    @Comment("资料名称 只有手动创建的存在")
    private String dataFileName;
    //排序字段 1正序 2倒序
    @Transient
    @TableField(exist = false)
    private Integer sortDate;
    @Transient
    @TableField(exist = false)
    private Integer pageNo;
    @Transient
    @TableField(exist = false)
    private Integer pageSize;
    @Transient
    @TableField(exist = false)
    private String createUsername;
    @Transient
    @TableField(exist = false)
    private String sourceName;

}
