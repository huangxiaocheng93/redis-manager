package com.vincent.h.config;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description redis集群对象管理
 *
 * @author huangxiaocheng
 * @date 2020/2/22 17:50
 */
public class RedisClusterConnect {

    /**
     * 创建jedisCluster客户端
     *
     * @param hostAndPort
     * @param password
     * @return
     */
    public static JedisCluster createJedisCluster(String hostAndPort, String password) {

        JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();

        //2 根据配置构建hostAndPorts
        Set<HostAndPort> hostAndPorts = Arrays.asList(hostAndPort.split(",")).stream().map(s -> {
            String[] split = s.split(":");
            return new HostAndPort(split[0], Integer.valueOf(split[1]));
        }).collect(Collectors.toSet());
        return new JedisCluster(hostAndPorts, 1000, 1000, 3, password, jedisPoolConfig);
    }

    private static JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(10);
        config.setMaxWaitMillis(1000);
        return config;
    }
}
