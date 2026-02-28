package com.jsdc.worktime.service.sys_;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsdc.worktime.mapper.sys_.SysGroupMapper;
import com.jsdc.worktime.mapper.sys_.SysGroupUserMapper;
import com.jsdc.worktime.sys_.SysDict;
import com.jsdc.worktime.sys_.SysGroup;
import com.jsdc.worktime.sys_.SysGroupUser;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SysGroupService extends ServiceImpl<SysGroupMapper, SysGroup> {
    @Autowired
    private SysGroupMapper mainMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysGroupUserMapper sysGroupUserMapper;
    @Autowired
    private SysGroupMapper sysGroupMapper;


    /**
     * 分页
     */
    public Page<SysGroup> selectPage(SysGroup bean) {
        SysUser sysUser = sysUserService.getUser();
        Page<SysGroup> page = mainMapper.queryPage(new Page<>(bean.getPageNo(), bean.getPageSize()), bean);
        for (SysGroup testBug : page.getRecords()) {
            convert(testBug, sysUser);
        }
        return page;
    }

    /**
     * 列表
     */
    public List<SysGroup> selectList(SysGroup bean) {
        SysUser sysUser = sysUserService.getUser();
        List<SysGroup> list = mainMapper.queryList(bean);
        for (SysGroup testBug : list) {
            convert(testBug, sysUser);
        }
        return list;
    }

    /**
     * 开发组长列表查询
     */
    public Map<Integer, String> getLeaderList(SysGroup bean) {
        SysUser sysUser = sysUserService.getUser();
        List<SysGroup> list = mainMapper.queryList(bean);
        for (SysGroup testBug : list) {
            convert(testBug, sysUser);
        }
        return list.stream().filter(g -> g.getLeader() != null && g.getLeader_name() != null).collect(Collectors.toMap(SysGroup::getLeader, SysGroup::getLeader_name, (existing, replacement) -> existing));
    }

    /**
     * 组员列表查询
     */
    public List<Map> getTypeUserList(SysGroup bean) {
        List<Map> result = new ArrayList<>();
        List<SysGroup> sysGroups = mainMapper.queryList(bean);
        for (SysGroup sysGroup : sysGroups) {
            QueryWrapper<SysGroupUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("groupId", sysGroup.getId());
            List<SysGroupUser> sysGroupUserList = sysGroupUserMapper.selectList(queryWrapper);
            for (SysGroupUser sysGroupUser : sysGroupUserList) {
                Map<String, String> map = new HashMap<>();
                map.put("id",sysGroupUser.getUserId() + "");
                map.put("userName",sysGroupUser.getUserName() + "");
                result.add(map);
            }
        }

        return result;
    }


    public List<SysGroup> groupList() {
        return mainMapper.selectList(new QueryWrapper<SysGroup>().eq("is_del", 0));
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<SysGroup> getList(SysGroup bean) {
        QueryWrapper<SysGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0");
        List<SysGroup> list = mainMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 开发组成员列表查询
     */
    public List<SysGroupUser> getGroupUserList(SysGroupUser bean) {
        QueryWrapper<SysGroupUser> queryWrapper = new QueryWrapper<>();
        if (null != bean) {
            //开发组Id
            if (null != bean.getGroupId()) {
                queryWrapper.eq("groupId", bean.getGroupId());
            }
        }

        List<SysGroupUser> list = sysGroupUserMapper.selectList(queryWrapper);
        return list;
    }


    public List<SysGroupUser> getGroupUser() {
        QueryWrapper<SysGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0") ;
        queryWrapper.eq("leader", sysUserService.getUser().getId()) ;
        List<SysGroup> sysGroup = sysGroupMapper.selectList(queryWrapper);

        QueryWrapper<SysGroupUser> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("groupId", sysGroup.stream() // 假设 SysGroup 的 id 是 Long 类型
                .map(SysGroup::getId) // 调用 getId 方法获取每个 SysGroup 的 id
                .collect(Collectors.toList()));

        List<SysGroupUser> list = sysGroupUserMapper.selectList(queryWrapper2);
        List<SysGroupUser> uniqueList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            Map<Integer, SysGroupUser> map = new LinkedHashMap<>();
            for (SysGroupUser user : list) {
                if (!map.containsKey(user.getUserId())) {
                    map.put(user.getUserId(), user);
                }
            }
            uniqueList = new ArrayList<>(map.values());
        }


        return uniqueList;
    }

    public void convert(SysGroup bean, SysUser sysUser) {
        //组长
        if (null != bean.getLeader()) {
            SysUser sysUserLeader = sysUserService.getById(bean.getLeader());
            if (null != sysUserLeader) {
                bean.setLeader_name(sysUserLeader.getUsername());
            }
        }

        //组员
        QueryWrapper<SysGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", bean.getId());
        List<SysGroupUser> selectSysGroupUserList = sysGroupUserMapper.selectList(queryWrapper);
        bean.setSysGroupUser(selectSysGroupUserList);

        //类型
        List<SysDict> sysGroupList = sysDictService.selectByType("sysGroup");
        Map<String, String> sysGroupMap = new HashMap<>();
        for (SysDict sysDict : sysGroupList) {
            sysGroupMap.put(sysDict.getDictValue(), sysDict.getDictLabel());
        }

        if (null != bean.getType()) {
            bean.setType_name(sysGroupMap.get(bean.getType()));
        }

        //创建人
        SysUser sysUserConvert = new SysUser();
        sysUserConvert.setId(bean.getCreate_user());
        sysUserConvert.selectById();
        bean.setCreate_user_name(sysUserConvert.getRealName());

    }


    /**
     * 新增修改
     */
    public AjaxJson addUpdateInfo(SysGroup bean) {
        SysUser sysUser = sysUserService.getUser();
        //唯一性校验
        boolean check = this.checkOnly(bean);
        if (!check) {
            AjaxJson.getError("编码已存在，禁止重复添加");
        }
        if (null != bean.getId()) {
            bean.updateById();
        } else {
            bean.setCreate_user(sysUser.getId());
            bean.setCreate_time(new Date());
            bean.setIs_del("0");
            bean.insert();
        }

        //关联组员
        SysGroupUser delSysGroupUser = new SysGroupUser();
        delSysGroupUser.setGroupId(bean.getId());
        delSysGroupUser.delete(new QueryWrapper<>(delSysGroupUser));
        if (null != bean.getSysGroupUserList() && bean.getSysGroupUserList().size() > 0) {
            for (int i = 0; i < bean.getSysGroupUserList().size(); i++) {
                Integer sysUserId = bean.getSysGroupUserList().get(i);
                SysUser sysUserGroup = sysUserService.getById(sysUserId);

                SysGroupUser insSysGroupUser = new SysGroupUser();
                insSysGroupUser.setGroupId(bean.getId());
                insSysGroupUser.setUserId(sysUserGroup.getId());
                insSysGroupUser.setUserName(sysUserGroup.getRealName());

                insSysGroupUser.insert();
            }
        }

        return AjaxJson.getSuccess();
    }


    public boolean checkOnly(SysGroup bean) {
        boolean checkOnly = true; //没有重复
//        QueryWrapper<SysGroup> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("code",bean.getCode()) ;
//        queryWrapper.eq("is_del","0") ;
//        List<SysGroup> info = mainMapper.selectList(queryWrapper) ;
//        if(!info.isEmpty()){
//            checkOnly = false ;
//            if(null!=bean.getId()){
//                if(bean.getId().equals(info.get(0).getId())){
//                    checkOnly = true ;
//                }
//            }
//        }
        return checkOnly;
    }

    /**
     * 删除
     */
    public boolean delData(Integer id) {
        SysGroup bean = new SysGroup();
        bean.setId(id);
        bean.setIs_del("1");
        return updateById(bean);
    }

    //根据用户id查询所在组
    public SysGroup selectGroupByUserId(Integer userId) {
        List<SysGroupUser> groupUsers = sysGroupUserMapper.selectList(new LambdaQueryWrapper<SysGroupUser>().eq(SysGroupUser::getUserId, userId));
        if (CollUtil.isNotEmpty(groupUsers)) {
            SysGroupUser groupUser = groupUsers.get(0);
            return mainMapper.selectById(groupUser.getGroupId());
        }
        return null;
    }

}
