import {mockPopular} from "$lib/data/movies";
import {fetchWhatsPopular, fetchWhatsPopularTV, transformToMediaType} from "$lib/features/whats-popular/api";
import { fetchWithCache } from "$lib/api";
import type {Movie} from "$lib/types/movie";

export function createWhatsPopularStore() {
    let activeTab = $state<string>('Streaming');
    let movies = $state<Movie[]>(mockPopular.slice(0, 10));
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);

    const tabs = ['Streaming', 'On Tv', 'For Rent', 'In Theaters'] as const;

    async function loadWhatsPopular(type: string) {
        const apiType = type === 'Streaming' ? 'streaming' 
            : type === 'On Tv' ? 'ontv' 
            : type === 'For Rent' ? 'forrent' 
            : type === 'In Theaters' ? 'in-theaters' 
            : 'streaming';
        const cacheKey = `whats-popular-${apiType}`;
        isLoading = true;
        error = null;

        try {
            if (apiType === 'ontv') {
                movies = await fetchWithCache(cacheKey, () => fetchWhatsPopularTV());
            } else {
                const items = await fetchWithCache(cacheKey, () => fetchWhatsPopular(apiType as any));
                movies = items.map(transformToMediaType);
            }
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load content';
            movies = mockPopular.slice(0, 10);
        } finally {
            isLoading = false;
        }
    }

    function setActiveTab(tab: string) {
        activeTab = tab;
        loadWhatsPopular(activeTab);
    }

    return {
        get activeTab() { return activeTab; },
        get movies() { return movies; },
        get isLoading() { return isLoading; },
        get error() { return error; },
        get tabs() { return tabs; },
        setActiveTab,
        loadWhatsPopular: () => loadWhatsPopular(activeTab),
    };
}