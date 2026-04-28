export interface TrendingItem {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string | null;
    backdropPath: string | null;
    mediaType: 'movie' | 'tv';
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    releaseDate: string | null;
    adult: boolean;
    video: boolean | null;
    voteAverage: number;
    voteCount: number;
}

export interface TrendingResponse {
    page: number;
    results: TrendingItem[];
    totalPages: number;
    totalResults: number;
}