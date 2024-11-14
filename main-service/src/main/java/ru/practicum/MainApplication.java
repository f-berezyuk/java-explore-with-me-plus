package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.stat.dto.EndpointHit;

import java.time.LocalDateTime;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
       ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        System.out.println("Hit start: ");
        StatClient client = context.getBean(StatClient.class);
        EndpointHit hitDto = new EndpointHit();
        hitDto.setApp("ewm_plus");
        hitDto.setIp("127.0.0.1");
        hitDto.setUri("localhost");
        hitDto.setTimestamp(LocalDateTime.now());
        client.hit(hitDto);
        System.out.println("Hit ok: ");
    }
}