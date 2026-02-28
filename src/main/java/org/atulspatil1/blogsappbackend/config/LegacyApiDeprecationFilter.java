package org.atulspatil1.blogsappbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LegacyApiDeprecationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isLegacyApi = path.startsWith(ApiRoutes.LEGACY_BASE + "/") && !path.startsWith(ApiRoutes.V1_BASE + "/");

        if (isLegacyApi) {
            response.setHeader("Deprecation", "true");
            response.setHeader("Sunset", "Wed, 30 Sep 2026 23:59:59 GMT");
            response.setHeader("Link", "</api/v1>; rel=\"successor-version\"");
        }

        filterChain.doFilter(request, response);
    }
}
