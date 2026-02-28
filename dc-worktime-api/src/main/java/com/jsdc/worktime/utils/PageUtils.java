package com.jsdc.worktime.utils;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.config.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PageUtils {

    /**
     * 最大分页数量 2000
     */
    private static final int MAX_PAGE_SIZE = 2000;

    public static <T> Page<T> safePage(PageQuery query) {
        // 1. 分页基础参数处理
        int pageNum = (query.getPage() != null && query.getPage() > 0) ?
                     query.getPage() : 1;
        int pageSize = (query.getSize() != null && query.getSize() > 0) ?
                     Math.min(query.getSize(), MAX_PAGE_SIZE) : 10;

        // 2. 创建分页对象
        Page<T> mpPage = new Page<>(pageNum, pageSize);

        // 3. 排序处理（修复版）
        if (query.getSorts() != null && !query.getSorts().isEmpty()) {
            List<String> sorts = query.getSorts();
            for (String sort : sorts) { // 确保getSorts()返回List<String>
                // 3.1 分割排序参数
                String[] parts = sort.split("[,:|]");
                if (parts.length != 2) continue;

                // 3.2 提取并清理字段和方向
                String field = StrUtil.trim(parts[0]);
                String direction = StrUtil.trim(parts[1]);

                // 3.3 校验有效性
                if (field.isEmpty() ||
                    !(direction.equals("asc") || direction.equals("desc"))) {
                    continue;
                }

                // 3.4 添加排序规则
                mpPage.addOrder("desc".equals(direction) ?
                    OrderItem.desc(field) : OrderItem.asc(field));
            }
        }

        // 4. 默认排序兜底
        if (mpPage.getOrders() == null || mpPage.getOrders().isEmpty()) {
            mpPage.addOrder(OrderItem.desc("id"));
        }

        return mpPage;
    }

    public static <T> Wrapper<T> buildWrapper(Object params) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (params == null) return wrapper;

        // 使用反射自动构建查询条件
        Arrays.stream(params.getClass().getDeclaredFields())
            .filter(f -> {
                try {
                    f.setAccessible(true);
                    return f.get(params) != null;
                } catch (IllegalAccessException e) {
                    return false;
                }
            })
            .forEach(f -> {
                try {
                    wrapper.eq(com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(f.getName()),
                              f.get(params));
                } catch (IllegalAccessException ignored) {}
            });

        return wrapper;
    }


    // 是否是 mybatis-plus 分页
    private final static boolean adjustPageIndex = true;
    /**
     * 将Spring Pageable转换为MyBatis-Plus的Page对象
     * @param pageable Spring分页对象
//     * @param adjustPageIndex 是否转换页码（Spring从0开始，MP默认从1开始）
     */
    public static <T> Page<T> convert(Pageable pageable) {
        Page<T> mpPage = new Page<>();
        mpPage.setCurrent(adjustPageIndex ? pageable.getPageNumber() + 1 : pageable.getPageNumber());
        mpPage.setSize(pageable.getPageSize());
        mpPage.setOrders(convertSort(pageable.getSort()));
        return mpPage;
    }

    private static List<OrderItem> convertSort(Sort sort) {
        return sort.stream()
                .map(order -> new OrderItem(
                        order.getProperty(),
                        order.isAscending()))
                .collect(Collectors.toList());
    }
}