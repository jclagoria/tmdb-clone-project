export type WhatsPopularType = 'streaming' | 'ontv' | 'forrent' | 'intheataters';

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
    result: WhatsPopularItem[];
    totalResults: number;
}