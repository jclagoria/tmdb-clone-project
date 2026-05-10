package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

/**
 * @deprecated Use {@link DiscoverPort} instead.
 */
@Deprecated
public interface WhatsPopularPort {
    Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page);

    Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page);
}