package com.example.coinserver.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@UtilityClass
public class HttpUtils {

    public Optional<HttpServletRequest> getHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    public Optional<String> getToken() {
        return getHttpRequest()
                .map(httpServletRequest -> httpServletRequest.getHeader("token"))
                .map(token -> StringUtils.isNotBlank(token) ? token : null);
    }
}
