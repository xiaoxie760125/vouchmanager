package com.sqds.comutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisUtil {


  @Autowired
  @Qualifier("redisServerTemplate")
    private RedisTemplate redisTemplate;
   /* @Bean("redisconnect")
    public  RedisConnectionFactory connectionFactory()
    {
         RedisConnectionFactory redisConnectionFactory=new JedisConnectionFactory();
         return  redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String,Object> functionDomaiTemplate()
    {

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisTemplate<String,Object> redist=new RedisTemplate<>();
        GenericJackson2JsonRedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
       this. redisTemplate.setKeySerializer(stringSerializer);
       this. redisTemplate.setHashKeySerializer(stringSerializer);
       this. redisTemplate.setValueSerializer(serializer);
        this.redisTemplate.afterPropertiesSet();
       this. redisTemplate.setConnectionFactory(this.redisTemplate.getConnectionFactory());
        return this.redisTemplate;
    }
    @Bean
    public RedisCacheManager cacheManager(@Qualifier("redisconnect") RedisConnectionFactory factory)
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
    }*/
    public boolean haskey(String key)
    {
        return  redisTemplate.boundValueOps(key).persist();
    }
  /* public  RedisUtil(RedisTemplate redisTemplate)
    {
        this.redisTemplate=redisTemplate;
        RedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
        this.redisTemplate.setKeySerializer(stringSerializer);
        this.redisTemplate.setHashKeySerializer(stringSerializer);
        this.redisTemplate.setValueSerializer(serializer);
        this.redisTemplate.afterPropertiesSet();






    }*/
    public Object get(String key)
    {

        return  key==null?null:this.redisTemplate.opsForValue().get(key);
    }

    public  boolean isexitedata(String key)
    {
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }

    public  void  set(String key,Object value)
    {
        redisTemplate.opsForValue().set(key,value);
    }
    public <T>  void  setlist(String key,T value) throws NoSuchFieldException, IllegalAccessException {
        ListOperations<String, T>
                ob = (ListOperations<String, T>) redisTemplate.opsForList();

        List<T> a=ob.range(key,0,-1);
        if(!ob.range(key,0,-1).contains(value))
        {
            ob.leftPush(key,value);
        }
        else
        {
            ob.remove(key,0,value);
            ob.leftPush(key,value);
        }
    }
    public <T>  List<T>  getlist(String key) throws NoSuchFieldException, IllegalAccessException {
        ListOperations<String, T>
                ob = (ListOperations<String, T>) redisTemplate.opsForList();
        return ob.range(key,0,-1);
    }






}
