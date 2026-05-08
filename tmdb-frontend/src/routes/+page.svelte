<script lang="ts">
	import { onMount } from 'svelte';
	import Hero from '$lib/components/Hero.svelte';
    import TrendingSection from "$lib/features/trending/components/TrendingSection.svelte";
    import LatestTrailersSection from "$lib/components/LatestTrailersSection.svelte";
	import { mockMovies } from '$lib/data/movies';

	let WhatsPopularSection = $state<any>(null);
	let FreeToWatchSection = $state<any>(null);
	let loaded = $state(false);

	let trailersActiveTab = $state('popular');

	const trailersTabs = ['Popular', 'Streaming', 'On TV', 'For Rent', 'In Theaters'];

	const trailersMovies = $derived(
		mockMovies.filter(m => m.media_type === 'movie').slice(0, 10)
	);

	function handleSearch(query: string) {
		console.log('Search query:', query);
	}

	onMount(async () => {
		const [{ default: WhatsPopular }, { default: FreeToWatch }] = await Promise.all([
			import("$lib/features/whats-popular/components/WhatsPopularSection.svelte"),
			import("$lib/features/free-to-watch/components/FreeToWatchSection.svelte")
		]);
		WhatsPopularSection = WhatsPopular;
		FreeToWatchSection = FreeToWatch;
		loaded = true;
	});
</script>

<Hero onSearch={handleSearch} />

<TrendingSection />

<LatestTrailersSection />

{#if loaded && WhatsPopularSection}
	<WhatsPopularSection />
{/if}

{#if loaded && FreeToWatchSection}
	<FreeToWatchSection />
{/if}