package com.api.tmdb.adapter.inbound.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String START_TIME = "startTime";
    private static final String REQUEST_ID = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);
        exchange.getAttributes().put(REQUEST_ID, requestId);

        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME, startTime);

        log.debug("""
                INCOMING_REQUEST: {
                  "requestId": "{}",
                  "method": "{}",
                  "uri": "{}",
                  "queryParams": "{}",
                  "headers": {},
                  "remoteAddress": "{}",
                  "contentType": "{}"
                }
                """,
                requestId,
                request.getMethod(),
                request.getURI(),
                request.getURI().getQuery(),
                sanitizeHeaders(request.getHeaders()),
                request.getRemoteAddress(),
                request.getHeaders().getContentType()
        );

        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis() - startTime;
            String requestIdAttr = exchange.getAttribute(REQUEST_ID);

            log.debug("""
                    OUTGOING_RESPONSE: {
                      "requestId": "{}",
                      "statusCode": {},
                      "durationMs": {},
                      "signal": "{}"
                    }
                    """,
                    requestIdAttr != null ? requestIdAttr : requestId,
                    response.getStatusCode(),
                    duration,
                    signalType
            );

            MDC.remove(REQUEST_ID);
        });
    }

    private String sanitizeHeaders(org.springframework.http.HttpHeaders headers) {
        return headers.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    if (key.equalsIgnoreCase("Authorization") || key.equalsIgnoreCase("Cookie")) {
                        return "\"" + key + "\": \"[REDACTED]\"";
                    }
                    return "\"" + key + "\": \"" + entry.getValue() + "\"";
                })
                .toList()
                .toString();
    }
}