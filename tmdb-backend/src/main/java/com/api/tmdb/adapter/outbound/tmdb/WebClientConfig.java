package com.api.tmdb.adapter.outbound.tmdb;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${tmdb.base-url}")
    private String tmdbBaseUrl;

    @Value("${tmdb.access-token}")
    private String accessToken;

    @Value("${tmdb.webclient.connect-timeout:5s}")
    private String connectTimeout;

    @Value("${tmdb.webclient.response-timeout:10s}")
    private String responseTimeout;

    @Value("${tmdb.webclient.pool.max-connections:50}")
    private int maxConnections;

    @Value("${tmdb.webclient.pool.pending-acquire-timeout:10s}")
    private String pendingAcquireTimeout;

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("tmdb-pool")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(parseDuration(pendingAcquireTimeout))
                .build();
    }

    @Bean
    public WebClient tmdbWebClient(ConnectionProvider connectionProvider) {
        int responseTimeoutMillis = parseDurationToMillis(responseTimeout);

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, parseDurationToMillis(connectTimeout))
                .responseTimeout(parseDuration(responseTimeout))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(responseTimeoutMillis, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(responseTimeoutMillis, TimeUnit.MILLISECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> config
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(tmdbBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeader("content-type", "application/json")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    private Duration parseDuration(String duration) {
        String d = duration.toLowerCase().trim();
        if (d.endsWith("ms")) {
            return Duration.ofMillis(Long.parseLong(d.replace("ms", "")));
        } else if (d.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(d.replace("s", "")));
        }
        return Duration.ofSeconds(Long.parseLong(d));
    }

    private int parseDurationToMillis(String duration) {
        String d = duration.toLowerCase().trim();
        if (d.endsWith("ms")) {
            return Integer.parseInt(d.replace("ms", ""));
        } else if (d.endsWith("s")) {
            return Integer.parseInt(d.replace("s", "")) * 1000;
        }
        return Integer.parseInt(d);
    }
}