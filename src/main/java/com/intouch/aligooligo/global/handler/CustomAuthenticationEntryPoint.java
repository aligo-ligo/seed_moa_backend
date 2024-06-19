package com.intouch.aligooligo.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouch.aligooligo.global.exception.ErrorMessage;
import com.intouch.aligooligo.global.exception.ErrorMessageDescription;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage() + " : " + request.getRequestURI());

        ErrorMessage errorMessage = new ErrorMessage(ErrorMessageDescription.AUTHENTICATION_ENTRY_POINT.getDescription());
        String responseBody = objectMapper.writeValueAsString(errorMessage);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
