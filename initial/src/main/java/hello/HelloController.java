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

        redisCache.saveObject("Hello","World".getBytes(),60);

        return "Greetings from Spring Boot!";
    }
    
}
