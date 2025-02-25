package com.sias.interviewHelper.manager;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 通用计数器（可用于实现频率统计、限流、封禁等等）
 * 自定义反爬虫 使用 Redis 的 Lua 脚本来完成
 *
 *
 * 还可以用策略模式实现多种计数器，比如基于 Java 中并发包 LongAdder 的本地计数器，开发者可以自主选择使用何种计数器。
 * 而且还可以将本地计数器作为 Redis 计数器统计异常的降级，这样即使项目没有配置 Redis 也可以使用。
 *
 * 3、优化频率统计
 * 目前每次统计都要请求 Redis，频率非常高的情况下，可能会对 Redis 造成压力。可以先本地聚合统计结果，
 * 每隔一段时间再统一发送给 Redis。这也是之前讲过的 Hotkey 的统计原理，在高并发点赞系统中，也可以运用类似的设计思路。
 * 当然，也可以直接尝试结合 Hotkey 或 Sentinel 优化频率统计的精准度（采用滑动窗口统计）。
 *
 */
@Slf4j
@Service
public class CounterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 增加并返回计数，默认统计一分钟内的计数结果
     *
     * @param key 缓存键
     * @return
     */
    public long incrAndGetCounter(String key) {
        return incrAndGetCounter(key, 1, TimeUnit.MINUTES);
    }

    /**
     * 增加并返回计数
     *
     * @param key          缓存键
     * @param timeInterval 时间间隔
     * @param timeUnit     时间间隔单位
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit) {
        int expirationTimeInSeconds;
        switch (timeUnit) {
            case SECONDS:
                expirationTimeInSeconds = timeInterval;
                break;
            case MINUTES:
                expirationTimeInSeconds = timeInterval * 60;
                break;
            case HOURS:
                expirationTimeInSeconds = timeInterval * 60 * 60;
                break;
            default:
                throw new IllegalArgumentException("Unsupported TimeUnit. Use SECONDS, MINUTES, or HOURS.");
        }

        return incrAndGetCounter(key, timeInterval, timeUnit, expirationTimeInSeconds);
    }

    /**
     * 增加并返回计数
     *
     * @param key                     缓存键
     * @param timeInterval            时间间隔
     * @param timeUnit                时间间隔单位
     * @param expirationTimeInSeconds 计数器缓存过期时间
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit, long expirationTimeInSeconds) {
        if (StrUtil.isBlank(key)) {
            return 0;
        }

        // 根据时间粒度生成 Redis Key
        long timeFactor;
        switch (timeUnit) {
            case SECONDS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval;
                break;
            case MINUTES:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 60;
                break;
            case HOURS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 3600;
                break;
            default:
                throw new IllegalArgumentException("不支持的单位");
        }

        String redisKey = key + ":" + timeFactor;

        // Lua 脚本
        String luaScript =
                "if redis.call('exists', KEYS[1]) == 1 then " +
                        "  return redis.call('incr', KEYS[1]); " +
                        "else " +
                        "  redis.call('set', KEYS[1], 1); " +
                        "  redis.call('expire', KEYS[1], ARGV[1]); " +
                        "  return 1; " +
                        "end";

        // 执行 Lua 脚本
        RScript script = redissonClient.getScript(IntegerCodec.INSTANCE);
        Object countObj = script.eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(redisKey), expirationTimeInSeconds
        );
        return (long) countObj;
    }
}














