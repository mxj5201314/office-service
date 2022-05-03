package com.zhouzhao.office.online_collaborative_office;

import cn.hutool.core.util.StrUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.SystemConstants;
import com.zhouzhao.office.online_collaborative_office.dao.SysConfigDao;
import com.zhouzhao.office.online_collaborative_office.entity.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication
@ServletComponentScan
@Slf4j
@EnableAsync
@EnableConfigurationProperties
public class OnlineCollaborativeOfficeApplication {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private SystemConstants constants;

    public static void main(String[] args) {
        SpringApplication.run(OnlineCollaborativeOfficeApplication.class, args);
    }

    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigDao.selectAllParam();
        list.forEach(one -> {
            String key = one.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = constants.getClass().getDeclaredField(key);
                field.set(constants, value);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        });
    }

}
