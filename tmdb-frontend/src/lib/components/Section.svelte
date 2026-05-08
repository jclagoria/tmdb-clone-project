<script lang="ts">
	import type { Movie } from '$lib/types/movie';
	import MovieCard from './MovieCard.svelte';
	import { ChevronLeft, ChevronRight, TrendingUp, PlayCircle, Star, MonitorPlay } from 'lucide-svelte';

	let { 
		title, 
		tabs = [], 
		activeTab = $bindable(''),
		movies = [],
		icon = 'trending'
	}: { 
		title: string; 
		tabs: string[]; 
		activeTab: string;
		movies: Movie[];
		icon?: 'trending' | 'play' | 'star' | 'monitor';
	} = $props();

	let scrollContainer: HTMLDivElement | null = $state(null);

	function scroll(direction: 'left' | 'right') {
		if (scrollContainer) {
			const scrollAmount = 400;
			scrollContainer.scrollBy({
				left: direction === 'left' ? -scrollAmount : scrollAmount,
				behavior: 'smooth'
			});
		}
	}

	function handleTabClick(tab: string) {
		activeTab = tab;
	}

	const iconMap = {
		trending: TrendingUp,
		play: PlayCircle,
		star: Star,
		monitor: MonitorPlay
	};
	const IconComponent = $derived(iconMap[icon] || TrendingUp);
</script>

<section class="section-backdrop py-8 px-4" style="content-visibility: auto;">
	<div class="max-w-7xl mx-auto">
		<div class="flex items-center justify-between mb-6">
			<h2 class="text-2xl font-bold text-white flex items-center gap-2">
				<IconComponent class="w-5 h-5 text-tmdb-light" />
				{title}
			</h2>
			<div class="flex gap-4 flex-wrap">
				{#each tabs as tab}
					<button 
						onclick={() => handleTabClick(tab.toLowerCase().replace(/\s+/g, ''))}
						class="pb-1 px-3 text-sm font-semibold transition cursor-pointer
							{activeTab === tab.toLowerCase().replace(/\s+/g, '') ? 'tab-active' : 'tab-inactive'}"
					>
						{tab}
					</button>
				{/each}
			</div>
		</div>
		
		<div class="relative">
			{#if movies.length > 0}
				<button 
					onclick={() => scroll('left')}
					class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex items-center justify-center"
				>
					<ChevronLeft class="w-6 h-6 text-white" />
				</button>
			{/if}
			
			<div 
				bind:this={scrollContainer}
				class="flex gap-4 overflow-x-auto hide-scrollbar pb-4"
			>
				{#each movies as movie (movie.id)}
					<MovieCard {movie} />
				{/each}
				{#if movies.length === 0}
					<p class="text-gray-400 py-8">No content available</p>
				{/if}
			</div>
			
			{#if movies.length > 0}
				<button 
					onclick={() => scroll('right')}
					class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex items-center justify-center"
				>
					<ChevronRight class="w-6 h-6 text-white" />
				</button>
			{/if}
		</div>
	</div>
</section>