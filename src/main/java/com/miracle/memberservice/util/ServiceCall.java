package com.miracle.memberservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miracle.memberservice.dto.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class ServiceCall {

    private static final String BASE_URL = "http://localhost:";
    private static final String VERSION = "/v1";

    private static WebClient.Builder createWebClientBuilder(String serviceType) {
        return WebClient.builder()
                .baseUrl(BASE_URL + port(serviceType))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    private static Mono<ApiResponse> addCommonHeaders(WebClient.RequestHeadersSpec<?> request, HttpSession httpSession, String serviceType) {
        String capitalizeFirstLetter = capitalizeFirstLetter(serviceType);
        MiracleTokenKey key = new MiracleTokenKey(httpSession);

        return request
                .header(Const.RequestHeader.MIRACLE, key.getHashcode())
                .header(Const.RequestHeader.SESSION_ID, key.getSessionId())
                .header(capitalizeFirstLetter + Const.RequestHeader.HEADER_ID, String.valueOf(httpSession.getAttribute(Const.RequestHeader.ID)))
                .header(capitalizeFirstLetter + Const.RequestHeader.HEADER_EMAIL, (String) httpSession.getAttribute(Const.RequestHeader.EMAIL))
                .header(capitalizeFirstLetter + Const.RequestHeader.HEADER_BNO, (String) httpSession.getAttribute(Const.RequestHeader.BNO))
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .onErrorResume(handleError());
    }

    public static ApiResponse get(HttpSession httpSession, String serviceType, String url) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().get()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).build()), httpSession, serviceType).block();
    }

    public static ApiResponse post(HttpSession httpSession, Object dto, String serviceType, String url) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().post()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).build()).bodyValue(dto), httpSession, serviceType).block();
    }

    public static ApiResponse postParam(HttpSession httpSession, Object dto, String serviceType, String url, int strNum, int endNum) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().post()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).queryParam("strNum", strNum).queryParam("endNum", endNum).build()).bodyValue(dto), httpSession, serviceType).block();
    }

    public static ApiResponse postParam2(HttpSession httpSession, Object dto, String serviceType, String url, int strNum, int endNum) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().post()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).queryParam("strNum", strNum).queryParam("endNum", endNum).queryParam("pageSize", 9).build()).bodyValue(dto), httpSession, serviceType).block();
    }

    public static ApiResponse delete(HttpSession httpSession, String serviceType, String url) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().delete()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).build()), httpSession, serviceType).block();
    }

    public static ApiResponse put(HttpSession httpSession, Object dto, String serviceType, String url) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().put()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).build()).bodyValue(dto), httpSession, serviceType).block();
    }

    public static ApiResponse getParam(HttpSession httpSession, String serviceType, String url, String name, String value) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().get()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).queryParam(name, value).build()), httpSession, serviceType).block();
    }

    public static ApiResponse getParamList(HttpSession httpSession, String serviceType, String url, int strNum, int endNum, String sort) {
        return addCommonHeaders(createWebClientBuilder(serviceType).build().get()
                .uri(uriBuilder -> uriBuilder.path(VERSION + url).queryParam("strNum", strNum).queryParam("endNum", endNum).queryParam("sort", sort).build()), httpSession, serviceType).block();
    }

    private static int port(String memberType) {
        switch (memberType) {
            case Const.RequestHeader.USER:
                return 60001;
            case Const.RequestHeader.COMPANY:
                return 60002;

            case Const.RequestHeader.ADMIN:
                return 60003;
            default:
                return 60000;
        }
    }

    private static String capitalizeFirstLetter(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private static Function<Throwable, Mono<ApiResponse>> handleError() {
        return e -> {
            ObjectMapper om = new ObjectMapper();
            WebClientResponseException exception = (WebClientResponseException) e;
            String errorResponseBody = exception.getResponseBodyAsString(StandardCharsets.UTF_8);
            ApiResponse apiResponse;
            try {
                apiResponse = om.readValue(errorResponseBody, ApiResponse.class);
            } catch (JsonProcessingException ex) {
                // 에러 응답을 만들 수 없는 경우 빈 ApiResponse를 반환
                apiResponse = new ApiResponse();
            }
            return Mono.just(apiResponse);
        };
    }
}