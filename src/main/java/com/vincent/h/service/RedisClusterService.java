package com.vincent.h.service;

import com.vincent.h.config.RedisClusterConnect;
import com.vincent.h.model.RedisConfigList;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description redis集群服务
 *
 * @author huangxiaocheng
 * @date 2020/2/24 14:20
 */
public class RedisClusterService {

    private JedisCluster jedisCluster;

    private String hostAndPort;

    private String password;

    /**
     * 初始化redis集群连接
     *
     * @param profile
     */
    public void initRedisClusterConnection(String profile) {
        try{
            if(jedisCluster != null) {
                jedisCluster.close();
            }
        } catch (IOException E) {

        } finally {
            jedisCluster = null;
        }
        initHostAndPort(profile);
        jedisCluster = RedisClusterConnect.createJedisCluster(hostAndPort, password);
    }

    /**
     * 获取key类型
     *
     * @param key
     * @return
     */
    public String type(String key) {
        return jedisCluster.type(key);
    }

    /**
     * 获取String类型value
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return jedisCluster.get(key);
    }

    /**
     * 获取hashMap类型value
     *
     * @param key
     * @return
     */
    public Map<String, String> getHashMap(String key) {
        return jedisCluster.hgetAll(key);
    }

    /**
     * 获取key过期时间
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    /**
     * 获取所有key列表
     *
     * @return
     */
    public List<String> getAllKeys() {
        Object[] keys = clusterKeys("*").toArray();
        List<String> keyList = new ArrayList<>();
        Arrays.stream(keys).forEach(x -> keyList.add(x.toString()));
        return keyList;
    }

    /**
     * redis 删除key
     *
     * @param key
     */
    public void del(String key) {
        jedisCluster.del(key);
    }

    /**
     * 遍历所有节点的key
     *
     * @param pattern
     * @return
     */
    private TreeSet<String> clusterKeys(String pattern){
        TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()){
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch(Exception e){
            } finally{
                connection.close();
            }
        }
        return keys;
    }

    /**
     * 初始化连接信息
     *
     * @param profile
     */
    private void initHostAndPort(String profile) {
        ConfigService configService = ServiceFactory.getService(ConfigService.class);
        RedisConfigList redisConfigList = configService.queryAllConfig();
        redisConfigList.getConfigList().forEach(x -> {
            if(x.getName().equals(profile)) {
                hostAndPort = x.getHostAndPort();
                password = x.getPassword();
            }
        });
    }
}
