package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by andy.mccann on 15-Feb-17.
 */
@Component
public class AccessRedis{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRedis.class);
    protected final RedisConfig cacheConfiguration;

    /**
     * Inject cacheConfiguration into this component
     * @param cacheConfiguration
     */
    public AccessRedis(RedisConfig cacheConfiguration){
        this.cacheConfiguration = cacheConfiguration;
    }

    public byte[] getObject(String key) {
        RedisConnection conn = null;
        byte[] value = null;
        try {
            //get connection to Redis
            conn = cacheConfiguration.getConnectionFactory().getConnection();

            //retrieve value
            value = conn.get(key.getBytes(StandardCharsets.UTF_8));
        } catch(Exception ex)    {
            LOGGER.error("Exception while retrieving cache value", ex);
        } finally {
            //close connection
            if(conn != null){
                conn.close();
            }
        }
        return value;
    }

    public void saveObject(String key, byte[] object, int timeToLive) {
        RedisConnection conn = null;

        try{
            //get connection to Redis
            conn = cacheConfiguration.getConnectionFactory().getConnection();

            //set value in Redis
            conn.setEx(key.getBytes(StandardCharsets.UTF_8), timeToLive, object);
        } catch (Exception ex) {
            LOGGER.error("Exception while setting cache value", ex);
        } finally {
            //close connection
            if(conn != null){
                conn.close();
            }
        }
    }

}
