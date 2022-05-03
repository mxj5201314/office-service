package com.zhouzhao.office.online_collaborative_office.common.config;

import com.baidu.aip.face.AipFace;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "baidu.face")
@Setter
public class BaiDuConfig {
    //设置APPID/AK/SK
    private String appId;
    private String apiKey ;
    private String secretKey ;



    @Bean
    public AipFace aipFace() {
        // 初始化一个AipFace
        AipFace client = new AipFace(appId, apiKey, secretKey);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;

    }
}
