package com.jsdc.worktime.controller.sys_;

import com.jsdc.worktime.pro_.ProDeliverable;
import com.jsdc.worktime.service.sys_.SysFileService;
import com.jsdc.worktime.sys_.SysFile;
import com.jsdc.worktime.utils.AjaxJson;
import com.jsdc.worktime.utils.FastDfs.FastDfsParams;
import com.jsdc.worktime.utils.FastDfs.FastDfsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件服务
 *
 * @author: jxl
 * @create: 2025-02-22 14:02
 **/
@Transactional
@RestController
@RequestMapping("/file")
public class SysFileController {
    @Autowired
    private FastDfsUtil fastDfsUtil;
    @Autowired
    private SysFileService fileService;

    /**
     * 多文件上传
     *
     * @param files
     * @param id         业务Id
     * @param uploadPath 自定义路径 所处模块文件包名  如 /pro
     * @param bizType    业务类型 1.应用图标 2:项目管理文件 3：交付物 4 领导交办上传文件 5 领导交办打回时上传的文件 6 领导交办挂起文件 7阶段性成果 8完成任务成果附件
     * @return
     * @throws IOException
     */
    public AjaxJson multipleCommentImageUpload(List<MultipartFile> files, Integer id, String uploadPath, String bizType) throws IOException {
        for (MultipartFile file : files) {
            FastDfsParams params = new FastDfsParams(uploadPath, file, bizType, id);
            params.setFileName(file.getOriginalFilename());
            AjaxJson result = fastDfsUtil.uploadFile(params);
            if (result.getCode() != 0) {
                return AjaxJson.getError();
            }
//            return result;
        }
        return AjaxJson.getSuccess();
    }

    @PostMapping("/saveOrUpd")
    public AjaxJson saveOrUpd(@RequestBody List<SysFile> bean) {
        fileService.saveOrUpd(bean);
        return AjaxJson.getSuccess();
    }
    //直接删除此条信息
    @GetMapping("/deleteById")
    public AjaxJson deleteById(String id) {
        fileService.deleteById(id);
        return AjaxJson.getSuccess();
    }
    //删除文件url
    @GetMapping("/deleteFileById")
    public AjaxJson deleteFileById(String id) {
        fileService.deleteFileById(id);
        return AjaxJson.getSuccess();
    }
    /**
     * 分页查询
     *
     * @param bean
     * @return
     */
    @PostMapping("/selectPageList")
    public AjaxJson selectPageList(@RequestBody SysFile bean) {
        return AjaxJson.getSuccessData(fileService.selectPageList(bean));

    }
}
