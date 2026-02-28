package com.jsdc.worktime.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.HashMap;

/**
 * 根据数据表自动生成bean, mapper, service, controller
 *
 * @author cjt
 * @since 2022/03/08 11:07
 **/
public class GeneratorCode {

    private final static String AUTHOR = "cjt";
    private final static String OUTPUT_DIR = "D:\\work";
    private final static String TABLE_PREFIX = "";
    private final static String PARENT = "com";

    private final static HashMap<String, String> TABLES = new HashMap() {{
        this.put("org_manage", "org");
    }};

    public static void main(String[] args) {
        TABLES.forEach(GeneratorCode::generator);
    }

    /**
     * 生成代码
     **/
    private static void generator(String table, String module) {
        //全局配置
        GlobalConfig config = new GlobalConfig();
        config.setActiveRecord(false) //是否支持AR模式
                .setAuthor(AUTHOR) //作者
                .setFileOverride(false)//文件覆盖
                .setOutputDir(OUTPUT_DIR)//生成路径
                .setMapperName("%sMapper")//
                .setXmlName("%sMapper")//
                .setServiceName("%sService") //
                .setControllerName("%sController")
                .setIdType(IdType.AUTO); //主键策略  AUTO=自动增长（配置表，数据量小），INPUT=系统生成（业务表，数据量大）

        //数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setDbType(DbType.MYSQL).setUrl("jdbc:mysql://192.168.0.93:3306/yb_worktime")
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUsername("root")
                .setPassword("rjgs@2016");
        //策略配置
        StrategyConfig stConfig = new StrategyConfig();
        stConfig.setCapitalMode(true)
                .setRestControllerStyle(true)   //api风格控制器
                .setControllerMappingHyphenStyle(true)  //URL中驼峰转连字符
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(table)
                .setTablePrefix(TABLE_PREFIX); //表前缀,将被从实体等名字中移除
        //包名策略
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent(PARENT)
                .setModuleName(module)//
                .setController("controller")//
                .setEntity("model")//
                .setService("service");
        AutoGenerator ag = new AutoGenerator().setGlobalConfig(config)
                .setDataSource(dsConfig)
                .setStrategy(stConfig)
                .setPackageInfo(pkConfig);
        ag.execute();
    }
}
