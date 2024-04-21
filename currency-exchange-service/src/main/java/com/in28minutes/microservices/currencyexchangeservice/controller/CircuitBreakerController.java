package com.in28minutes.microservices.currencyexchangeservice.controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * TODO: Resillience4j kütüphanesi ile CircuitBreaker özelliğini kullanarak hata durumunda fallback
 * methodunu çalıştırma işlemi gerçekleştirilecek ve test edilecek aynı zamanda  retry özelliği ile hata durumunda belirli  bir süre sonra tekrar deneme işlemi yapılacak
 *
 * Bu örnekte circuit breaker test etmek için linux terminalde aşağıdaki komutu çalıştırabilirsiniz
 * watch -n 1 curl http://localhost:8080/sample-api
 * her bir saniye de bir sample-api endpointine istek atacak ve hata durumunda fallback methodunu çalıştıracak
 *
 * ancak watch komutunu çalıştırmadan önce local'e watch kurmak gerekir.
 *
 */


/**
 * Docker events
 * docker top <container-id>
 * docker stats
 * docker run -m 512m --memory-swap 1g -p 8080:8080 -d <image-name>
 * --cpu-quota 5000 %5 kadar kullanım hakkı
 * docker system df
 */

@RestController
public class CircuitBreakerController {


    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);


    @GetMapping("/sample-api")
    //@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
    @CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    @RateLimiter(name = "default") // 10 saniyede 10 requesti geçemez gibi bir kısıtlama getirir
    //@Bulkhead(name = "sample-api") özelliği ile aynı anda kaç requesti karşılayabileceğini belirleyebiliriz
    /* application.properties file daha özellikli yapılandırabilir*/
    public String sampleApi() {
        // retry default olarak 3 defa deneyecek
        // fakat configuration edilirse sample-api göre config de tanımlanmışsa oradaki değerler geçerli olacak
        // orda kaç saniye de bi retry etceği ayarlanabilir ve kaç defa deneyeceği ve expenential backoff ayarlanır ve fallback methodu belirlenir
        logger.info("Sample API call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
                String.class);
        return forEntity.getBody();
    }

    public String hardcodedResponse(Exception ex) {
        return "fallback-response";
    }


}
