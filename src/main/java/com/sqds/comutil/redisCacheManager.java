package com.sqds.comutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



import java.time.Duration;

@Configuration
@EnableCaching
public class redisCacheManager {

    private RedisConnectionFactory connectionFactory;



    //定义redis Driver
    @Bean("redisdriver")
    public RedisConnectionFactory RedisConnectionFactory(){
        return  new LettuceConnectionFactory(new RedisStandaloneConfiguration("10.18.8.32",6379));
    }

    //定义redis template
    @Bean("redisServerTemplate")
    public RedisTemplate<String,Object> functionDomaiTemplate(@Qualifier("redisdriver") RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate();
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisTemplate<String,Object> redist=new RedisTemplate<>();
        GenericJackson2JsonRedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    /**
     * 配置RedisCacheManager
     * @param factory
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(@Qualifier("redisdriver") RedisConnectionFactory factory)
    {
        RedisCacheConfiguration configurtion=RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(factory)
                .cacheDefaults(configurtion)
                .transactionAware()
                .build();
    }
}
