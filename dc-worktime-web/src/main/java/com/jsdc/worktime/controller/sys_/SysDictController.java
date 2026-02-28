package com.jsdc.worktime.controller.sys_;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.global.G;
import com.jsdc.worktime.sys_.SysDict;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.service.sys_.SysDictService;
import com.jsdc.worktime.service.sys_.SysUserService;
import com.jsdc.worktime.utils.AjaxJson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据字典
 */
@RestController
@RequestMapping("sysDict")
public class SysDictController {
    /**
     * 服务对象
     */
    @Resource
    private SysDictService sysDictService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询所有数据
     *
     * @param sysDict 查询实体
     * @return 所有数据
     */
    @PostMapping("/getPage")
    public AjaxJson getPage(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                            SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null != sysDict.getParentId(), SysDict::getParentId, sysDict.getParentId());
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysDict.getDictLabel()), SysDict::getDictLabel, sysDict.getDictLabel());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictValue()), SysDict::getDictValue, sysDict.getDictValue());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictSign()), SysDict::getDictSign, sysDict.getDictSign());
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.orderByDesc(SysDict::getDictSign).orderByAsc(SysDict::getSort);
        Page<SysDict> dictPage = sysDictService.page(new Page<>(pageNo, pageSize), lambdaQueryWrapper);
        for (SysDict dict : dictPage.getRecords()) {
            if (null != dict.getParentId() && dict.getParentId() != -1) {
                SysDict parentDict = sysDictService.getById(dict.getParentId());
                dict.setParentName(parentDict.getDictLabel());
            }
        }
        return AjaxJson.getSuccessData(dictPage);
    }
    /**
     * 树形结构查询
     */
    @PostMapping("/getTree")
    public AjaxJson getTree(SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null != sysDict.getParentId(), SysDict::getParentId, sysDict.getParentId());
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysDict.getDictLabel()), SysDict::getDictLabel, sysDict.getDictLabel());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictValue()), SysDict::getDictValue, sysDict.getDictValue());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictSign()), SysDict::getDictSign, sysDict.getDictSign());
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.orderByDesc(SysDict::getDictSign);
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);

        for (SysDict dict : list) {
            if (null != dict.getParentId() && dict.getParentId() != -1) {
                SysDict parentDict = sysDictService.getById(dict.getParentId());
                dict.setParentName(parentDict.getDictLabel());
            }
        }
        // 组装树形结构递归数据到children
        List<SysDict> treeList = sysDictService.getTreeList(list, -1);
        return AjaxJson.getSuccessData(treeList);
    }

    /**
     * 查询所有类型数据
     */
    @PostMapping("/getDictSignList")
    public AjaxJson getDictSignList() {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);
        // 根据字典类型去重
        List<String> types = list.stream().map(SysDict::getDictSign).distinct().collect(Collectors.toList());
        return AjaxJson.getSuccessData(types);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("/selectOne/{id}")
    public AjaxJson selectOne(@PathVariable Serializable id) {
        return AjaxJson.getSuccessData(this.sysDictService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysDict 实体对象
     * @return 新增结果
     */
    @PostMapping("/saveOrUpdate")
    public AjaxJson saveOrUpdate(@RequestBody SysDict sysDict) {
        try {
            sysDictService.saveDict(sysDict);
        } catch (Exception e) {
            return AjaxJson.getError(e.getMessage());
        }
        return AjaxJson.getSuccessData(sysDict.getDictLabel());
    }

    /**
     * 得到所有数据list集合
     */
    @PostMapping("/getList")
    public AjaxJson getList(SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictSign()), SysDict::getDictSign, sysDict.getDictSign());
        lambdaQueryWrapper.orderByDesc(SysDict::getCreateTime);
        return AjaxJson.getSuccessData(sysDictService.list(lambdaQueryWrapper));
    }

    /**
     * 得到所有数据Map-list集合
     */
    @PostMapping("/getMapList")
    public AjaxJson getMapList() {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.orderByDesc(SysDict::getCreateTime);
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);
        List<SysDict> sortedList = list.stream()
                .sorted(Comparator.comparingInt(dict -> Integer.parseInt(dict.getDictValue())))
                .collect(Collectors.toList());
        Map<String, List<SysDict>> dictMap = sortedList.stream()
                .collect(Collectors.groupingBy(SysDict::getDictSign));
        return AjaxJson.getSuccessData(dictMap);
    }

    /**
     * 删除数据
     *
     * @return 删除结果
     */
    @PostMapping("/delete")
    public AjaxJson delete(Integer id) {
        if (null == id) {
            return AjaxJson.getError("id不能为空");
        }
        LambdaUpdateWrapper<SysDict> wrapper = Wrappers.<SysDict>lambdaUpdate();
        wrapper.eq(SysDict::getId, id);
        wrapper.set(SysDict::getIsDel, G.ISDEL_YES);
        sysDictService.update(null, wrapper);
        SysDict sysDict = sysDictService.getById(id);
        return AjaxJson.getSuccessData(sysDict.getDictLabel());
    }

    /**
     * 修改数据
     *
     * @param sysDict 实体对象
     * @return 修改结果
     */
    @RequestMapping("/update")
    public AjaxJson update(@RequestBody SysDict sysDict) {
        return AjaxJson.getSuccessData(this.sysDictService.updateById(sysDict));
    }

    /**
     * 新增修改数据
     *
     * @param sysDict 实体对象
     * @return 修改结果
     */
    @RequestMapping("/insertOrUpdate")
    public AjaxJson insertOrUpdate(@RequestBody SysDict sysDict) {
        SysUser sysUser = sysUserService.getUser();
        if (sysDict.getId() == null) {
            sysDict.setCreateUser(Integer.valueOf(sysUser.getId()));
            sysDict.setCreateTime(new Date());
            sysDict.setIsDel(0);
        } else {
            sysDict.setUpdateUser(Integer.valueOf(sysUser.getId()));
            sysDict.setUpdateTime(new Date());
        }
        sysDictService.saveOrUpdate(sysDict);
        return AjaxJson.getSuccessData(sysDict.getDictLabel());
    }

    /**
     * 根据字典类型，查询列表
     * @param bean
     * @return
     */
    @PostMapping("/selectDictList")
    public AjaxJson selectDictList(SysDict bean) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        if(null != bean && null != bean.getDictSign()) {
            lambdaQueryWrapper.eq(SysDict::getDictSign, bean.getDictSign());
        }
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);
        return AjaxJson.getSuccessData(list);
    }

}

