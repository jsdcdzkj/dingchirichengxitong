package com.jsdc.worktime.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.worktime.config.WebSocketHandler;
import com.jsdc.worktime.config.formula.FormulaConfig;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.service.sys_.SysUserService;
import com.jsdc.worktime.utils.AjaxJson;
import com.jsdc.worktime.vo.RouterVo;
import com.jsdc.worktime.vo.SysMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/")
public class MainController {
    @Autowired
    private SysUserService sysUserService;

    /**
     *  登录方法
     * @return
     */
    @RequestMapping("/login")
    public AjaxJson login(@RequestBody SysUser sysUser){

        return sysUserService.login(sysUser.getUsername(),sysUser.getPassword());
    }

    /**
     *  配合园区APP，调整优化账户信息，以便于手机端在园区APP的部署
     * @return
     */
    @RequestMapping("/loginPhone")
    public AjaxJson loginPhone(String userName){
        return sysUserService.loginPhone(userName);
    }


    /**
     *  获取用户
     *
     * @return
     */
    @RequestMapping("/userInfo")
    public AjaxJson userInfo(){
        SysUser sysUser =  sysUserService.getUser();
        JSONObject result = new JSONObject();
        result.put("permissions", StpUtil.getRoleList());
        result.put("username", sysUser.getUsername());
        sysUser.setPassword("null");
        result.put("sysUser", sysUser);
        return AjaxJson.getSuccessData(result);
    }

    /**
     *  获取用户菜单
     *
     * @return
     */
    @RequestMapping("/userMenu")
    public AjaxJson userMenu(Integer id){
        List<RouterVo>  routerVoList = sysUserService.userMenu(id);
        return AjaxJson.getSuccessData(routerVoList);
    }

    /**
     *  获取用户 菜单树
     *
     * @return
     */
    @RequestMapping("/userMenuTree")
    public AjaxJson userMenuTree(Integer id){
        List<SysMenuVo>  sysMenuVoList = sysUserService.userMenuTree(id);
        return AjaxJson.getSuccessData(sysMenuVoList);
    }


    /**
     *登出
     * Author wzn
     * Date 2023/3/7 14:11
     */
    @RequestMapping("/logout")
    public AjaxJson logout(){
        sysUserService.logout();
        return AjaxJson.getSuccess();
    }


    @Autowired
    private  WebSocketHandler webSocketHandler;


    @GetMapping("/sendMessage")
    public String sendMessageToUser(@RequestParam String username,@RequestParam String title, @RequestParam String message) {
        webSocketHandler.sendMessageToUser(username, title,message);
        return "Message sent to " + username;
    }



    @PostMapping("/login_client")
    public AjaxJson login_test(@RequestParam String username) {
        QueryWrapper<SysUser> qw = new QueryWrapper<SysUser>();
        qw.eq("workId",username);
        qw.eq("is_del","0");
        SysUser sysUser =  sysUserService.getOne(qw);
        if(sysUser != null){
            return AjaxJson.getSuccessData(sysUser.getUsername());
        }
        return AjaxJson.getError();
    }

    @PostMapping("/select_test")
    public AjaxJson select_test(@RequestBody String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        jsonObject.put("msg","请求成功了");
        return AjaxJson.getSuccessData(jsonObject);
    }
    @Autowired
    private FormulaConfig formulaConfig;

    @PostMapping("/getAllConfigs")
    public AjaxJson getAllConfigs(){
        FormulaConfig config = new FormulaConfig();
        config.setDifficulty(formulaConfig.getDifficulty());
        config.setWeights(formulaConfig.getWeights());
        config.setDevWeights(formulaConfig.getDevWeights());
        config.setManageWeights(formulaConfig.getManageWeights());
        config.setSeverity(formulaConfig.getSeverity());
        config.setEfficiency(formulaConfig.getEfficiency());
        config.setBugFreeRate(formulaConfig.getBugFreeRate());
        return AjaxJson.getSuccessData(config);
    }
}
