<script lang="ts">
	import Hero from '$lib/components/Hero.svelte';
	import Section from '$lib/components/Section.svelte';
	import { mockTrending, mockMovies, mockPopular, mockFreeToWatch } from '$lib/data/movies';

    import { getTrendingMedia } from "$lib/api";
    import type {Movie} from "$lib/types/movie";

    let trendingActiveTab = $state('today');
	let trailersActiveTab = $state('popular');
	let popularActiveTab = $state('streaming');
	let freeActiveTab = $state('movies');

    let trendingMedia = $state<Movie[]>(mockTrending.slice(0, 10));
    let isLoadingTrending = $state<boolean>(false);

	const trendingTabs = ['Today', 'This Week'];
	const trailersTabs = ['Popular', 'Streaming', 'On TV', 'For Rent', 'In Theaters'];
	const popularTabs = ['Streaming', 'On TV', 'For Rent', 'In Theaters'];
	const freeTabs = ['Movies', 'TV'];

	const trailersMovies = $derived(
		mockMovies.filter(m => m.media_type === 'movie').slice(0, 10)
	);

	const popularMovies = $derived(
		popularActiveTab === 'streaming'
			? mockPopular.filter((_, i) => i % 2 === 0)
			: mockPopular.filter((_, i) => i % 2 !== 0)
	);

	const freeMovies = $derived(
		freeActiveTab === 'movies'
			? mockFreeToWatch.filter(m => m.media_type === 'movie')
			: mockFreeToWatch.filter(m => m.media_type === 'tv')
	);

    async function fetchTrending(window: 'day' | 'week') {
        isLoadingTrending = true;
        try {
            trendingMedia = await getTrendingMedia(window);
        } catch (error) {
            console.error('Failed to fetch trending:', error);
            trendingMedia = mockMovies.slice(0, 10);
        } finally {
            isLoadingTrending = false;
        }
    }

    $effect(() => {
        const timeWindow = trendingActiveTab === 'today' ? 'day' : 'week';
        fetchTrending(timeWindow);
    });

	function handleSearch(query: string) {
		console.log('Search query:', query);
	}
</script>

<Hero onSearch={handleSearch} />

<Section
	title="Trending"
	tabs={trendingTabs}
	bind:activeTab={trendingActiveTab}
	movies={trendingMedia}
	icon="trending"
    isLoading={isLoadingTrending}
/>

<Section
	title="Latest Trailers"
	tabs={trailersTabs}
	bind:activeTab={trailersActiveTab}
	movies={trailersMovies}
	icon="play"
/>

<Section
	title="What's Popular"
	tabs={popularTabs}
	bind:activeTab={popularActiveTab}
	movies={popularMovies}
	icon="star"
/>

<Section
	title="Free To Watch"
	tabs={freeTabs}
	bind:activeTab={freeActiveTab}
	movies={freeMovies}
	icon="monitor"
/>