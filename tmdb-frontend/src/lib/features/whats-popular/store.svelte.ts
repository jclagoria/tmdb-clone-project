import {mockPopular} from "$lib/data/movies";
import {fetchWhatsPopular, fetchWhatsPopularTV, transformToMediaType} from "$lib/features/whats-popular/api";
import type {Movie} from "$lib/types/movie";
import type {WhatsPopularType} from "$lib/features/whats-popular/components/WhatsPopularType";

export function createWhatsPopularStore() {
    let activeTab = $state<WhatsPopularType>('streaming');
    let movies = $state<Movie[]>(mockPopular.slice(0, 10));
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);

    const tabs = ['Streaming', 'On Tv', 'For Rent', 'In Theaters'] as const;
    const tabValues: WhatsPopularType[] = ['streaming', 'ontv', 'forrent', 'in-theaters'];

    async function loadWhatsPopular(type: WhatsPopularType) {
        isLoading = true;
        error = null;

        try {
            if (type === 'ontv') {
                // Use TV-specific endpoint
                movies = await fetchWhatsPopularTV();
            } else {
                // Use existing movie endpoints
                const items = await fetchWhatsPopular(type);
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
        const index = tabs.indexOf(tab as typeof tabs[number]);
        if (index !== -1) {
            activeTab = tabValues[index];
            loadWhatsPopular(activeTab);
        }
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