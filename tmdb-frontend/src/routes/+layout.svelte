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
</svelte:head>

<Header onSearch={handleSearch} />

<main>
	{@render children()}
</main>

<Footer />

<style>
	main {
		min-height: 100vh;
	}
</style>