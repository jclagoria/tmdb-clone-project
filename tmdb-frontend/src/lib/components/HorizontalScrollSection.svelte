<script lang="ts">
	import MovieCard from '$lib/components/MovieCard.svelte';
	import { ChevronLeft, ChevronRight } from 'lucide-svelte';
	import { onMount } from 'svelte';

	let {
		title,
		Icon,
		films = [],
		tabs = [],
		activeTab = $bindable(),
		isLoading = false,
		onTabChange
	}: {
		title: string;
		Icon: any;
		films: any[];
		tabs: readonly string[] | string[];
		activeTab?: string;
		isLoading?: boolean;
		onTabChange?: (tab: string) => void;
	} = $props();

	let scrollContainer: HTMLDivElement | null = $state(null);
	let visibleItems = $state<Set<number>>(new Set());
	let observer: IntersectionObserver | null = null;

	function isActive(tab: string): boolean {
		return activeTab?.toLowerCase() === tab.toLowerCase();
	}

	function scroll(direction: 'left' | 'right') {
		scrollContainer?.scrollBy({
			left: direction === 'left' ? -400 : 400,
			behavior: 'smooth'
		});
	}

	onMount(() => {
		observer = new IntersectionObserver(
			(entries) => {
				entries.forEach((entry) => {
					const id = Number(entry.target.getAttribute('data-movie-id'));
					if (entry.isIntersecting) {
						visibleItems.add(id);
						visibleItems = new Set(visibleItems);
					}
				});
			},
			{ root: scrollContainer, rootMargin: '100px' }
		);

		return () => observer?.disconnect();
	});

	function observeCard(node: HTMLDivElement, movieId: number) {
		observer?.observe(node);
		return {
			destroy() {
				observer?.unobserve(node);
			}
		};
	}
</script>

<section class="section-backdrop py-8 px-4" style="content-visibility: auto;">
	<div class="max-w-7xl mx-auto">
		<div class="flex items-center justify-between mb-6">
			<h2 class="text-2xl font-bold text-white flex items-center gap-2">
				<Icon class="w-5 h-5 text-tmdb-light" />
				{title}
			</h2>
			{#if tabs.length > 0}
				<div class="flex gap-4">
					{#each tabs as tab}
						<button
							onclick={() => onTabChange?.(tab)}
							class="pb-1 px-3 text-sm font-semibold transition cursor-pointer
								{isActive(tab) ? 'tab-active' : 'tab-inactive'}"
						>
							{tab}
						</button>
					{/each}
				</div>
			{/if}
		</div>

		<div class="relative">
			{#if isLoading}
				<div class="flex justify-center py-12">
					<div class="animate-spin rounded-full h-8 w-8 border-b-2 border-tmdb-light"></div>
				</div>
			{:else}
				<button
					onclick={() => scroll('left')}
					class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex"
				>
					<ChevronLeft class="w-6 h-6" />
				</button>

				<div bind:this={scrollContainer} class="flex gap-4 overflow-x-auto hide-scrollbar pb-4">
					{#each films as movie (movie.id)}
						<div use:observeCard={movie.id} data-movie-id={movie.id} class="flex-shrink-0">
							<MovieCard {movie} />
						</div>
					{/each}
				</div>

				<button
					onclick={() => scroll('right')}
					class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex"
				>
					<ChevronRight class="w-6 h-6" />
				</button>
			{/if}
		</div>
	</div>
</section>