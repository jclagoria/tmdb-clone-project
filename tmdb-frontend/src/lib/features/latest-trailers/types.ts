export interface VideoResult {
    videoKey: string | null;
    videoSite: string | null;
    videoType: string | null;
    videoOfficial: boolean | null;
    videoUrl: string | null;
}

export interface LatestTrailerItem {
    id: number;
    title: string;
    originalTitle: string;
    overview: string;
    posterPath: string | null;
    backdropPath: string | null;
    popularity: number;
    voteAverage: number;
    voteCount: number;
    releaseDate: string | null;
    originalLanguage: string;
    genreIds: number[];
    mediaType: string;
    originCountry: string | null;
    videos: {
        results: VideoResult[];
    };
}

export interface LatestTrailerResponse {
    page: number;
    results: LatestTrailerItem[];
    totalResults: number;
}