package com.mask.api.global.jwt;

import com.mask.api.domain.user.domain.User;
import com.mask.api.domain.user.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthenticationFilter extends GenericFilterBean {

    private final UserService userService;
    private final RedisTemplate<String,String> redisTemplate;

    public AuthenticationFilter(UserService userService,RedisTemplate<String,String>  redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    //Jwt가 유효한 토큰인지 인증하기 위한 Filter이다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = parseToken((HttpServletRequest) request);

        if(token != null){
            var result = JwtTokenProvider.verify(token);
            if(result.isSuccess()) {
                String isLogout = redisTemplate.opsForValue().get(token);
                // 로그아웃 되어 있는지 검사
                if (ObjectUtils.isEmpty(isLogout)) {
                    var user = (User) userService.loadUserByUsername(result.getEmail());

                    var usernamePasswordToken = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordToken);
                }
            }
        }

        chain.doFilter(request,response);
    }
    private String parseToken(HttpServletRequest request){
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearer == null  || !bearer.startsWith("Bearer "))
            return null;
        return bearer.substring("Bearer ".length());

    }
}
