<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import Header from '$lib/components/Header.svelte';
	import Footer from '$lib/components/Footer.svelte';
	import { goto } from '$app/navigation';

	let { children } = $props();

	function handleSearch(query: string) {
		console.log('Search:', query);
	}
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>The Movie Database (TMDB)</title>
	<meta name="description" content="Discover popular movies, TV shows, and more with TMDB Clone - your source for entertainment information." />
	<meta property="og:title" content="The Movie Database (TMDB) Clone" />
	<meta property="og:description" content="Discover popular movies, TV shows, and trending content." />
	<meta property="og:image" content="/og-image.jpg" />
	<meta property="og:type" content="website" />
	<meta name="twitter:card" content="summary_large_image" />
	<meta name="twitter:title" content="The Movie Database (TMDB) Clone" />
	<meta name="twitter:description" content="Discover popular movies, TV shows, and trending content." />
</svelte:head>

<Header onSearch={handleSearch} />

<main class="pt-16">
	{@render children()}
</main>

<Footer />

<style>
	main {
		min-height: 100vh;
	}
</style>