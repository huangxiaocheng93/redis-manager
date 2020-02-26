package com.vincent.h.service;

import com.vincent.h.model.RedisConfig;
import com.vincent.h.model.RedisConfigList;
import com.vincent.h.util.FileUtils;
import com.vincent.h.util.JacksonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @description 
 *
 * @author huangxiaocheng
 * @date 2020/2/25 13:49
 */
public class ConfigService {

    public boolean andConfig(RedisConfig redisConfig) {
        String content = FileUtils.readConfigFile();
        if(content == null || "".equals(content)) {
            RedisConfigList redisConfigList = new RedisConfigList();
            List<RedisConfig> configList = new ArrayList<>();
            configList.add(redisConfig);
            redisConfigList.setConfigList(configList);
            content = JacksonUtils.encode(redisConfigList);
            FileUtils.writeConfigFile(content);
            return true;
        }
        RedisConfigList redisConfigList = JacksonUtils.decode(content, RedisConfigList.class);
        Optional optional = redisConfigList.getConfigList().stream()
                .filter(x -> x.getName().equals(redisConfig.getName())).findAny();
        if(optional.isPresent()) {
            return false;
        }
        redisConfigList.getConfigList().add(redisConfig);
        content = JacksonUtils.encode(redisConfigList);
        FileUtils.writeConfigFile(content);
        return true;
    }

    public RedisConfigList queryAllConfig() {
        String content = FileUtils.readConfigFile();
        if(content == null || "".equals(content)) {
            return null;
        }
        return JacksonUtils.decode(content, RedisConfigList.class);
    }
}
