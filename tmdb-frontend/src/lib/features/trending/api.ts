import { PUBLIC_CLONE_API_BASE } from '$env/static/public';
import type { TrendingItem, TrendingResponse } from "$lib/features/trending/types";
import type { Movie } from "$lib/types/movie";

export async function fetchTrending(
    timeWindow: 'day' | 'week'
): Promise<TrendingItem[]> {
    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/trending/${timeWindow}?language=en-US`,
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: TrendingResponse = await response.json();

    return data.results;
}

export function transformToMediaType(item: TrendingItem): Movie {
    return {
        id: item.id,
        title: item.title,
        original_title: item.originalTitle,
        overview: item.overview,
        poster_path: item.posterPath,
        backdrop_path: item.backdropPath,
        release_date: item.releaseDate!,
        first_air_date: item.releaseDate!,
        vote_average: item.voteAverage,
        vote_count: item.voteCount,
        genre_ids: item.genreIds,
        adult: item.adult,
        original_language: item.originalLanguage,
        popularity: item.popularity,
        media_type: item.mediaType as 'movie' | 'tv',
    };
}