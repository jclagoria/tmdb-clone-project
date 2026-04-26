<script lang="ts">
	import { Search } from 'lucide-svelte';

	let { onSearch }: { onSearch?: (query: string) => void } = $props();

	let searchQuery = $state('');
	let showSearch = $state(false);

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

<header class="fixed top-0 left-0 right-0 bg-tmdb-dark/95 backdrop-blur-sm z-40 border-b border-white/10">
	<div class="max-w-7xl mx-auto px-4 py-3">
		<div class="flex items-center justify-between">
			<div class="flex items-center gap-8">
				<a href="/" class="flex items-center gap-2">
					<svg class="w-8 h-8" viewBox="0 0 32 32" fill="none">
						<path d="M16 2L2 9.5v13L16 30l14-7.5v-13L16 2z" fill="#01b6e9"/>
						<path d="M16 8.5L8 12.5v7l8 4 8-4v-7l-8-4z" fill="#0d253f"/>
						<circle cx="16" cy="16" r="3" fill="#01b6e9"/>
					</svg>
					<span class="text-xl font-bold hidden sm:block text-white">The Movie Database</span>
				</a>
				<nav class="hidden md:flex items-center gap-6">
					<a href="#movies" class="font-semibold text-gray-300 hover:text-tmdb-light transition">Movies</a>
					<a href="#tvshows" class="font-semibold text-gray-300 hover:text-tmdb-light transition">TV Shows</a>
					<a href="#people" class="font-semibold text-gray-300 hover:text-tmdb-light transition">People</a>
					<a href="#more" class="font-semibold text-gray-300 hover:text-tmdb-light transition">More</a>
				</nav>
			</div>
			<div class="flex items-center gap-4">
				{#if showSearch}
					<input
						type="text"
						bind:value={searchQuery}
						onkeydown={handleKeydown}
						placeholder="Search..."
						class="px-3 py-1.5 text-sm bg-white/10 border border-white/20 rounded text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-tmdb-light"
					/>
				{/if}
				<button onclick={() => showSearch = !showSearch} class="text-gray-400 hover:text-white transition">
					<Search class="w-5 h-5" />
				</button>
				<button class="text-sm text-gray-300 hover:text-white transition hidden sm:block">Login</button>
				<button class="px-4 py-1.5 text-sm bg-tmdb-light text-tmdb-dark font-semibold rounded hover:bg-tmdb-lightest transition">
					Join TMDB
				</button>
			</div>
		</div>
	</div>
</header>