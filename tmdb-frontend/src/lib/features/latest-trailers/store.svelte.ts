import { fetchLatestTrailersPopular, fetchLatestTrailersStreaming, fetchLatestTrailersForRent, transformToMovie } from "$lib/features/latest-trailers/api";
import type { LatestTrailerItem } from "$lib/features/latest-trailers/types";
import type { Movie } from "$lib/types/movie";

export function createLatestTrailersStore() {
    let activeTab = $state<string>('popular');
    let movies = $state<Movie[]>([]);
    let isLoading = $state<boolean>(false);
    let error = $state<string | null>(null);
    let isModalOpen = $state<boolean>(false);
    let selectedMovie = $state<Movie | null>(null);

    const tabs = ['Popular', 'Streaming', 'For Rent', 'In Theaters'];

    async function loadMovies() {
        isLoading = true;
        error = null;

        try {
            let items: LatestTrailerItem[];
            
            switch (activeTab) {
                case 'streaming':
                    items = await fetchLatestTrailersStreaming();
                    break;
                case 'forrent':
                    items = await fetchLatestTrailersForRent();
                    break;
                case 'popular':
                default:
                    items = await fetchLatestTrailersPopular();
                    break;
            }
            
            movies = items.map(transformToMovie);
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to load trailers';
            movies = [];
        } finally {
            isLoading = false;
        }
    }

    function setActiveTab(tab: string) {
        activeTab = tab.toLowerCase().replace(/\s+/g, '');
        loadMovies();
    }

    function openModal(movie: Movie) {
        selectedMovie = movie;
        isModalOpen = true;
    }

    function closeModal() {
        isModalOpen = false;
        selectedMovie = null;
    }

    return {
        get activeTab() { return activeTab; },
        get movies() { return movies; },
        get isLoading() { return isLoading; },
        get error() { return error; },
        get isModalOpen() { return isModalOpen; },
        get selectedMovie() { return selectedMovie },
        get tabs() { return tabs; },
        setActiveTab,
        openModal,
        closeModal,
        loadMovies
    };
}