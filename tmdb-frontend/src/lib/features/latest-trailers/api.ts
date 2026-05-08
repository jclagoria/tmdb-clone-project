import { PUBLIC_CLONE_API_BASE } from "$env/static/public";
import type { LatestTrailerItem, LatestTrailerResponse } from "./types";
import type { Movie } from "$lib/types/movie";

export async function fetchLatestTrailersPopular(
    language: string = 'en-US'
): Promise<LatestTrailerItem[]> {
    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/latest-trailers/popular?language=${language}`
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: LatestTrailerResponse = await response.json();
    return data.results;
}

export async function fetchLatestTrailersStreaming(
    language: string = 'en-US',
    watchRegion: string = 'US'
): Promise<LatestTrailerItem[]> {
    const response = await fetch(
        `${PUBLIC_CLONE_API_BASE}/latest-trailers/streaming?language=${language}&watch_region=${watchRegion}`
    );

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data: LatestTrailerResponse = await response.json();
    return data.results;
}

export function transformToMovie(item: LatestTrailerItem): Movie {
    const videos = item.videos?.results || [];
    const trailer = videos.find(v => v.videoSite === 'YouTube' && v.videoType === 'Trailer');
    
    const trailerUrl = trailer?.videoUrl 
        || (trailer?.videoKey ? `https://www.youtube.com/watch?v=${trailer.videoKey}` : undefined);
    
    return {
        id: item.id,
        title: item.title,
        original_title: item.originalTitle,
        overview: item.overview,
        poster_path: item.posterPath,
        backdrop_path: item.backdropPath,
        release_date: item.releaseDate ?? undefined,
        first_air_date: item.releaseDate ?? undefined,
        vote_average: item.voteAverage,
        vote_count: item.voteCount,
        genre_ids: item.genreIds,
        adult: false,                    // Not in API response
        original_language: item.originalLanguage,
        popularity: item.popularity,
        // Transform uppercase to lowercase
        media_type: item.mediaType === 'MOVIE' ? 'movie' : 'tv',
        trailer_url: trailerUrl,
    };
}