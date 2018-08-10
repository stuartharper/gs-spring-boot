package hello;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;


@Configuration("CacheConfiguration")
public class RedisConfig {
    Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redis.hosts}")
    private String redisHosts;
    @Value("${redis.dynamicRefreshSources}")
    private Boolean redisDynamicRefreshSources;

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
        List<String> clusterNodes = Arrays.asList(redisHosts.split(","));
        int refreshInterval = 10;
        /*
        In case of failover within the cluster, the client may need to refresh it's knowledge of the
        cluster nodes. The periodic refresh interval defaults to 1 minute
         */
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(refreshInterval))
                .dynamicRefreshSources(redisDynamicRefreshSources)
                .build();

        ClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().clientOptions(clientOptions).build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }
}
