<script lang="ts">
	import Hero from '$lib/components/Hero.svelte';
    import TrendingSection from "$lib/features/components/TrendingSection.svelte";
    import WhatsPopularSection from "$lib/features/whats-popular/components/WhatsPopularSection.svelte";
    import FreeToWatchSection from "$lib/features/free-to-watch/components/FreeToWatchSection.svelte";
	import Section from '$lib/components/Section.svelte';
	import { mockMovies } from '$lib/data/movies';

	let trailersActiveTab = $state('popular');

	const trailersTabs = ['Popular', 'Streaming', 'On TV', 'For Rent', 'In Theaters'];

	const trailersMovies = $derived(
		mockMovies.filter(m => m.media_type === 'movie').slice(0, 10)
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

<FreeToWatchSection />