import { mockMovies, mockTVShows, mockTrending, mockPopular, mockFreeToWatch } from '$lib/data/movies';
import type { Movie, SearchResponse } from '$lib/types/movie';

const API_BASE = 'https://api.themoviedb.org/3';
const IMAGE_BASE = 'https://image.tmdb.org/t/p';
const CLONE_API_BASE = 'http://localhost:8080/api/v1';

let apiKey = '';
let mockMode = true;

export function setApiKey(key: string) {
	apiKey = key;
	mockMode = false;
}

export function getImageUrl(path: string | null, size: string = 'w500'): string | null {
	if (!path) return null;
	return `${IMAGE_BASE}/${size}${path}`;
}

// <-- Interfaces -->
interface TrendingApiResponse {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string | null;
    backdropPath: string | null;
    mediaType: string;
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    releaseDate: string | null;
    adult: boolean;
    video: boolean | null;
    voteAverage: number;
    voteCount: number;
}

// <-- CALL TO CLONE API-->

export async function getTrendingMedia(timeWindow: `day` | 'week'): Promise<Movie[]> {
    const response = await fetch(`${CLONE_API_BASE}/trending/${timeWindow}?language=en-US`);

    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }

    const data = await response.json();

    return data.results.map(transformToMediaType);
}

function transformToMediaType(apiResponse: TrendingApiResponse): Movie {
    return {
        id: apiResponse.id,
        title: apiResponse.title,
        original_title: apiResponse.originalTitle,
        name: apiResponse.title,
        original_name: apiResponse.originalTitle,
        overview: apiResponse.overview,
        poster_path: apiResponse.posterPath,
        backdrop_path: apiResponse.backdropPath,
        release_date: apiResponse.releaseDate!,
        first_air_date: apiResponse.releaseDate!,
        vote_average: apiResponse.voteAverage,
        vote_count: apiResponse.voteCount,
        genre_ids: apiResponse.genreIds,
        adult: apiResponse.adult,
        original_language: apiResponse.originalLanguage,
        popularity: apiResponse.popularity,
        media_type: apiResponse.mediaType as 'movie' | 'tv',
    };
}

// <-- END CALL TO CLONE API-->

export async function getTrending(timeWindow: 'day' | 'week' = 'day'): Promise<Movie[]> {
	if (mockMode) {
		return mockTrending;
	}
	const data = await fetchWithAuth<{ results: Movie[] }>(`/trending/movie/${timeWindow}`);
	return data.results;
}

export async function getTrendingTV(timeWindow: 'day' | 'week' = 'day'): Promise<Movie[]> {
	if (mockMode) {
		return mockTrending.filter(m => m.media_type === 'tv');
	}
	const data = await fetchWithAuth<{ results: Movie[] }>(`/trending/tv/${timeWindow}`);
	return data.results;
}

export async function getLatestTrailers(type: 'movie' | 'tv' = 'movie', sortBy: string = 'popularity.desc'): Promise<Movie[]> {
	if (mockMode) {
		return mockMovies.filter(m => m.media_type === type).slice(0, 10);
	}
	const endpoint = type === 'movie' ? '/movie/now_playing' : '/tv/on_the_air';
	const data = await fetchWithAuth<{ results: Movie[] }>(endpoint);
	return data.results;
}

export async function getWhatsPopular(type: 'movie' | 'tv' = 'movie', watchRegion: string = 'US'): Promise<Movie[]> {
	if (mockMode) {
		return mockPopular.filter(m => m.media_type === type);
	}
	const endpoint = type === 'movie' ? '/movie/popular' : '/tv/popular';
	const data = await fetchWithAuth<{ results: Movie[] }>(endpoint);
	return data.results;
}

export async function getFreeToWatch(type: 'movie' | 'tv' = 'movie'): Promise<Movie[]> {
	if (mockMode) {
		return mockFreeToWatch.filter(m => m.media_type === type);
	}
	const data = await fetchWithAuth<{ results: Movie[] }>(`/discover/${type}?watch_region=US&with_watch_providers=free`);
	return data.results;
}

export async function searchMovies(query: string): Promise<SearchResponse> {
	if (mockMode) {
		const results = mockMovies.filter(m => 
			(m.title?.toLowerCase().includes(query.toLowerCase()) || 
			 m.name?.toLowerCase().includes(query.toLowerCase()))
		);
		return {
			page: 1,
			results,
			total_pages: 1,
			total_results: results.length
		};
	}
	return fetchWithAuth(`/search/multi?query=${encodeURIComponent(query)}`) as Promise<SearchResponse>;
}

async function fetchWithAuth<T>(endpoint: string): Promise<T> {
	if (!apiKey) {
		throw new Error('API key not set');
	}
	const response = await fetch(`${API_BASE}${endpoint}?api_key=${apiKey}`);
	if (!response.ok) {
		throw new Error(`API error: ${response.status}`);
	}
	return response.json();
}