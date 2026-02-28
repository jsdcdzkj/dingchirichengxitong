package com.jsdc.worktime.controller.sys_;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.service.sys_.SysMenuService;
import com.jsdc.worktime.sys_.SysMenu;
import com.jsdc.worktime.utils.AjaxJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @projectName: IOT
 * @className: SysMenuController
 * @author: wp
 * @description:
 * @date: 2023/5/9 14:47
 */
@RestController
@RequestMapping("sysmenu")
@Api( tags = "菜单管理")
public class SysMenuController {

    @Autowired
    SysMenuService menuService;

    @PostMapping(value = "getMenu.do")
    @ApiOperation("查询菜单信息")
    public AjaxJson getMenu(SysMenu menu){
        SysMenu m = menuService.getMenu(menu);
        return AjaxJson.getSuccessData(m);
    }

    @PostMapping(value = "getMenuList.do")
    @ApiOperation("查询菜单列表")
    public AjaxJson getMenuList(SysMenu menu){
        List<SysMenu> list = menuService.getMenuList(menu);
        return AjaxJson.getSuccessData(list);
    }

    @PostMapping(value = "getMenuPage.do")
    @ApiOperation("分页查询菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public AjaxJson getPage(SysMenu menu, @RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        Page page = menuService.getMenuPage(menu, pageIndex, pageSize);
        return AjaxJson.getSuccessData(page);
    }

    @PostMapping(value = "getMenuTree.do")
    @ApiOperation("查询菜单树")
    public AjaxJson getMenuTree(SysMenu menu){
//        List<Tree<String>> tree = menuService.getMenuTree(menu);
        return AjaxJson.getSuccessData(menuService.getMenuTree1(menu));
    }

    @PostMapping(value = "saveMenu.do")
    @ApiOperation("保存菜单")
    public AjaxJson addMenu(@RequestBody SysMenu menu){
        if (null != menu.getId() && menuService.getBaseMapper().selectById(menu.getId()) != null) {
            if(menuService.editMenu(menu)){
                return AjaxJson.getSuccess("编辑菜单成功");
            }else{
                return AjaxJson.getError("编辑菜单失败");
            }
        }else {
            if(menuService.addMenu(menu)){
                return AjaxJson.getSuccess("保存菜单成功");
            }else{
                return AjaxJson.getError("新增菜单失败");
            }
        }
    }

//    @PostMapping(value = "editMenu.do")
//    @ApiOperation("编辑菜单")
//    public AjaxJson editMenu(SysMenu menu){
//        if(menuService.editMenu(menu)){
//            return AjaxJson.success();
//        }else{
//            return AjaxJson.error("编辑菜单失败");
//        }
//    }

    @PostMapping(value = "delMenu.do")
    @ApiOperation("删除菜单")
    public AjaxJson delMenu(SysMenu menu){
        if(menuService.delMenu(menu.getId())){
            return AjaxJson.getSuccess("删除菜单成功");
        }else{
            return AjaxJson.getError("删除菜单失败");
        }
    }

    @PostMapping(value = "doChangeStatus")
    @ApiOperation("修改显示隐藏菜单状态")
    public AjaxJson doChangeStatus(SysMenu menu){
        if(menuService.doChangeStatus(menu)){
            return AjaxJson.getSuccess("修改显示隐藏菜单状态成功");
        }else{
            return AjaxJson.getError("修改显示隐藏菜单状态失败");
        }
    }

    /**
     * 组装菜单树
     */
    @PostMapping(value = "getMenuTypeTree.do")
    @ApiOperation("查询菜单树")
    public AjaxJson getMenuTypeTree(SysMenu menu){
        return AjaxJson.getSuccessData(menuService.getMenuTypeTree(menu));
    }

}
