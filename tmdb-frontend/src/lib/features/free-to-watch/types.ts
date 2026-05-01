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

export interface FreeToWatchResponse {
    page: number;
    results: FreeToWatchMovie[];
    totalResults: number;
}