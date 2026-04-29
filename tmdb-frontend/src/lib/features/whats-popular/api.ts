import { PUBLIC_CLONE_API_BASE } from "$env/static/public";
import type {
    WhatsPopularItem,
    WhatsPopularResponse,
    WhatsPopularType
} from "$lib/features/whats-popular/components/WhatsPopularType";
import type { Movie } from "$lib/types/movie";

export async function fetchWhatsPopular(
    type: WhatsPopularType,
    options: {
        language?: string;
        region?: string;
        page?: number;
    } = {}
): Promise<WhatsPopularItem[]> {
    const { language = 'en-US', region = 'US', page = 1 } = options;

    const params = new URLSearchParams({
        language,
        region,
        page: page.toString(),
    });

    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/whats-popular/${type}?${params.toString()}`
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: WhatsPopularResponse = await response.json();
    return data.result;
}

export function transformToMediaType(item: WhatsPopularItem): Movie {
    return {
        id: item.id,
        title: item.title,
        original_title: item.originalTitle,
        name: item.title,
        original_name: item.originalTitle,
        overview: item.overview,
        poster_path: item.posterPath,
        backdrop_path: item.backdropPath,
        release_date: item.releaseDate ?? item.firstAirDate ?? '',
        first_air_date: item.firstAirDate ?? item.releaseDate ?? '',
        vote_average: item.voteAverage,
        vote_count: item.voteCount,
        genre_ids: item.genreIds,
        original_language: item.originalLanguage,
        popularity: item.popularity,
        media_type: item.mediaType as 'movie' | 'tv',
        adult: item.adult
    };
}