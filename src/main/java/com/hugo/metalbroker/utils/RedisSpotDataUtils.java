package com.hugo.metalbroker.utils;

import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import org.springframework.data.redis.core.RedisTemplate;
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
            throw new RuntimeException("Failed to serialize Protobuf object", e);
        }
    }

    public SpotItemsList getValue(String key) {
        try {
            byte[] bytes = (byte[]) redisTemplate.opsForValue().get(key);
            if (bytes != null) {
                return SpotItemsList.parseFrom(bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Protobuf object", e);
        }
        return null;
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
