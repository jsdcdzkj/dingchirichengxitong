package com.jsdc.worktime.controller.sys_;


import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.service.sys_.SysUserService;
import com.jsdc.worktime.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;






    /**
     * 保存
     * @param sysUser
     * @return
     */
    @PostMapping("/save")
    public AjaxJson saveUser(@RequestBody SysUser sysUser){
        return sysUserService.saveUser(sysUser);
    }


    /**
    *用户启用
    * Author wzn
    * Date 2023/3/24 14:00
    */
    @PostMapping("/forbidden")
    public AjaxJson forbidden(@RequestBody SysUser sysUser){
        return sysUserService.forbidden(sysUser);
    }


    /**
     * 修改密码
     * @param sysUser
     * @return
     */
    @PostMapping("/pass")
    public AjaxJson pass(@RequestBody SysUser sysUser){
        return sysUserService.pass(sysUser);
    }

    /**
     * 重置密码 123456
     * @return
     */
    @PostMapping("/resetPass")
    public AjaxJson resetPass(Integer id){
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setPassword((SaSecureUtil.md5("Jsdc@0516")));
        sysUser.updateById();
        return AjaxJson.getSuccessData(sysUser);
    }


    /**
     *登录后校验密码
     * @author wzn
     * @date 2024/6/3 15:48
     */
//    @PostMapping("/verifyPass")
//    public AjaxJson verifyPass(@RequestBody SysUser user){
//        return AjaxJson.getSuccess(sysUserService.verifyPass(user));
//    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @PostMapping("/delUser")
    public AjaxJson delUser(Integer userId){
        SysUser sysUser = sysUserService.getById(userId);
        if(sysUser != null){
            sysUser.setIs_del("1");
            sysUser.updateById();
        }else {
            return AjaxJson.getError("无此用户数据！");
        }

        try {
            List<SysUser> users = new ArrayList<>();
            users.add(sysUser);
            sysUserService.syncUser(users, "2");
        }catch (Exception e) {
            System.out.println("统一门户账号信息同步失败");
        }

        return AjaxJson.getSuccess();
    }






    /**
    *用户列表
    * Author wzn
    * Date 2023/3/8 14:00
    */
    @PostMapping("/getList")
    public AjaxJson getList(@RequestBody SysUser sysUser){
        Page<SysUser> userPage = sysUserService.selectUserList(sysUser);
        return AjaxJson.getSuccessData(userPage);
    }

    /**
     *用户列表 不分页
     * Author wzn
     * Date 2023/3/8 14:00
     */
    @PostMapping("/getListAll")
    public AjaxJson getListAll(@RequestBody SysUser sysUser){
        List<SysUser> list = sysUserService.selectGetListAll(sysUser);
        return AjaxJson.getSuccessData(list);
    }

    /**
     *用户列表 不分页 筛选分组
     * Author wzn
     * Date 2023/3/8 14:00
     */
    @PostMapping("/selectByGroupId")
    public AjaxJson selectByGroupId(@RequestBody SysUser sysUser){
        List<SysUser> list = sysUserService.selectByGroupId(sysUser);
        return AjaxJson.getSuccessData(list);
    }


    /**
     * 初始化所有账号的密码 身份证+@0516
     * @author wzn
     * @date 2024/6/3 16:07
     */
    @GetMapping("/initPass")
    public AjaxJson initPass(){
        sysUserService.initPass() ;
        return AjaxJson.getSuccess();
    }

    /**
     * 统一门户用户同步
     * @return
     */
    @GetMapping("/syncAccountInfo")
    public AjaxJson syncAccountInfo() {
        return sysUserService.syncAccountInfo();
    }
}

