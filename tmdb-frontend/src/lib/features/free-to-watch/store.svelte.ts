import { mockFreeToWatch } from "$lib/data/movies";
import { fetchFreeToWatchMovies, fetchFreeToWatchTVShows, transformToMediaType, transformTVToMediaType } from "./api";
import type { Movie } from "$lib/types/movie";

export type FreeToWatchTab = 'movies' | 'tv';

export function createFreeToWatchStore() {
    let activeTab = $state<FreeToWatchTab>('movies');
    let movies = $state<Movie[]>(mockFreeToWatch.filter(m => m.media_type === 'movie').slice(0, 10));
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);

    const tabs = ['Movies', 'TV'] as const;
    const tabValues: FreeToWatchTab[] = ['movies', 'tv'];

    async function loadMovies(type: FreeToWatchTab) {
        isLoading = true;
        error = null;

        try {
            if (type === 'movies') {
                const items = await fetchFreeToWatchMovies();
                movies = items.map(transformToMediaType);
            } else {
                const items = await fetchFreeToWatchTVShows();
                movies = items.map(transformTVToMediaType);
            }
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load content';
            movies = mockFreeToWatch.filter(m => type === 'movies' ? m.media_type === 'movie' : m.media_type === 'tv').slice(0, 10);
        } finally {
            isLoading = false;
        }
    }

    function setActiveTab(tab: string) {
        const index = tabs.indexOf(tab as typeof tabs[number]);
        if (index !== -1) {
            activeTab = tabValues[index];
            loadMovies(activeTab);
        }
    }

    return {
        get activeTab() { return activeTab; },
        get movies() { return movies; },
        get isLoading() { return isLoading; },
        get error() { return error; },
        get tabs() { return tabs; },
        setActiveTab,
        loadMovies: () => loadMovies(activeTab),
    };
}