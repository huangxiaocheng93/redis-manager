package com.vincent.h.model;

import lombok.Data;

/**
 * @description 
 *
 * @author huangxiaocheng
 * @date 2020/2/25 13:45
 */
@Data
public class RedisConfig {

    private String name;

    private String type;

    private String hostAndPort;

    private String password;
}
