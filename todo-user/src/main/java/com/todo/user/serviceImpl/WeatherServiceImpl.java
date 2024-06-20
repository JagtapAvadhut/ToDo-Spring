package com.todo.user.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.user.exception.Response;
import com.todo.user.service.RedisService;
import com.todo.user.service.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherServiceImpl implements Weather {

    //    private static final String WETHERKEY = "a1daa629b82441ffada6beb4c93bc93e";

    @Autowired
    private RedisService redisService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${weatherbit.api.key}")
    private String apiKey;

    @Value("${weatherbit.api.url}")
    private String apiUrl;


    public Response<Object> getCurrentWeather(String city) {
        try {
            String name = city;
            if (redisService.find(city) != null) {
                return new Response<>(200, "success", redisService.find(city));
            } else {
                String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("city", city)
                        .queryParam("key", apiKey)
                        .toUriString();

                Object object = restTemplate.getForObject(url, Object.class);
                redisService.save(city, object);
                return new Response<>(200, "success", object);

            }
        } catch (
                Exception e) {
            return null;
        }
    }
}
