package com.zhouzhao.office.online_collaborative_office.common.components;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class RedisHandler {
    /**
     * 框架自带的stringRedisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * String-Object
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisHandler() {
    }


    /**
     * 批量删除对应的value
     *
     * @param keys 所有要删除的key
     */
    public void remove(final String... keys) throws GlobalException {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern 根据pattern匹配删除所有keys
     */
    public void removePattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key 删除单个key
     */
    public void remove(final String key) throws GlobalException {
        if (exists(key)) {
            Boolean ret = stringRedisTemplate.delete(key);
            if (Boolean.FALSE.equals(ret)) {
                throw new GlobalException(RespCodeEnum.ERR_REDIS_REMOVE);
            }
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key 键值
     * @return true 有 false 没有
     */
    public boolean exists(final String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 获取所有的key
     *
     * @return 所有key的集合
     */
    public Set<String> keys() {
        return stringRedisTemplate.keys("*");
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(final String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 读取string缓存
     *
     * @param key 键
     * @return 值
     */
    public String getString(final String key) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 按类型读取缓存
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>   泛型
     * @return T
     */
    public <T> T get(final String key, Class<T> clazz) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String json = operations.get(key);

        return JSONObject.parseObject(json, clazz);
    }

    /**
     * 按类型读取List缓存
     *
     * @param key           键
     * @param typeReference 类型
     * @param <T>           泛型
     * @return List
     */
    public <T> List<T> getList(final String key, TypeReference<List<T>> typeReference) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String json = operations.get(key);
        return JSONObject.parseObject(json, typeReference);
    }

    /**
     * 写入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(final String key, Object value) throws GlobalException {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 写入缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间(秒)
     */
    public void set(final String key, Object value, long expireSeconds) throws GlobalException {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 写入缓存
     *
     * @param key      键
     * @param value    值
     * @param timeout  超时时间
     * @param timeUnit 超时时间单位
     */
    public void set(final String key, Object value, long timeout, TimeUnit timeUnit) throws GlobalException {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 写入缓存(String)
     *
     * @param key   键
     * @param value 值
     */
    public void setString(final String key, String value) throws GlobalException {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 写入缓存(String)
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时时间(秒)
     */
    public void setString(final String key, String value, long timeout) throws GlobalException {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 写入缓存(String)
     *
     * @param key      键
     * @param value    值
     * @param timeout  超时时间
     * @param timeUnit 超时时间单位
     */
    public void setString(final String key, String value, long timeout, TimeUnit timeUnit) throws GlobalException {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(RespCodeEnum.ERR_REDIS_SET);
        }
    }

    /**
     * 更新缓存
     *
     * @param key   键
     * @param value 值
     */
    public void update(final String key, Object value) throws GlobalException {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, 0);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_UPDATE);
        }
    }

    /**
     * 更新缓存(String)
     *
     * @param key   键
     * @param value 值
     */
    public void updateString(final String key, String value) throws GlobalException {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value, 0);
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_REDIS_UPDATE);
        }
    }

    /**
     * 设置超时
     *
     * @param key        键
     * @param expireTime 超时时间
     */
    public void expire(final String key, long expireTime) {
        stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 设置超时
     *
     * @param key        键
     * @param expireTime 超时时间
     * @param timeUnit   超时时间单位
     */
    public void expire(final String key, long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, expireTime, timeUnit);
    }


}
