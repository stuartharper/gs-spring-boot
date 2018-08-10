package hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {
    protected final AccessRedis redisCache;

    public HelloController(AccessRedis redisCache) {
        this.redisCache = redisCache;
    }

    @RequestMapping("/")
    public String index() {
        String response = null;
        try {
            redisCache.saveObject("Hello", "World".getBytes(), 60);
            String returned = new String(redisCache.getObject("Hello"));
            response = "Redis up. Returned: " + returned;
        } catch(Exception e){
            response = "redis is down :( " + e.getMessage();
        }

        return response;
    }
    
}
