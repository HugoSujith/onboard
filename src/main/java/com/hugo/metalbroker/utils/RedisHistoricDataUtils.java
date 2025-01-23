package com.hugo.metalbroker.utils;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisHistoricDataUtils {
    private final RedisTemplate<String, byte[]> redisTemplate;

    public RedisHistoricDataUtils(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, HistoricItemsList value) {
        try {
            byte[] bytes = value.toByteArray();
            redisTemplate.opsForValue().set(key, bytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Protobuf object", e);
        }
    }

    public HistoricItemsList getValue(String key) {
        try {
            byte[] bytes = redisTemplate.opsForValue().get(key);
            if (bytes != null) {
                return HistoricItemsList.parseFrom(bytes);
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
