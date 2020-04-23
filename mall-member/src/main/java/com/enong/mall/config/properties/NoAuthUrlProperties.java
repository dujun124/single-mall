package com.enong.mall.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;

@ConfigurationProperties(prefix = "member.auth")
@Data
public class NoAuthUrlProperties {
    private LinkedHashSet<String> shouldSkipUrls;
}
