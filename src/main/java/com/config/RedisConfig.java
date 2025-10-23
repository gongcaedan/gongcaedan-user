package com.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String redisUrl = dotenv.get("REDIS_URL");
        System.out.println("Redis 연결 시도 중: " + redisUrl);

        String host = "localhost";
        int port = 6379;

        if (redisUrl != null && redisUrl.startsWith("redis://")) {
            String stripped = redisUrl.replace("redis://", "");
            String[] parts = stripped.split(":");
            host = parts[0];
            port = Integer.parseInt(parts[1]);
        }

        System.out.println("실제 Redis 연결 → " + host + ":" + port);
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}