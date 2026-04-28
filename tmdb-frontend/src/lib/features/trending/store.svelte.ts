import { mockTrending } from "$lib/data/movies";
import { fetchTrending, transformToMediaType } from "$lib/features/trending/api";
import type { Movie } from "$lib/types/movie";

export function createTrendingStore() {
    let activeTab = $state<'day' | 'week'>('day');
    let movies = $state<Movie[]>(mockTrending.slice(0,10));
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);

    const tabs = ['Today', 'This Week'] as const;

    async function loadTrending(timeWindow: 'day' | 'week') {
        isLoading = true;
        error = null;

        try {
            const items = await fetchTrending(timeWindow);
            movies = items.map(transformToMediaType);
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load trending';
            movies = mockTrending.slice(0, 10);
        } finally {
            isLoading = false;
        }
    }

    function setActiveTab(tab: 'day' | 'week') {
        activeTab = tab;
        loadTrending(activeTab);
    }

    return {
        get activeTab() { return activeTab; },
        get movies() { return movies; },
        get isLoading() { return isLoading; },
        get error() { return error; },
        get tabs() { return tabs; },
        setActiveTab,
        loadTrending: () => loadTrending(activeTab),
    };
}