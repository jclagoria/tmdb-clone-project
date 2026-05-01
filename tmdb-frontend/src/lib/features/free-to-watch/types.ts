export interface FreeToWatchMovie {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string | null;
    backdropPath: string | null;
    mediaType: 'movie';
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    releaseDate: string | null;
    firstAirDate: null;
    voteAverage: number;
    voteCount: number;
    originCountry: null;
}

export interface FreeToWatchTV {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string | null;
    backdropPath: string | null;
    mediaType: 'tv';
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    releaseDate: null;
    firstAirDate: string;
    voteAverage: number;
    voteCount: number;
    originCountry: string[];
}

export interface FreeToWatchResponse {
    page: number;
    results: FreeToWatchMovie[];
    totalResults: number;
}

export interface FreeToWatchTVResponse {
    page: number;
    results: FreeToWatchTV[];
    totalResults: number;
}