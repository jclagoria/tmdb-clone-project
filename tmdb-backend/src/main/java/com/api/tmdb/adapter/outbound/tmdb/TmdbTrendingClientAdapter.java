package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TrendingMapper;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.outbound.TmdbClientPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@Primary
public class TmdbTrendingClientAdapter extends TmdbBaseAdapter implements TmdbClientPort {

    private final TrendingMapper trendingMapper;

    public TmdbTrendingClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            TrendingMapper trendingMapper) {
        super(webClient);
        this.trendingMapper = trendingMapper;
    }

    @Override
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        String tmdbTimeWindow = timeWindow.getValue();
        
        log.debug("Calling TMDB API: /trending/all/{}?language={}", tmdbTimeWindow, language);
        
        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/trending/all/{timeWindow}")
                .queryParam("language", language)
                .build(tmdbTimeWindow);

        return executeGet(uriConfig, trendingMapper::mapToTrendingResponse);
    }
}