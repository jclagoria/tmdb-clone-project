<script lang="ts">
	import { ChevronLeft, ChevronRight } from 'lucide-svelte';
	import { getImageUrl } from '$lib/api';
	import type { CastMember } from '$lib/types/movie';

	let {
		cast,
		onViewFullCast
	}: {
		cast: CastMember[];
		onViewFullCast?: () => void;
	} = $props();

	let scrollContainer: HTMLDivElement;
	let showLeftArrow = $state(false);
	let showRightArrow = $state(true);

	function scrollLeft() {
		scrollContainer.scrollBy({ left: -300, behavior: 'smooth' });
	}

	function scrollRight() {
		scrollContainer.scrollBy({ left: 300, behavior: 'smooth' });
	}

	function updateScrollButtons() {
		if (scrollContainer) {
			showLeftArrow = scrollContainer.scrollLeft > 0;
			showRightArrow = scrollContainer.scrollLeft < scrollContainer.scrollWidth - scrollContainer.clientWidth - 10;
		}
	}
</script>

<section class="mb-8 overflow-hidden">
	<div class="flex justify-between items-center mb-4">
		<h2 class="text-lg font-semibold text-white">Top Billed Cast</h2>
		{#if onViewFullCast}
			<button onclick={onViewFullCast} class="text-tmdb-light hover:underline text-sm">
				Full Cast & Crew
			</button>
		{/if}
	</div>

	<div class="relative group/carousel overflow-hidden">
		{#if showLeftArrow}
			<button
				onclick={scrollLeft}
				class="absolute left-0 top-1/2 -translate-y-1/2 z-10 w-10 h-10 bg-black/60 hover:bg-black/80 rounded-full flex items-center justify-center text-white opacity-0 group-hover/carousel:opacity-100 transition-opacity"
				aria-label="Scroll left"
			>
				<ChevronLeft class="w-6 h-6" />
			</button>
		{/if}

		<div
			bind:this={scrollContainer}
			onscroll={updateScrollButtons}
			class="flex gap-4 overflow-x-auto pb-4 hide-scrollbar"
		>
			{#each cast as member}
				<div class="flex-shrink-0 w-[138px] cursor-pointer group">
					<div class="rounded-lg overflow-hidden mb-2 transition-opacity hover:opacity-80">
						{#if member.profile_path}
							<img
								src={getImageUrl(member.profile_path, 'w185')}
								alt={member.name}
								class="w-[138px] h-[175px] object-cover bg-gray-800"
								loading="lazy"
							/>
						{:else}
							<div class="w-[138px] h-[175px] bg-gray-800 flex items-center justify-center">
								<span class="text-gray-500 text-2xl">{member.name[0]}</span>
							</div>
						{/if}
					</div>
					<p class="cast-name text-white font-medium text-sm truncate group-hover:text-tmdb-light transition-colors">
						{member.name}
					</p>
					<p class="text-gray-400 text-xs truncate">{member.character}</p>
				</div>
			{/each}

			{#if onViewFullCast}
				<button
					onclick={onViewFullCast}
					class="flex-shrink-0 w-[100px] flex items-center justify-center cursor-pointer hover:bg-white/10 rounded-lg transition-colors"
				>
					<span class="text-tmdb-light text-sm">
						View More <i class="fas fa-chevron-right ml-1"></i>
					</span>
				</button>
			{/if}
		</div>

		{#if showRightArrow}
			<button
				onclick={scrollRight}
				class="absolute right-0 top-1/2 -translate-y-1/2 z-10 w-10 h-10 bg-black/60 hover:bg-black/80 rounded-full flex items-center justify-center text-white opacity-0 group-hover/carousel:opacity-100 transition-opacity"
				aria-label="Scroll right"
			>
				<ChevronRight class="w-6 h-6" />
			</button>
		{/if}
	</div>
</section>