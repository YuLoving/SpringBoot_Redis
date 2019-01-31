package com.nj.redisconfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import redis.clients.jedis.JedisPool;

//<bean id="jedisPool" class="redis.clients.jedis.JedisPool">
//<constructor-arg name="host" value="192.168.0.110"/>
//<constructor-arg name="port" value="6379"/>
//</bean>
//<!-- 连接redis单机版 -->
//<bean id="jedisClientPool" class="com.yychao.redis.impl.JedisClientPool">
//<property name="jedisPool" ref="jedisPool"/>
//</bean>
@Configuration
@ImportResource(locations = {"classpath:applicationContext-redis.xml"})
public class JedisConfig {
   /* //将Jedis注入spring容器
    @Bean(name="jedisPool")
    public JedisPool jedisPool(){
        //redis的IP 和 端口号
        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        return  jedisPool;
    }

    @Bean
    public JedisClientPool jedisClientPool(@Qualifier("jedisPool") JedisPool jedisPool){
        JedisClientPool jedisClientPool = new JedisClientPool();
        jedisClientPool.setJedisPool(jedisPool);
        return new JedisClientPool();
    }*/

}
