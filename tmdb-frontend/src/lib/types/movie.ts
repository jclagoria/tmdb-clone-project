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
    trailer_url?: string;
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

export interface CastMember {
	id: number;
	name: string;
	original_name: string;
	character: string;
	profile_path: string | null;
	order: number;
}

export interface CrewMember {
	id: number;
	name: string;
	job: string;
	department: string;
	profile_path: string | null;
}

export interface Keyword {
	id: number;
	name: string;
}

export interface Video {
	id: string;
	key: string;
	name: string;
	site: string;
	type: string;
	published_at: string;
}

export interface Review {
	id: string;
	author: string;
	author_details: {
		name: string;
		username: string;
		rating: number;
		avatar_path: string | null;
	};
	content: string;
	created_at: string;
}

export interface MovieDetails extends Movie {
	tagline: string;
	runtime: number;
	status: string;
	budget: number;
	revenue: number;
	genres: Genre[];
	credits: {
		cast: CastMember[];
		crew: CrewMember[];
	};
	keywords: Keyword[];
	videos: {
		results: Video[];
	};
	reviews: {
		results: Review[];
	};
}

export function formatRuntime(minutes: number): string {
	const hours = Math.floor(minutes / 60);
	const mins = minutes % 60;
	return `${hours}h ${mins}m`;
}

export function formatCurrency(amount: number): string {
	return new Intl.NumberFormat('en-US', {
		style: 'currency',
		currency: 'USD',
		minimumFractionDigits: 0,
		maximumFractionDigits: 0
	}).format(amount);
}

export function getDirector(crew: CrewMember[]): CrewMember | undefined {
	return crew.find(c => c.job === 'Director');
}

export function getWriters(crew: CrewMember[]): CrewMember[] {
	return crew.filter(c => 
		c.department === 'Writing' && 
		['Screenplay', 'Writer', 'Novel', 'Story'].includes(c.job)
	);
}