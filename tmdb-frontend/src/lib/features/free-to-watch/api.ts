import { PUBLIC_CLONE_API_BASE } from "$env/static/public";
import type { FreeToWatchMovie, FreeToWatchResponse } from "./types";
import type { Movie } from "$lib/types/movie";

export async function fetchFreeToWatchMovies(
    options: {
        language?: string;
        region?: string;
        page?: number;
    } = {}
): Promise<FreeToWatchMovie[]> {
    const { language = 'en-US', region = 'US', page = 1 } = options;

    const params = new URLSearchParams({
        language,
        region,
        page: page.toString(),
    });

    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/free-to-watch/movie?${params.toString()}`
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: FreeToWatchResponse = await response.json();
    return data.results;
}

export function transformToMediaType(item: FreeToWatchMovie): Movie {
    return {
        id: item.id,
        title: item.title,
        original_title: item.originalTitle,
        name: item.title,
        original_name: item.originalTitle,
        overview: item.overview,
        poster_path: item.posterPath,
        backdrop_path: item.backdropPath,
        release_date: item.releaseDate ?? '',
        first_air_date: item.firstAirDate ?? '',
        vote_average: item.voteAverage,
        vote_count: item.voteCount,
        genre_ids: item.genreIds,
        original_language: item.originalLanguage,
        popularity: item.popularity,
        media_type: item.mediaType,
        adult: false
    };
}