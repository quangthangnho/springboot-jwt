package com.learnudemy.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnudemy.constant.SecurityConstant;
import com.learnudemy.exception.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponse httpResponse = new HttpResponse(
                HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase(), SecurityConstant.FORBIDDEN_MESSAGE
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
