export type WhatsPopularType = 'streaming' | 'ontv' | 'forrent' | 'in-theaters';

export interface WhatsPopularItem {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string;
    backdropPath: string;
    mediaType: 'movie' | 'tv';
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    releaseDate: string | null;
    firstAirDate: string | null;
    voteAverage: number;
    voteCount: number;
    originCountry: string[] | null;
    adult: false;
}

export interface WhatsPopularResponse {
    page: number;
    results: WhatsPopularItem[];
    totalResults: number;
    totalPages?: number;
    dates?: {
        maximum: string;
        minimum: string;
    };
}