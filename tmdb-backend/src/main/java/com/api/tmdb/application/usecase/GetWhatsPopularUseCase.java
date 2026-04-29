package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.WhatsPopularPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GetWhatsPopularUseCase implements WhatsPopularPort {

    private static final Logger log = LoggerFactory.getLogger(GetWhatsPopularUseCase.class);
    private static final int MAX_ITEMS = 40;

    private final TmdbWhatsPopularClientPort tmdbClientPort;

    public GetWhatsPopularUseCase(TmdbWhatsPopularClientPort tmdbClientPort) {
        this.tmdbClientPort = tmdbClientPort;
    }

    @Override
    public Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "flatrate",
                effectivePage,
                effectiveRegion,
                false);

        log.debug("Executing GetWhatsPopularUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        Mono<WhatsPopularResponse> moviesMono = tmdbClientPort.discoverMovies(params);
        Mono<WhatsPopularResponse> tvMono = tmdbClientPort.discoverTv(params);

        return Mono.zip(moviesMono, tvMono)
                .map(tuple -> {
                    List<WhatsPopularItem> allItems = new ArrayList<>();
                    allItems.addAll(tuple.getT1().results());
                    allItems.addAll(tuple.getT2().results());

                    List<WhatsPopularItem> sorted = allItems.stream()
                            .sorted(Comparator.comparingDouble(WhatsPopularItem::popularity).reversed())
                            .limit(MAX_ITEMS)
                            .toList();

                    log.debug("GetWhatsPopularUseCase completed: totalItems={}, returned={}",
                            allItems.size(), sorted.size());

                    return new WhatsPopularResponse(effectivePage, sorted, sorted.size());
                })
                .doOnError(error -> log.error("GetWhatsPopularUseCase failed: {}", error.getMessage(), error));
    }
}
