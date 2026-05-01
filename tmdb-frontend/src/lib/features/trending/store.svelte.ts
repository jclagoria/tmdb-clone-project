import { mockTrending } from "$lib/data/movies";
import { fetchTrending, transformToMediaType } from "$lib/features/trending/api";
import type { Movie } from "$lib/types/movie";

export function createTrendingStore() {
    let activeTab = $state<string>('Today');
    let movies = $state<Movie[]>(mockTrending.slice(0,10));
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);

    const tabs = ['Today', 'This Week'] as const;

    async function loadTrending(timeWindow: string) {
        const apiTime = timeWindow === 'This Week' ? 'week' : 'day';
        isLoading = true;
        error = null;

        try {
            const items = await fetchTrending(apiTime as 'day' | 'week');
            movies = items.map(transformToMediaType);
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load trending';
            movies = mockTrending.slice(0, 10);
        } finally {
            isLoading = false;
        }
    }

    function setActiveTab(tab: string) {
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