package com.miracle.memberservice.filter;

import com.miracle.memberservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends HttpFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final List<String> excludeUriPatterns = new ArrayList<>();
    private final TokenService tokenService;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    public void addExcludeUriPattern(String... antPatterns) {
        excludeUriPatterns.addAll(List.of(antPatterns));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        httpServletRequest = request;
        httpServletResponse = response;

        String uri = httpServletRequest.getRequestURI();
        if (isExcluded(uri)) {
            chain.doFilter(request, response);
            return;
        }

        String bearerToken = getBearerToken();
        if (bearerToken == null || !tokenService.validateToken(bearerToken)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isExcluded(String uri) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : excludeUriPatterns) {
            if (antPathMatcher.match(pattern, uri)) return true;
        }

        return false;
    }

    private String getBearerToken() {
        String bearerToken = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
