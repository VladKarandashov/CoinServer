package com.example.coinserver.exception;

import com.example.coinserver.common.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@Component
@Slf4j
public class DefaultExceptionHandler extends DefaultHandlerExceptionResolver {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error("Validation error", ex);
        var res = new GenericResponse<>(1200, "VALIDATION_ERROR");
        response.setStatus(OK.value());
        response.setContentType("application/json");
        try {
            response.getWriter().write(mapper.writeValueAsString(res));
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ModelAndView();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}