package com.jsdc.worktime.controller.sys_;

import com.jsdc.worktime.service.sys_.SysGroupService;
import com.jsdc.worktime.sys_.SysGroup;
import com.jsdc.worktime.sys_.SysGroupUser;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开发组管理
 */
@RestController
@RequestMapping("/sysGroup")
public class SysGroupController {
    @Autowired
    private SysGroupService mainService;

    /**
     * 分页接口
     */
    @RequestMapping("/selectPage")
    public AjaxJson selectPage(SysGroup bean) {
        return AjaxJson.getSuccessData(mainService.selectPage(bean));
    }

    /**
     * 开发组列表查询
     */
    @RequestMapping("/getList")
    public AjaxJson getList(SysGroup bean) {
        return AjaxJson.getSuccessData(mainService.selectList(bean));
    }

    /**
     * 开发组长列表查询
     */
    @RequestMapping("/getLeaderList")
    public AjaxJson getLeaderList(SysGroup bean) {
        return AjaxJson.getSuccessData(mainService.getLeaderList(bean));
    }

    /**
     *
     *所有组列表
     * @author wzn
     * @date 2025/04/22 16:12
     */
    @RequestMapping("/groupList")
    public AjaxJson groupList() {
        return AjaxJson.getSuccessData(mainService.groupList());
    }

    /**
     * 开发组成员列表查询
     */
    @RequestMapping("/getGroupUserList")
    public AjaxJson getGroupUserList(SysGroupUser bean) {
        return AjaxJson.getSuccessData(mainService.getGroupUserList(bean));
    }

    @RequestMapping("/getGroupUser")
    public AjaxJson getGroupUser() {
        return AjaxJson.getSuccessData(mainService.getGroupUser());
    }

    /**
     * 工作组成员列表查询
     */
    @RequestMapping("/getTypeUserList")
    public AjaxJson getTypeUserList(SysGroup bean) {
        return AjaxJson.getSuccessData(mainService.getTypeUserList(bean));
    }

    /**
     * 新增修改接口
     */
    @RequestMapping("/addUpdateInfo")
    public AjaxJson addUpdateInfo(@RequestBody SysGroup bean) {
        return AjaxJson.getSuccessData(mainService.addUpdateInfo(bean));
    }

    /**
     * 删除接口
     */
    @RequestMapping("/delData")
    public AjaxJson delData(Integer id) {
        return AjaxJson.getSuccessData(mainService.delData(id));
    }

}
