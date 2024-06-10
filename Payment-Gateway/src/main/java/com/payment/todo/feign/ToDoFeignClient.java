package com.payment.todo.feign;

import com.payment.todo.exception.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "todo-user")
public interface ToDoFeignClient {
    @PutMapping("/v1/user/user/is-subscribed")
    Response<Object> setSubscribeStatus(@RequestParam Long userId, @RequestParam Boolean isSubscribed);

    @GetMapping("/v1/user/find-all-users")
    Response<Object> allUsers();
}
