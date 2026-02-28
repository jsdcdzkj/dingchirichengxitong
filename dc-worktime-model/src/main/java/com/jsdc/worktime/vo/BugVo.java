package com.jsdc.worktime.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;


@Data
public class BugVo extends Model<BugVo> {
    private String realName;
    private String count;
    private String assigned_to_user_id;
}
