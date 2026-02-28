package com.jsdc.worktime.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;


@Data
public class TimeVo extends Model<TimeVo> {
    private String realName;
    private Integer count;
}
