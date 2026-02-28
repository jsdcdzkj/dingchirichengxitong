package com.jsdc.worktime.controller.sys_;

import com.jsdc.worktime.utils.AjaxJson;
import com.jsdc.worktime.vo.ReportKPIVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考核组成员
 */
@RestController
@RequestMapping("/sysTeamUser")
public class SysTeamUserController {

    @Autowired
    private SysTeamUserService sysTeamUserService;

    /**
     * 考核组成员分页查询
     */
    @RequestMapping("/selectPage")
    public AjaxJson selectPage(@RequestBody SysTeamUser bean) {
        return AjaxJson.getSuccessData(sysTeamUserService.selectPage(bean));
    }

    /**
     * 考核组成员列表查询
     */
    @RequestMapping("/getList")
    public AjaxJson getList(@RequestBody SysTeamUser bean) {
        return AjaxJson.getSuccessData(sysTeamUserService.selectList(bean));
    }

    /**
     * 选择成员列表查询
     */
    @RequestMapping("/getTeamUserList")
    public AjaxJson getTeamUserList(@RequestBody SysTeamUser bean) {
        return sysTeamUserService.getTeamUserList(bean);
    }

    /**
     * 新增
     */
    @RequestMapping("/add")
    public AjaxJson add(@RequestBody SysTeamUser bean) {
        return sysTeamUserService.add(bean);
    }

    /**
     * 删除
     */
    @RequestMapping("/del")
    public AjaxJson del(Integer id) {
        sysTeamUserService.del(id);
        return AjaxJson.getSuccess();
    }

    /**
     * 季度绩效考核
     */
    @RequestMapping("/reportKPI")
    public AjaxJson reportKPI(@RequestBody ReportKPIVo vo) {
        return sysTeamUserService.reportKPI(vo);
    }

}
