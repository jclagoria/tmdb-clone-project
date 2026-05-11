<script lang="ts">
	import { getImageUrl } from '$lib/api';
	import type { Video } from '$lib/types/movie';

	let {
		backdrops = [],
		posters = [],
		videos = [],
		onViewAllVideos,
		onViewAllBackdrops,
		onViewAllPosters
	}: {
		backdrops?: string[];
		posters?: string[];
		videos?: Video[];
		onViewAllVideos?: () => void;
		onViewAllBackdrops?: () => void;
		onViewAllPosters?: () => void;
	} = $props();

	let activeTab = $state<'popular' | 'videos' | 'backdrops' | 'posters'>('popular');

	const tabs = $derived([
		{ id: 'popular', label: 'Most Popular' },
		{ id: 'videos', label: `Videos ${videos.length}` },
		{ id: 'backdrops', label: `Backdrops ${backdrops.length}` },
		{ id: 'posters', label: `Posters ${posters.length}` }
	] as const);
</script>

<section class="mb-8">
	<h2 class="text-lg font-semibold text-white mb-4">Media</h2>

	<div class="flex gap-4 mb-4 border-b border-gray-700 pb-2">
		{#each tabs as tab}
			<button
				onclick={() => activeTab = tab.id}
				class="pb-2 text-sm transition-colors {activeTab === tab.id
					? 'text-white border-b-2 border-tmdb-light'
					: 'text-gray-400 hover:text-white'}"
			>
				{tab.label}
			</button>
		{/each}
	</div>

	{#if activeTab === 'popular'}
		<div class="grid grid-cols-2 gap-2">
			{#each backdrops.slice(0, 2) as backdrop, i}
				<div class="relative overflow-hidden rounded-lg cursor-pointer group">
					<img
						src={getImageUrl(backdrop, 'w780')}
						alt="Backdrop {i + 1}"
						class="w-full h-40 object-cover transition-opacity group-hover:opacity-80"
						loading="lazy"
					/>
				</div>
			{/each}
		</div>
	{:else if activeTab === 'videos'}
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
			{#each videos.slice(0, 3) as video}
				<div class="bg-gray-800 rounded-lg overflow-hidden cursor-pointer hover:opacity-80 transition-opacity">
					<div class="aspect-video bg-gray-700 flex items-center justify-center relative">
						<i class="fas fa-play-circle text-5xl text-gray-500"></i>
					</div>
					<p class="p-3 text-white text-sm">{video.name}</p>
				</div>
			{/each}
		</div>
		{#if onViewAllVideos}
			<button onclick={onViewAllVideos} class="text-tmdb-light hover:underline text-sm mt-4">
				View All {videos.length} Videos
			</button>
		{/if}
	{:else if activeTab === 'backdrops'}
		<div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2">
			{#each backdrops.slice(0, 8) as backdrop, i}
				<div class="relative overflow-hidden rounded cursor-pointer group">
					<img
						src={getImageUrl(backdrop, 'w300')}
						alt="Backdrop {i + 1}"
						class="w-full h-32 object-cover transition-opacity group-hover:opacity-80"
						loading="lazy"
					/>
				</div>
			{/each}
		</div>
		{#if onViewAllBackdrops}
			<button onclick={onViewAllBackdrops} class="text-tmdb-light hover:underline text-sm mt-4">
				View All {backdrops.length} Backdrops
			</button>
		{/if}
	{:else if activeTab === 'posters'}
		<div class="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-2">
			{#each posters.slice(0, 6) as poster, i}
				<div class="relative overflow-hidden rounded cursor-pointer group">
					<img
						src={getImageUrl(poster, 'w342')}
						alt="Poster {i + 1}"
						class="w-full rounded transition-opacity group-hover:opacity-80"
						loading="lazy"
					/>
				</div>
			{/each}
		</div>
		{#if onViewAllPosters}
			<button onclick={onViewAllPosters} class="text-tmdb-light hover:underline text-sm mt-4">
				View All {posters.length} Posters
			</button>
		{/if}
	{/if}
</section>