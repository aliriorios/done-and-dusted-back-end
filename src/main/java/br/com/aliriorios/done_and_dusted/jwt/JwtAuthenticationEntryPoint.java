package br.com.aliriorios.done_and_dusted.jwt;

import br.com.aliriorios.done_and_dusted.web.exception.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Http Status 401 {}", authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("www-authenticate", "Bearer realm='/api/v1/auth'");
        response.setContentType("application/json");

        ErrorMessage error = new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Unauthorized user");

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
