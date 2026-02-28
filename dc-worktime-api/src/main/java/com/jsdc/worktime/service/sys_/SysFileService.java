package com.jsdc.worktime.service.sys_;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.mapper.sys_.SysDictMapper;
import com.jsdc.worktime.mapper.sys_.SysFileMapper;
import com.jsdc.worktime.service.BaseService;
import com.jsdc.worktime.sys_.SysFile;
import com.jsdc.worktime.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysFileService extends BaseService<SysFile> {

    @Autowired
    private SysFileMapper fileMapper;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysDictMapper dictMapper;

    /**
     * 列表查询
     */
    public List<SysFile> toList(SysFile bean) {
        QueryWrapper<SysFile> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("is_del", "0");
        if (null != bean) {
            if (null != bean.getBizId()) {
                queryWrapper.eq("bizId", bean.getBizId());
            }
            if (StringUtils.isNotEmpty(bean.getBizType())) {
                queryWrapper.eq("bizType", bean.getBizType());
            }
        }
        queryWrapper.orderByDesc("uploadTime");
        List<SysFile> list = fileMapper.selectList(queryWrapper);
        return list;
    }

    public void saveOrUpd(List<SysFile> files) {
        for (SysFile bean : files) {
            if (null == bean.getId()) {
                bean.setUserId(userService.getUser().getId());
                bean.setUploadTime(DateUtils.dateToStr(new Date()));
                fileMapper.insert(bean);
            } else {
                bean.setUserId(userService.getUser().getId());
                bean.setUploadTime(DateUtils.dateToStr(new Date()));
                fileMapper.updateById(bean);
            }
        }
    }

    public Page<SysFile> selectPageList(SysFile bean) {
        Page<SysFile> page = fileMapper.selectPageList1(new Page<>(bean.getPageNo(), bean.getPageSize()),bean);
        List<SysFile> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (SysFile record : records) {
            }
        }
        return page;
    }



    /**
     * 格式化路径字符串，处理斜杠和空格
     */
    private String formatPathString(String rawPath) {
        if (StrUtil.isBlank(rawPath)) {
            return "";
        }

        // 处理斜杠：前后加空格，去除连续空格，去除首尾空格
        String formattedPath = rawPath.replaceAll("/", " / ")
                .replaceAll("\\s+", " ")
                .trim();

        // 去除最后一个斜杠及多余空格
        if (formattedPath.endsWith("/")) {
            formattedPath = formattedPath.substring(0, formattedPath.length() - 1).trim();
        }

        return formattedPath;
    }
    public void deleteById(String id) {
        fileMapper.deleteById(id);
    }

    public void deleteFileById(String id) {
        fileMapper.update(null, Wrappers.<SysFile>lambdaUpdate().eq(SysFile::getId, id)
                .set(SysFile::getFileName, null)
                .set(SysFile::getFileUrl, null));
    }
}
