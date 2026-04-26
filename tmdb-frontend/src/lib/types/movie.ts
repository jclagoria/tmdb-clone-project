export interface Movie {
	id: number;
	title?: string;
	original_title?: string;
	name?: string;
	original_name?: string;
	overview: string;
	poster_path: string | null;
	backdrop_path: string | null;
	release_date?: string;
	first_air_date?: string;
	vote_average: number;
	vote_count: number;
	genre_ids: number[];
	adult: boolean;
	original_language: string;
	popularity: number;
	media_type: 'movie' | 'tv';
}

export interface SearchResponse {
	page: number;
	results: Movie[];
	total_pages: number;
	total_results: number;
}

export interface Genre {
	id: number;
	name: string;
}

export const GENRES: Genre[] = [
	{ id: 28, name: 'Action' },
	{ id: 12, name: 'Adventure' },
	{ id: 16, name: 'Animation' },
	{ id: 35, name: 'Comedy' },
	{ id: 80, name: 'Crime' },
	{ id: 99, name: 'Documentary' },
	{ id: 18, name: 'Drama' },
	{ id: 10751, name: 'Family' },
	{ id: 14, name: 'Fantasy' },
	{ id: 36, name: 'History' },
	{ id: 27, name: 'Horror' },
	{ id: 10402, name: 'Music' },
	{ id: 9648, name: 'Mystery' },
	{ id: 10749, name: 'Romance' },
	{ id: 878, name: 'Science Fiction' },
	{ id: 10770, name: 'TV Movie' },
	{ id: 53, name: 'Thriller' },
	{ id: 10752, name: 'War' },
	{ id: 37, name: 'Western' }
];

export function getGenreName(genreId: number): string {
	const genre = GENRES.find(g => g.id === genreId);
	return genre?.name || 'Unknown';
}

export function getRatingColor(rating: number): string {
	if (rating >= 7.5) return 'bg-green-500';
	if (rating >= 6) return 'bg-yellow-500';
	return 'bg-red-500';
}

export function getYear(item: Movie): string {
	return item.release_date?.split('-')[0] || item.first_air_date?.split('-')[0] || '';
}

export function getTitle(item: Movie): string {
	return item.title || item.name || 'Unknown';
}