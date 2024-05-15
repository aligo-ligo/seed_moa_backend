package com.intouch.aligooligo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        log.error(accessDeniedException.getMessage() + " : " + request.getRequestURI());

        ErrorMessage errorMessage = new ErrorMessage(ErrorMessageDescription.AUTHORIZATION_DENIED.getDescription());
        String responseBody = objectMapper.writeValueAsString(errorMessage);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
