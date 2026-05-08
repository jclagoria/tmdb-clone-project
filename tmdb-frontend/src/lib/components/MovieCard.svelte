<script lang="ts">
	import type { Movie } from '$lib/types/movie';
	import { getGenreName, getRatingColor, getYear, getTitle } from '$lib/types/movie';
	import { getImageUrl } from '$lib/api';
	import { goto } from '$app/navigation';

	let { movie }: { movie: Movie } = $props();

	const ratingColor = $derived(getRatingColor(movie.vote_average));
	const year = $derived(getYear(movie));
	const title = $derived(getTitle(movie));
	const posterUrl = $derived(getImageUrl(movie.poster_path));

	let imageLoaded = $state(false);

	function handleImageLoad(e: Event) {
		const img = e.currentTarget as HTMLImageElement;
		imageLoaded = true;
	}

	function handleClick() {
		goto(`/movie/${movie.id}`);
	}
</script>

<button 
	class="flex-shrink-0 w-40 md:w-44 card-hover cursor-pointer group text-left"
	onclick={handleClick}
>
	<div class="relative rounded-lg overflow-hidden poster-placeholder aspect-[2/3]">
		{#if posterUrl}
			<img 
				srcset="{getImageUrl(movie.poster_path, 'w342')} 342w, {getImageUrl(movie.poster_path, 'w500')} 500w"
				sizes="(max-width: 768px) 160px, 180px"
				src={posterUrl}
				alt={title}
				loading="lazy"
				decoding="async"
				onload={handleImageLoad}
				class="w-full h-full object-cover transition-opacity duration-300 {imageLoaded ? 'opacity-100' : 'opacity-0'}"
			/>
		{:else}
			<div class="w-full h-full flex items-center justify-center text-4xl text-gray-600">
				&#127916;
			</div>
		{/if}
		<div class="absolute top-2 right-2 w-10 h-10 {ratingColor} rounded-full flex items-center justify-center text-xs font-bold shadow-lg text-white">
			{movie.vote_average.toFixed(1)}
		</div>
		<div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
			<svg class="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
				<path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
			</svg>
		</div>
	</div>
	<div class="mt-3">
		<h3 class="font-semibold text-sm text-white line-clamp-2 leading-tight">{title}</h3>
		<p class="text-gray-400 text-xs mt-1">{year}</p>
	</div>
</button>