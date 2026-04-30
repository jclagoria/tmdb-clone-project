import { PUBLIC_CLONE_API_BASE } from "$env/static/public";
import type {
    WhatsPopularItem,
    WhatsPopularResponse,
    WhatsPopularType
} from "$lib/features/whats-popular/components/WhatsPopularType";
import type { Movie } from "$lib/types/movie";
import type {
    WhatsPopularTvItem,
    WhatsPopularTvResponse
} from "$lib/features/whats-popular/components/WhatsPopularTvItem";

function getEndpointPath(type: WhatsPopularType): string {
    const pathMap: Record<WhatsPopularType, string> = {
        'streaming': 'streaming',
        'ontv': 'tv/on-the-air',
        'forrent': 'for-rent',
        'intheataters': 'intheataters'
    };
    return pathMap[type];
}

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

    const endpointPath = getEndpointPath(type);
    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/whats-popular/${endpointPath}?${params.toString()}`
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

export async function fetchWhatsPopularTV(
    options: {
        language?: string;
        page?: number;
        timezone?: string;
    } = {}
): Promise<Movie[]> {
    const { language = 'en-US', page = 1 } = options;

    const params = new URLSearchParams({
        language,
        page: page.toString(),
    });

    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/whats-popular/tv/on-the-air?${params.toString()}`
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: WhatsPopularTvResponse = await response.json();
    return data.results.map(transformTVToMediaType);
}

function transformTVToMediaType(item: WhatsPopularTvItem): Movie {
    return {
        id: item.id,
        title: item.name,
        original_title: item.originalName,
        name: item.name,
        original_name: item.originalName,
        overview: item.overview,
        poster_path: item.posterPath,
        backdrop_path: item.backdropPath,
        release_date: item.firstAirDate ?? '',
        first_air_date: item.firstAirDate ?? '',
        vote_average: item.voteAverage,
        vote_count: item.voteCount,
        genre_ids: item.genreIds,
        original_language: item.originalLanguage,
        popularity: item.popularity,
        media_type: 'tv' as const,
        adult: item.adult
    };
}