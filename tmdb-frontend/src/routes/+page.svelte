<script lang="ts">
	import Hero from '$lib/components/Hero.svelte';
	import Section from '$lib/components/Section.svelte';
	import { mockTrending, mockMovies, mockPopular, mockFreeToWatch } from '$lib/data/movies';
	import type { Movie } from '$lib/types/movie';

	let trendingActiveTab = $state('today');
	let trailersActiveTab = $state('popular');
	let popularActiveTab = $state('streaming');
	let freeActiveTab = $state('movies');

	const trendingTabs = ['Today', 'This Week'];
	const trailersTabs = ['Popular', 'Streaming', 'On TV', 'For Rent', 'In Theaters'];
	const popularTabs = ['Streaming', 'On TV', 'For Rent', 'In Theaters'];
	const freeTabs = ['Movies', 'TV'];

	const trendingMovies = $derived(
		trendingActiveTab === 'today' 
			? mockTrending.slice(0, 10)
			: mockTrending.slice(5, 15)
	);

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

	function handleSearch(query: string) {
		console.log('Search query:', query);
	}
</script>

<Hero onSearch={handleSearch} />

<Section
	title="Trending"
	tabs={trendingTabs}
	bind:activeTab={trendingActiveTab}
	movies={trendingMovies}
	icon="trending"
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