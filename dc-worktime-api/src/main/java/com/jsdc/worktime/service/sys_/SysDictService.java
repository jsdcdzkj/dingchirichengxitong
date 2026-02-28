package com.jsdc.worktime.service.sys_;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.worktime.global.G;
import com.jsdc.worktime.sys_.SysDict;
import com.jsdc.worktime.service.BaseService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (SysDict)表服务接口
 *
 * @author wangYan
 * @since 2023-05-09
 */
@Service
@Transactional
public class SysDictService extends BaseService<SysDict> {

    @Autowired
    private SysUserService sysUserService;


    /**
     * @description 查询所有字典数据
     */
    public List<SysDict> getAllDicts() {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getIsDel, "0");
        return list(wrapper);
    }

    /**
     * @description 通过类型和值
     */
    public SysDict selectByValueAndType(String dict_type, String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictSign", dict_type);
        queryWrapper.eq("dictValue", value);
        queryWrapper.eq("isDel", "0");
        return getOne(queryWrapper);
    }

    /**
     * @description 通过类型
     */
    public List<SysDict> selectByType(String dict_type) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictSign", dict_type);
        queryWrapper.eq("isDel", "0");
        queryWrapper.orderByAsc("sort")  ;
        return list(queryWrapper);
    }

    /**
     * @description 通过类型
     */
    public boolean compareDictValueIsContains(String dict_type,String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictSign", dict_type);
        queryWrapper.eq("isDel", "0");
        List<SysDict> list = list(queryWrapper);
        SysDict sysDict = list.get(0);
        List<String> collect = Arrays.stream(sysDict.getDictValue().split(",")).collect(Collectors.toList());
        return collect.contains(value);
    }

    /**
     * @description 通过值
     */
    public SysDict selectByValue(String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", value);
        queryWrapper.eq("isDel", "0");
        return getOne(queryWrapper);
    }

    /**
     * 保存数据字典
     * @param sysDict
     * @return
     */
    public Object saveDict(SysDict sysDict) {
        // 校验
        list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictValue, sysDict.getDictValue())
                .eq(SysDict::getDictSign, sysDict.getDictSign()).eq(SysDict::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(dict -> {
            if (null == sysDict.getId() || !sysDict.getId().equals(dict.getId())) {
                throw new RuntimeException("字典值已存在");
            }
        });
        list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictLabel, sysDict.getDictLabel())
                .eq(SysDict::getDictSign, sysDict.getDictSign()).eq(SysDict::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(dict -> {
            if (null == sysDict.getId() || !sysDict.getId().equals(dict.getId())) {
                throw new RuntimeException("字典标识已存在");
            }
        });
        // 数据组装
        sysDict.setIsDel(G.ISDEL_NO);
        sysDict.setParentId(null == sysDict.getParentId() ? -1 : sysDict.getParentId());
        if (null != sysDict && null != sysDict.getId() && null != sysDict.selectById(sysDict.getId())){
            sysDict.setUpdateTime(new Date());
            sysDict.setUpdateUser(Integer.valueOf(sysUserService.getUser().getId()));
        }else {
            sysDict.setCreateTime(new Date());
            sysDict.setCreateUser(Integer.valueOf(sysUserService.getUser().getId()));
        }
        sysDict.insertOrUpdate();

        return sysDict;
    }

    public List<SysDict> getList(SysDict sysDict) {
        QueryWrapper queryWrapper=new QueryWrapper();

        queryWrapper.eq("isDel",0);

        if (Strings.isNotEmpty(sysDict.getDictSign())){
            queryWrapper.eq("dictSign",sysDict.getDictSign());
        }

        return getBaseMapper().selectList(queryWrapper);
    }

    public static List<SysDict> getTreeList(List<SysDict> list, int i) {
        List<SysDict> collect = list.stream().filter(sysDict -> sysDict.getParentId() == i).collect(Collectors.toList());
        for (SysDict sysDict : collect) {
            List<SysDict> children = getTreeList(list, sysDict.getId());
            sysDict.setChildren(children);
        }
        return collect;
    }

}

