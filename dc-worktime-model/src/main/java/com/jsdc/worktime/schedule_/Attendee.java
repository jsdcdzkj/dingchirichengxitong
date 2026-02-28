package com.jsdc.worktime.schedule_;

import lombok.Data;

@Data
public class Attendee {
    private String userid;
    private String name;
    private int status; // 0: 未响应, 1: 接受, 2: 拒绝, 3: 待定
}
