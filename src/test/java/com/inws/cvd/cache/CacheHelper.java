package com.inws.cvd.cache;

import com.inws.cvd.dto.StatisticByDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final RedisTemplate<String, StatisticByDate> redisTemplate;

    public void setContext() {
        // doNothing.
        // Required for additional unit tests (out of scope for test task).
    }

    public void clean() {
        var keys = redisTemplate.keys("*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
