package com.jsdc.worktime.service.sys_;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.global.G;
import com.jsdc.worktime.mapper.sys_.SysUserMapper;
import com.jsdc.worktime.service.BaseService;
import com.jsdc.worktime.sys_.SysMenu;
import com.jsdc.worktime.sys_.SysRoleMenu;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.sys_.SysUserRole;
import com.jsdc.worktime.utils.AjaxJson;
import com.jsdc.worktime.utils.TreeParserUtils;
import com.jsdc.worktime.utils.UnifiedPortalUtils;
import com.jsdc.worktime.vo.MetaVo;
import com.jsdc.worktime.vo.RouterVo;
import com.jsdc.worktime.vo.SysMenuVo;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SysUserService extends BaseService<SysUser> {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuService sysMenuService;


    /**
     * 登录方法
     * Author wzn
     * Date 2023/3/7 14:09
     */
    public AjaxJson login(String userName, String password) {

        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("userName", userName).eq("is_del", "0"));
        if (sysUser == null) {
            sysUser = this.getOne(new QueryWrapper<SysUser>().eq("phone", userName).eq("is_del", "0"));
        }
        JSONObject token = new JSONObject();
        if (sysUser != null) {
            if ("1".equals(sysUser.getStatus())) {
                return AjaxJson.getError("账号已锁定,请联系管理员！");
            }
//            try {
//                password= URLDecoder.decode(password, "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (StringUtils.hasText(password) && SaSecureUtil.md5(password).equals(sysUser.getPassword())) {
                sysUser.setPassword(password);
                HashMap<Boolean, Object> map = verifyPass(sysUser);
                Boolean check = (Boolean) map.get("check");
                if (check) {
                    StpUtil.login(sysUser.getId());
                    token.put("jsdcToken", StpUtil.getTokenValue());
                    List<SysMenu> authority = selectSysMenuList(this.getUser().getId());
                    token.put("permissions", buildMenus(authority));
                    token.put("user", this.getUser());
                    token.put("roles", StpUtil.getRoleList());
                    return AjaxJson.getSuccessData(token);
                } else {
                    return AjaxJson.getSuccessData(map);
                }

            } else {
                return AjaxJson.getError("密码错误！");
            }
        } else {
            return AjaxJson.getError("账号或密码不正确");
        }
    }

    public AjaxJson loginPhone(String userName) {

        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("userName", userName).eq("is_del", "0"));
        if (sysUser == null) {
            sysUser = this.getOne(new QueryWrapper<SysUser>().eq("phone", userName).eq("is_del", "0"));
        }
        JSONObject token = new JSONObject();
        if (sysUser != null) {
            if ("1".equals(sysUser.getStatus())) {
                return AjaxJson.getError("账号已锁定,请联系管理员！");
            }

            StpUtil.login(sysUser.getId());
            token.put("jsdcToken", StpUtil.getTokenValue());
            List<SysMenu> authority = selectSysMenuList(this.getUser().getId());
            token.put("permissions", buildMenus(authority));
            token.put("user", this.getUser());
            token.put("roles", StpUtil.getRoleList());
            return AjaxJson.getSuccessData(token);
        } else {
            return AjaxJson.getError("账号信息不正确");
        }
    }

    /**
     * 获取用户菜单
     *
     * @param id
     * @return
     */
    public List<SysMenu> selectSysMenuList(Integer id) {
        // 根据用户得到角色
        List<SysUserRole> userRoles = sysUserRoleService.getBaseMapper().selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getUser_id, id));
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        List<Integer> roleIds = userRoles.stream().map(SysUserRole::getRole_id).collect(Collectors.toList());
        // 根据角色得到菜单
        List<SysRoleMenu> roleMenus = sysRoleMenuService.getBaseMapper().selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .in(SysRoleMenu::getRole_id, roleIds));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }
        List<Integer> menuIds = roleMenus.stream().map(SysRoleMenu::getMenu_id).collect(Collectors.toList());

        // 根据菜单得到菜单信息 , 如果不是最上级菜单，需要查询父级菜单
        List<SysMenu> menus = sysMenuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, menuIds).eq(SysMenu::getIsDel, G.ISDEL_NO)
                // 查询已经显示的菜单,隐藏的菜单不显示
                .eq(SysMenu::getIsShow, 1)
                .eq(SysMenu::getMenuType, 1)
                .orderByAsc(SysMenu::getSort)
        );
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        // 根据父级菜单查询
        List<Integer> parentIds = menus.stream().map(SysMenu::getParentId).collect(Collectors.toList());
        List<SysMenu> parentMenus = sysMenuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery()
                .in(SysMenu::getId, parentIds).eq(SysMenu::getIsDel, G.ISDEL_NO)
                .orderByAsc(SysMenu::getSort)
        );
        if (!CollectionUtils.isEmpty(parentMenus)) {
            menus.addAll(parentMenus);
        }
        menus = menus.stream().distinct().collect(Collectors.toList());
        return menus;
    }


    /**
     * 组装菜单到路由
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        // {
        //    path: '/',
        //    component: 'Layout',
        //    redirect: 'index',
        //    children: [
        //      {
        //        path: 'index',
        //        name: 'Index',
        //        component: '@/views/index/index',
        //        meta: {
        //          title: '首页',
        //          icon: 'home',
        //          affix: true,
        //        },
        //      },
        //    ],
        //  },

        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setId(String.valueOf(menu.getId()));
            router.setParentId(String.valueOf(menu.getParentId()));
            router.setPath(menu.getRouterUrl());
            router.setName(menu.getRouterName());
            router.setRedirect(menu.getRedirectType());
            router.setComponent(menu.getVueUrl());
            router.setComponentUrl(menu.getVueUrl());
            router.setMeta(new MetaVo(menu.getTitle(), menu.getIcon()));
            routers.add(router);
        }

        List<RouterVo> menus1 = TreeParserUtils.getTreeList("0", routers);


        return menus1;
    }


    /**
     * 获取用户
     *
     * @return
     */
    public SysUser getUser() {
        if (StpUtil.getLoginId() != null) {
            return this.getById((Serializable) StpUtil.getLoginId());
        }
        return null;
    }


    /**
     * 登出
     */
    public void logout() {
        StpUtil.logout();
    }


    /**
     * 新增用户
     *
     * @param sysUser
     * @return
     */
    public AjaxJson saveUser(SysUser sysUser) {
        SysUser currUser = getUser();

        //唯一性校验
        boolean check = this.checkOnly(sysUser);
        if (!check) {
            return AjaxJson.getError("身份证号码已存在,禁止重复添加！");
        }
        //唯一性校验
        boolean checkPhone = this.checkOnlyPhone(sysUser);
        if (!checkPhone) {
            return AjaxJson.getError("手机号已存在,禁止重复添加！");
        }
        //强密码验证
        if (null != sysUser && null != sysUser.getPassword()) {
            boolean checkPass = this.validateStrongPassword(sysUser.getPassword());
            if (!checkPass) {
                return AjaxJson.getError("密码规则说明：小写字母+大写字母+数字+特殊符号+长度为8-16位！");
            }
        }

        if (null != sysUser.getId()) {
            if (!StringUtils.isEmpty(sysUser.getPassword())) {
                //加密
                if (!sysUser.getPassword().equals(sysUserMapper.selectById(sysUser.getId()).getPassword())) {
                    sysUser.setPassword(SaSecureUtil.md5(sysUser.getPassword()));
                }
            }

            //修改
            sysUser.setUpdate_user(currUser.getId());
            sysUser.setUpdate_time(new Date());
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", sysUser.getId()));
        } else {
            //判断用户名是否重复
            List<SysUser> list = this.list(new QueryWrapper<SysUser>().eq("username", sysUser.getUsername()).eq("is_del", "0"));
            if (list != null && list.size() > 0) {
                return AjaxJson.getError("用户名重复！");
            }

            //加密
            sysUser.setPassword(SaSecureUtil.md5("Jsdc@0516"));
            //新增
            sysUser.setCreate_user(currUser.getId());
            sysUser.setCreate_time(new Date());
            sysUser.setIs_del("0");
            sysUser.setStatus("0");
        }

        sysUser.insertOrUpdate();
        if (!StringUtils.isEmpty(sysUser.getRoleIds())) {
            //新增用户角色关联数据
            for (Integer roleId : sysUser.getRoleIds()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRole_id(roleId);
                sysUserRole.setUser_id(sysUser.getId());
                sysUserRole.insert();
            }
        }

        return AjaxJson.getSuccess();
    }


    // 正则表达式用于验证密码强度
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";

    /**
     * 验证密码是否符合强密码规则
     *
     * @param password 待验证的密码
     * @return 如果密码符合规则返回true，否则返回false
     */
    public static boolean validateStrongPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    /**
     * 用户启用、禁用
     * Author wzn
     * Date 2023/3/24 13:58
     */
    public AjaxJson forbidden(SysUser sysUser) {
        SysUser currUser = getUser();

        //修改
        sysUser.setUpdate_user(currUser.getId());
        sysUser.setUpdate_time(new Date());
        sysUser.updateById();
        return AjaxJson.getSuccess();
    }

    /**
     * 身份证号唯一性校验
     * Author wzn
     * Date 2023/3/9 13:59
     */
    public boolean checkOnly(SysUser sysUser) {
        boolean checkOnly = true; //没有重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_no", sysUser.getCard_no());
        queryWrapper.eq("is_del", "0");
        List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
        if (!sysUserList.isEmpty()) {
            checkOnly = false;
            if (null != sysUser.getId()) {
                if (sysUser.getId().equals(sysUserList.get(0).getId())) {
                    checkOnly = true;
                }
            }
        }
        return checkOnly;
    }

    /**
     * 手机号唯一性校验
     * Author wzn
     * Date 2023/3/9 13:59
     */
    public boolean checkOnlyPhone(SysUser sysUser) {
        boolean checkOnly = true; //没有重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", sysUser.getPhone());
        queryWrapper.eq("is_del", "0");
        List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
        if (!sysUserList.isEmpty()) {
            checkOnly = false;
            if (null != sysUser.getId()) {
                if (sysUser.getId().equals(sysUserList.get(0).getId())) {
                    checkOnly = true;
                }
            }
        }
        return checkOnly;
    }

    /**
     * 修改密码
     * Author wzn
     * Date 2023/3/8 8:57
     */
    public AjaxJson pass(SysUser sysUser) {

        SysUser currUser = null;

        if (StringUtils.isEmpty(sysUser.getLoginName())) {
            currUser = getUser();
        } else {
            currUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", sysUser.getLoginName()).eq("is_del", "0"));
            ;
        }


        if (!currUser.getPassword().equals(SaSecureUtil.md5(sysUser.getTempPass()))) {
            return AjaxJson.getError("旧密码输入错误，请重新输入！");
        }

        //强密码验证
        if (null != sysUser && null != sysUser.getPassword()) {
            boolean checkPass = this.validateStrongPassword(sysUser.getPassword());
            if (!checkPass) {
                return AjaxJson.getError("密码规则说明：小写字母+大写字母+数字+特殊符号+长度为8-16位！");
            }
        }

        currUser.setPassword((SaSecureUtil.md5(sysUser.getPassword())));
        currUser.updateById();
        return AjaxJson.getSuccess();
    }


    public HashMap<Boolean, Object> verifyPass(SysUser user) {
        HashMap map = new HashMap();
        map.put("check", true);

        if (null == user) {
            map.put("check", false);
            map.put("msg", "用户不存在！");
            return map;
        }

        return map;
    }


    /**
     * 用户列表
     * Author wzn
     * Date 2023/3/8 14:00
     */
    public Page<SysUser> selectUserList(SysUser sysUser) {
        Page<SysUser> page = new Page<>(sysUser.getPageNo(), sysUser.getPageSize());
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0");
        if (!"".equals(sysUser.getUsername()) && null != sysUser.getUsername()) {
            queryWrapper.like("username", sysUser.getUsername());
        }
        if (!"".equals(sysUser.getPhone()) && null != sysUser.getPhone()) {
            queryWrapper.like("phone", sysUser.getPhone());
        }
        if (!"".equals(sysUser.getCard_no()) && null != sysUser.getCard_no()) {
            queryWrapper.eq("card_no", sysUser.getCard_no());
        }
        Page<SysUser> userPage = sysUserMapper.selectPage(page, queryWrapper);
        return userPage;
    }

    public AjaxJson syncAccountInfo() {
        // 工时用户
        List<SysUser> list = this.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIs_del, 0).isNotNull(SysUser::getPhone));
        if (list.size() > 0){
            return syncUser(list,"1");
        }else {
            return  AjaxJson.getError("没有需要同步的数据");
        }
    }

    @Value("${dcSync.portalUrl}")
    private String portalUrl;
    @Value("${dcSync.appid}")
    private String appid;
    @Value("${dcSync.privateKey}")
    private String privateKey;
    /**
     * 同步用户数据
     */
    public AjaxJson syncUser(List<SysUser> users, String isloginty) {

        String token = "";
        try {
            // 找到统一门户配置
            token = UnifiedPortalUtils.getToken(portalUrl, appid, privateKey);
            if (org.apache.commons.lang3.StringUtils.equals("500", token)) {
                return AjaxJson.getError("统一门户获取token错误,请检查参数接口等信息.");
            }
        } catch (Exception e) {
            return AjaxJson.getError("统一门户配置不正确,请联系管理员!");
        }

        List<JSONObject> params = new ArrayList<>();
        String enable = org.apache.commons.lang3.StringUtils.equals("1", isloginty) ? "1" : "2";
        for (SysUser user : users) {
            if (user.getPhone() == null || user.getPhone().isEmpty()) {
                continue;
            }
            JSONObject param = new JSONObject();
            param.put("biz_id", user.getId() + "");
            // 中文转拼音
//        param.put("loginName", convertToPinyin(user.getLoginName()));
            param.put("loginName", user.getPhone());
            param.put("phone", user.getPhone());
            param.put("operateType", enable);
            params.add(param);
        }
        try {
            UnifiedPortalUtils.syncAccountInfo(portalUrl, token, params);
            // 更新同步状态
            for (SysUser user : users) {
                user.setIsloginty(enable);
                this.updateById(user);
            }
        } catch (Exception e) {
            return AjaxJson.getError("同步用户数据失败,请联系维护管理员！");
        }

        return AjaxJson.getSuccess("同步完成");

    }

    public List<SysUser> selectGetListAll(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0");
        if (!"".equals(sysUser.getUsername()) && null != sysUser.getUsername()) {
            queryWrapper.like("username", sysUser.getUsername());
        }
        if (!"".equals(sysUser.getCard_no()) && null != sysUser.getCard_no()) {
            queryWrapper.eq("card_no", sysUser.getCard_no());
        }
        //根据用户首拼字母排序
        List<SysUser> list = sysUserMapper.selectList(queryWrapper);
        // 分离 admin 和普通用户
        List<SysUser> adminUsers = new ArrayList<>();
        List<SysUser> normalUsers = new ArrayList<>();

        for (SysUser user : list) {
            if (user.getUsername().startsWith("admin")) {
                adminUsers.add(user);
            } else {
                normalUsers.add(user);
            }
        }

        // 创建拼音格式化器
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        // 对普通用户进行排序
        normalUsers.sort(Comparator.comparing(
                user -> getPinyinFirstLetter(user.getRealName(), format),
                String::compareToIgnoreCase
        ));

        // 合并结果
        List<SysUser> sortedUsers = new ArrayList<>(adminUsers);
        sortedUsers.addAll(normalUsers);

        return sortedUsers;
    }

    // 获取姓名的拼音首字母（处理中英文混合）
    private static String getPinyinFirstLetter(String name, HanyuPinyinOutputFormat format) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char c : name.toCharArray()) {
            // 判断是否为中文字符
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    // 获取拼音数组（可能有多音字）
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        // 取第一个拼音的首字母
                        result.append(pinyinArray[0].charAt(0));
                    } else {
                        // 无法转换的中文字符，保留原字符
                        result.append(c);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 转换异常，保留原字符
                    result.append(c);
                }
            } else {
                // 非中文字符（如英文），直接保留
                result.append(c);
            }
        }
        return result.toString();
    }

    public List<SysUser> selectByGroupId(SysUser sysUser) {
        List<SysUser> list = sysUserMapper.selectByGroupId(sysUser);
        return list;
    }

    public void initPass() {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0");
        List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
        for (SysUser s : sysUserList) {
            s.setPassword(SaSecureUtil.md5(s.getCard_no().substring(s.getCard_no().length() - 6) + "@xzyb"));
            s.updateById();
        }


    }

    public List<RouterVo> userMenu(Integer id) {
        List<SysMenu> authority = selectSysMenuList(this.getUser().getId());
        return buildMenus(authority);
    }

    public List<SysMenuVo> userMenuTree(Integer id) {
        // 根据用户得到角色
        List<SysUserRole> userRoles = sysUserRoleService.getBaseMapper().selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getUser_id, id));
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        List<Integer> roleIds = userRoles.stream().map(SysUserRole::getRole_id).collect(Collectors.toList());
        // 根据角色得到菜单
        List<SysRoleMenu> roleMenus = sysRoleMenuService.getBaseMapper().selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .in(SysRoleMenu::getRole_id, roleIds));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }
        List<Integer> menuIds = roleMenus.stream().map(SysRoleMenu::getMenu_id).collect(Collectors.toList());

        SysMenu sysMenu = new SysMenu();
        sysMenu.setMenuIds(menuIds);

        List<SysMenuVo> sysMenuVoList = sysMenuService.getMenuTree1(sysMenu);
        return sysMenuVoList;
    }

    public static void main(String[] args) {
//        System.out.println(SaSecureUtil.md5("Jsdc@0516"));
        String pass = "Xujian..123";
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
        System.out.println(pass.matches(regex));
    }
}
