package org.atulspatil1.blogsappbackend.config;

import org.atulspatil1.blogsappbackend.api.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Set;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final Set<String> EXCLUDED_PATH_PREFIXES = Set.of(
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        String path = request.getURI().getPath();
        if (isExcluded(path) || body instanceof ApiResponse<?> || body instanceof String || body instanceof byte[]) {
            return body;
        }

        int status = HttpStatus.OK.value();
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }
        HttpStatusCode statusCode = HttpStatusCode.valueOf(status);
        if (status == HttpStatus.NO_CONTENT.value()) {
            return body;
        }

        HttpStatus httpStatus = statusCode != null ? HttpStatus.resolve(status) : HttpStatus.OK;
        String resolvedMessage = httpStatus != null ? httpStatus.getReasonPhrase() : "OK";

        return ApiResponse.success(status, resolvedMessage, body, path);
    }

    private boolean isExcluded(String path) {
        return EXCLUDED_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
