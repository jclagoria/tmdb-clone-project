import { mockMovies, mockTVShows, mockTrending, mockPopular, mockFreeToWatch, mockMovieDetails } from '$lib/data/movies';
import type { Movie, SearchResponse, MovieDetails } from '$lib/types/movie';

const API_BASE = 'https://api.themoviedb.org/3';
const IMAGE_BASE = 'https://image.tmdb.org/t/p';

let apiKey = '';
let mockMode = true;

const CACHE_TTL = 5 * 60 * 1000;
const MAX_CACHE_SIZE = 50;

interface CacheEntry<T> {
	promise: Promise<T>;
	timestamp: number;
}

const requestCache = new Map<string, CacheEntry<any>>();

function cleanExpiredCache() {
	const now = Date.now();
	for (const [k, v] of requestCache) {
		if (now - v.timestamp > CACHE_TTL) {
			requestCache.delete(k);
		}
	}
}

function enforceMaxSize() {
	if (requestCache.size >= MAX_CACHE_SIZE) {
		const firstKey = requestCache.keys().next().value;
		if (firstKey) {
			requestCache.delete(firstKey);
		}
	}
}

export function setApiKey(key: string) {
	apiKey = key;
	mockMode = false;
}

export async function fetchWithCache<T>(key: string, fetcher: () => Promise<T>): Promise<T> {
	cleanExpiredCache();
	enforceMaxSize();

	if (requestCache.has(key)) {
		const entry = requestCache.get(key);
		if (entry && Date.now() - entry.timestamp <= CACHE_TTL) {
			return entry.promise;
		}
		requestCache.delete(key);
	}

	const promise = fetcher();
	requestCache.set(key, { promise, timestamp: Date.now() });
	return promise;
}

export function createFetchController(): AbortController {
	return new AbortController();
}

export function getImageUrl(path: string | null, size: string = 'w500'): string | null {
	if (!path) return null;
	return `${IMAGE_BASE}/${size}${path}`;
}

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

export async function getMovieDetails(id: string | number): Promise<MovieDetails> {
	const numericId = typeof id === 'string' ? parseInt(id, 10) : id;
	if (mockMode || !apiKey) {
		const movie = mockMovieDetails[numericId];
		if (movie) {
			return movie;
		}
		throw new Error('Movie not found in mock data');
	}
	const data = await fetchWithAuth<MovieDetails>(`/movie/${id}?append_to_response=credits,keywords,videos,reviews`);
	return data;
}

async function fetchWithAuth<T>(endpoint: string, signal?: AbortSignal): Promise<T> {
	if (!apiKey) {
		throw new Error('API key not set');
	}
	const response = await fetch(`${API_BASE}${endpoint}?api_key=${apiKey}`, { signal });
	if (!response.ok) {
		throw new Error(`API error: ${response.status}`);
	}
	return response.json();
}