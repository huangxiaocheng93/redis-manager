package com.vincent.h.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description service单例工厂
 *
 * @author huangxiaocheng
 * @date 2020/2/25 8:27
 */
public class ServiceFactory {

    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 实例化service
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getService(Class<T> clazz) {
        try {
            if(serviceMap.containsKey(clazz.getName())) {
                return (T)serviceMap.get(clazz.getName());
            }
            Object object = clazz.newInstance();
            serviceMap.put(clazz.getName(), object);
            return (T)object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
