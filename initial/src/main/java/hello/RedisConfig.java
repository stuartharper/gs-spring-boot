package hello;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;


@Configuration("CacheConfiguration")
public class RedisConfig {
    Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    RedisTemplate<String, Object> getRedisTemplate() {
        RedisTemplate<String, Object> template = null;
        try {
            RedisConnectionFactory factory = getConnectionFactory();
            if (factory != null) {
                template = new RedisTemplate<>();
                template.setConnectionFactory(factory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new StringRedisSerializer());
            } else {
                LOGGER.error("Failed to connect to Redis.");
            }
        } catch(Exception ex) {
            LOGGER.error("Failed to initialise Redis template.", ex);
        }
        return template;
    }


    @Bean
    public RedisConnectionFactory getConnectionFactory() {
        List<String> clusterNodes = Arrays.asList("server1:7001".split(","));
        int refreshInterval = 60;
        boolean dynamicRefreshSources = false;
        /*
        In case of failover within the cluster, the client may need to refresh it's knowledge of the
        cluster nodes. The periodic refresh interval defaults to 1 minute
         */
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh()
                .dynamicRefreshSources(dynamicRefreshSources)
                .build();

        ClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().clientOptions(clientOptions).build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }
}
