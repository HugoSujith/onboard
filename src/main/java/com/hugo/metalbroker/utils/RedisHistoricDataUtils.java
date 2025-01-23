package com.hugo.metalbroker.utils;

import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    public boolean hasData(String key) throws Exception {
        if (key != null) {
            return redisTemplate.hasKey(key);
        } else {
            throw new Exception("Error");
        }
    }

    @Scheduled(fixedRate = 2000)
    public void deleteRegularly() throws Exception {
        Dotenv dotenv = Dotenv.load();

        if (hasData(dotenv.get("REDIS_HISTORIC_GOLD_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_HISTORIC_GOLD_KEY"));
        }

        if (hasData(dotenv.get("REDIS_HISTORIC_SILVER_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_HISTORIC_SILVER_KEY"));
        }

        if (hasData(dotenv.get("REDIS_SPOT_GOLD_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_SPOT_GOLD_KEY"));
        }

        if (hasData(dotenv.get("REDIS_SPOT_SILVER_KEY"))) {
            redisTemplate.delete(dotenv.get("REDIS_SPOT_SILVER_KEY"));
        }

    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
