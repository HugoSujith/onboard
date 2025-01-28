package com.hugo.metalbroker.utils;

import com.hugo.metalbroker.exceptions.DeserializationFailureException;
import com.hugo.metalbroker.exceptions.RedisKeyNotFoundException;
import com.hugo.metalbroker.exceptions.SerializationFailureException;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RedisSpotDataUtils {
    private final RedisTemplate<String, byte[]> redisTemplate;

    public RedisSpotDataUtils(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, SpotItemsList value) {
        try {
            byte[] bytes = value.toByteArray();
            redisTemplate.opsForValue().set(key, bytes);

        } catch (Exception e) {
            throw new SerializationFailureException(this.getClass().getName());
        }
    }

    public SpotItemsList getValue(String key) {
        try {
            byte[] bytes = redisTemplate.opsForValue().get(key);
            if (bytes != null) {
                return SpotItemsList.parseFrom(bytes);
            }
        } catch (Exception e) {
            throw new DeserializationFailureException(this.getClass().getName());
        }
        return null;
    }

    public boolean hasData(String key) {
        if (key != null) {
            return redisTemplate.hasKey(key);
        } else {
            throw new RedisKeyNotFoundException(null);
        }
    }

    @Scheduled(fixedRate = 150000)
    public void deleteRegularly() {
        Dotenv dotenv = Dotenv.load();

        if (hasData(dotenv.get("REDIS_SPOT_GOLD_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_SPOT_GOLD_KEY"));
        }

        if (hasData(dotenv.get("REDIS_SPOT_SILVER_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_SPOT_SILVER_KEY"));
        }
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
