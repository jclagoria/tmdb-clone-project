package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.inbound.TvOnTheAirPort;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetTvOnTheAirUseCase implements TvOnTheAirPort {

    private static final Logger log = LoggerFactory.getLogger(GetTvOnTheAirUseCase.class);
    private final TmdbTvOnTheAirPort tmdbTvOnTheAirPort;

    public GetTvOnTheAirUseCase(TmdbTvOnTheAirPort tmdbTvOnTheAirPort) {
        this.tmdbTvOnTheAirPort = tmdbTvOnTheAirPort;
    }

    @Override
    public Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone) {
        log.info("Executing GetTvOnTheAirUseCase: language={}, page={}, timezone={}", language, page, timezone);
        return tmdbTvOnTheAirPort.getTvOnTheAir(language, page, timezone);
    }
}
