package yandex.practicum.market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import yandex.practicum.market.dto.UserDto;
import yandex.practicum.market.entity.UserEntity;
import yandex.practicum.market.mapper.UserMapper;
import yandex.practicum.market.repository.UserRepository;


import java.util.List;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDto> findByName(String username) {
        return repository.findByUsername(username)
                .map(userMapper::toUserDto);
    }

    @Override
    public Mono<String> registerUser(UserDto userDto, ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .defaultIfEmpty(new SecurityContextImpl())
                .handle((securityContext, sink) -> {
                    var authentication = securityContext.getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        sink.error(new IllegalArgumentException("Вы уже зарегистрированы"));
                    } else {
                        sink.next(userDto.getUsername());
                    }
                })
                .flatMap(v ->
                        findByName(userDto.getUsername())
                                .flatMap(u -> Mono.error(new IllegalArgumentException("Пользователь с таким именем уже существует")))
                                .switchIfEmpty(Mono.just(userDto.getUsername()))
                )
                .flatMap(name -> {
                    UserDetails userDetails = User.withUsername(userDto.getUsername())
                            .password(userDto.getPassword())
                            .passwordEncoder(passwordEncoder::encode)
                            .build();
                    return insertUser(userDto.getUsername(), userDetails.getPassword());
                })
                .flatMap(userEntity ->
                        exchange.getSession()
                                .doOnNext(session -> {
                                    SecurityContextImpl securityContext = new SecurityContextImpl();
                                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                            userEntity.getUsername(),
                                            userEntity.getPassword(),
                                            List.of()
                                    );
                                    securityContext.setAuthentication(authentication);

                                    session.getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
                                })
                                .flatMap(WebSession::changeSessionId)
                                .thenReturn("redirect:/"));
    }

    private Mono<UserEntity> insertUser(String username, String password) {
        return repository.save(UserEntity.builder()
                .username(username)
                .password(password)
                .build());
    }
}
