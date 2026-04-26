<script lang="ts">
	let { onSearch }: { onSearch?: (query: string) => void } = $props();

	let searchQuery = $state('');

	function handleSearch() {
		if (onSearch && searchQuery.trim()) {
			onSearch(searchQuery);
		}
	}

	function handleKeydown(e: KeyboardEvent) {
		if (e.key === 'Enter') {
			handleSearch();
		}
	}
</script>

<section class="hero-backdrop pt-32 pb-16 px-4">
	<div class="max-w-7xl mx-auto">
		<h1 class="text-4xl md:text-5xl font-bold mb-2 text-white">Welcome.</h1>
		<p class="text-xl md:text-2xl text-gray-300 mb-8">Millions of movies, TV shows and people to discover. Explore now.</p>
		
		<div class="flex gap-2 max-w-2xl">
			<input 
				type="text" 
				bind:value={searchQuery}
				onkeydown={handleKeydown}
				placeholder="Search for a movie, tv show, person......" 
				class="flex-1 px-4 py-3 rounded bg-white text-gray-900 focus:outline-none focus:ring-2 focus:ring-tmdb-light"
			/>
			<button 
				onclick={handleSearch}
				class="px-8 py-3 bg-tmdb-light text-tmdb-dark font-bold rounded hover:bg-tmdb-lightest transition"
			>
				Search
			</button>
		</div>
	</div>
</section>