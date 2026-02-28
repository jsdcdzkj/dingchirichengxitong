package com.jsdc.worktime.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SatokenConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // 注册 Sa-Token 拦截器，定义详细认证规则
            registry.addInterceptor(new SaInterceptor(handler -> {
                // 登录match 规则
                SaRouter.match("/**")    // 拦截的 path 列表，可以写多个 */
                        .notMatch("/login","/loginPhone","/login_client","/sendMessage")        // 排除掉的 path 列表，可以写多个
                        .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式

            })).addPathPatterns("/**").order(-10000).excludePathPatterns(
                    "/contribution/**","/sysGroup/groupList","/taskInfo/degreeOfDifficulty","/taskInfo/taskInfoListByGroup","/taskInfo/milestone"
                    );
        }

    /**
     * 注册 Sa-Token全局过滤器，解决跨域问题
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 拦截与排除 path
                .addInclude("/**").addExclude("/favicon.ico")

                // 全局认证函数
                .setAuth(obj -> {
                    // ...
                })

                // 异常处理函数
                .setError(e -> {
                    return AjaxJson.getError(e.getMessage());
                })

                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
                            .back();
                })
                ;
    }
}
