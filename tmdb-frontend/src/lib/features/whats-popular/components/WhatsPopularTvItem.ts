export interface WhatsPopularTvItem {
    id: number;
    name: string;
    originalName: string;
    overview: string;
    posterPath: string;
    backdropPath: string;
    mediaType: 'tv';
    originalLanguage: string;
    genreIds: number[];
    popularity: number;
    firstAirDate: string | null;
    voteAverage: number;
    voteCount: number;
    originCountry: string[] | null;
    adult: false;
}

export interface WhatsPopularTvResponse {
    page: number;
    results: WhatsPopularTvItem[];
    totalPages: number;
    totalResults: number;
}