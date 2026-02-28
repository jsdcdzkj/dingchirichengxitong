package com.jsdc.worktime.utils.FastDfs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FastDfsParams {
    private String uploadPath;
    private MultipartFile file;
    private String bizType;//业务类型 1.应用图标 2.项目文件4 领导交办上传文件 5 领导交办打回时上传的文件 6 领导交办挂起文件
    private Integer bizId;//业务数据ID
    private String fileName;//文件名

    public FastDfsParams() {
    }

    public FastDfsParams(String uploadPath, MultipartFile file, String bizType, Integer bizId) {
        this.uploadPath = uploadPath;
        this.file = file;
        this.bizType = bizType;
        this.bizId = bizId;
    }
}
