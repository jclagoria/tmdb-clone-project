<script lang="ts">
	import Hero from '$lib/components/Hero.svelte';
    import TrendingSection from "$lib/features/components/TrendingSection.svelte";
    import WhatsPopularSection from "$lib/features/whats-popular/components/WhatsPopularSection.svelte";
	import Section from '$lib/components/Section.svelte';
	import { mockMovies, mockFreeToWatch } from '$lib/data/movies';

	let trailersActiveTab = $state('popular');
	let freeActiveTab = $state('movies');

	const trailersTabs = ['Popular', 'Streaming', 'On TV', 'For Rent', 'In Theaters'];
	const freeTabs = ['Movies', 'TV'];

	const trailersMovies = $derived(
		mockMovies.filter(m => m.media_type === 'movie').slice(0, 10)
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

<TrendingSection />

<Section
	title="Latest Trailers"
	tabs={trailersTabs}
	bind:activeTab={trailersActiveTab}
	movies={trailersMovies}
	icon="play"
/>

<WhatsPopularSection />

<Section
	title="Free To Watch"
	tabs={freeTabs}
	bind:activeTab={freeActiveTab}
	movies={freeMovies}
	icon="monitor"
/>