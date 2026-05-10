package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TrendingMapper;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.outbound.TmdbTrendingPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

/**
 * Adapter implementing TmdbTrendingPort for TMDB Trending API.
 * 
 * This adapter calls the TMDB /trending endpoint:
 * - /trending/all/{time_window}
 * 
 * Note: This adapter does NOT handle caching - that's managed by AOP
 * in the use case layer via @Cacheable annotation.
 */
@Component
public class TmdbTrendingAdapter extends TmdbBaseAdapter implements TmdbTrendingPort {

    private final TrendingMapper trendingMapper;

    public TmdbTrendingAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            TrendingMapper trendingMapper) {
        super(webClient);
        this.trendingMapper = trendingMapper;
    }

    @Override
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        log.debug("Calling TMDB API: /trending/all/{}?language={}", timeWindow.getValue(), language);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/trending/all/" + timeWindow.getValue())
                .queryParam("language", language)
                .build();

        return executeGet(uriConfig, trendingMapper::mapToTrendingResponse);
    }
}