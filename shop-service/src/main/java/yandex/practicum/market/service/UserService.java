package yandex.practicum.market.service;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.UserDto;


public interface UserService {

    Mono<UserDto> findByName(String name);

    Mono<String> registerUser(UserDto userDto, ServerWebExchange exchange);
}
