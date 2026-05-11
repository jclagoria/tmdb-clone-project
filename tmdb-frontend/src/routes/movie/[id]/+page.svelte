<script lang="ts">
	import { Play, Plus, Heart, Bookmark } from 'lucide-svelte';
	import { getImageUrl } from '$lib/api';
	import { formatRuntime } from '$lib/types/movie';
	import type { PageData } from './$types';
	import CastList from '$lib/components/movie-detail/CastList.svelte';
	import MediaGallery from '$lib/components/movie-detail/MediaGallery.svelte';
	import ReviewSection from '$lib/components/movie-detail/ReviewSection.svelte';
	import SocialLinks from '$lib/components/movie-detail/SocialLinks.svelte';
	import KeywordsList from '$lib/components/movie-detail/KeywordsList.svelte';
	import MovieInfoBlock from '$lib/components/movie-detail/MovieInfoBlock.svelte';
	import WatchOptions from '$lib/components/movie-detail/WatchOptions.svelte';
	import VideoModal from '$lib/components/VideoModal.svelte';

	let { data }: { data: PageData } = $props();

	const movie = $derived(data.movie);
	const releaseYear = $derived(movie.release_date?.split('-')[0] || '');
	const ratingPercentage = $derived(Math.round(movie.vote_average * 10));

	let activeTab = $state<'overview' | 'media' | 'fandom' | 'share'>('overview');
	let showTrailerModal = $state(false);

	const tabs = [
		{ id: 'overview', label: 'Overview' },
		{ id: 'media', label: 'Media' },
		{ id: 'fandom', label: 'Fandom' },
		{ id: 'share', label: 'Share' }
	] as const;

	function handleTabChange(tabId: typeof activeTab) {
		activeTab = tabId;
	}
</script>

<svelte:head>
	<title>{movie.title} ({releaseYear}) — The Movie Database (TMDB)</title>
</svelte:head>

<div class="min-h-screen pb-12 overflow-x-hidden">
	<nav class="bg-[#0d253f] border-b border-white/10">
		<div class="max-w-7xl mx-auto px-4">
			<div class="flex gap-8">
				{#each tabs as tab}
					<button
						onclick={() => handleTabChange(tab.id)}
						class="py-4 px-2 font-medium transition-colors {activeTab === tab.id
							? 'text-white border-b-4 border-tmdb-light'
							: 'text-gray-400 hover:text-white'}"
					>
						{tab.label}
					</button>
				{/each}
			</div>
		</div>
	</nav>

	<div class="max-w-7xl mx-auto px-4 py-6">
		{#if activeTab === 'overview'}
			<section class="flex flex-col md:flex-row gap-8 mb-8">
				<div class="flex-shrink-0 w-[300px]">
					<div class="relative cursor-pointer group">
						<img
							src={getImageUrl(movie.poster_path, 'w500')}
							alt={movie.title}
							class="w-full rounded-lg shadow-lg transition-transform group-hover:scale-105"
						/>
						<div class="absolute bottom-4 right-4 w-16 h-16 rounded-full flex items-center justify-center text-white font-bold text-sm bg-gradient-to-br from-green-400 to-green-600">
							{ratingPercentage}%
						</div>
					</div>
				</div>

				<div class="flex-1">
					<h1 class="text-4xl font-bold text-white mb-2">
						{movie.title} <span class="text-gray-400 text-2xl">({releaseYear})</span>
					</h1>

					<div class="flex flex-wrap items-center gap-3 mb-4 text-gray-300">
						<span class="bg-gray-700 px-2 py-0.5 rounded text-sm">+13</span>
						<span>{movie.release_date}</span>
						{#each movie.genres as genre}
							<span class="text-tmdb-light hover:underline cursor-pointer">{genre.name}</span>
							{#if movie.genres.indexOf(genre) < movie.genres.length - 1}
								<span class="text-gray-500">,</span>
							{/if}
						{/each}
						<span class="text-gray-500">•</span>
						<span>{formatRuntime(movie.runtime)}</span>
					</div>

					{#if movie.tagline}
						<h2 class="text-xl text-gray-300 italic mb-4">"{movie.tagline}"</h2>
					{/if}

					<div class="flex flex-wrap gap-4 mb-6">
						<button
							onclick={() => showTrailerModal = true}
							class="flex items-center gap-2 bg-tmdb-light hover:bg-tmdb-lightest text-tmdb-dark px-4 py-2 rounded-lg font-semibold transition"
						>
							<Play class="w-5 h-5" />
							<span>Play Trailer</span>
						</button>
						<button class="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white px-4 py-2 rounded-lg transition">
							<Plus class="w-5 h-5" />
							<span class="text-sm">Add to list</span>
						</button>
						<button class="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white px-4 py-2 rounded-lg transition">
							<Heart class="w-5 h-5" />
							<span class="text-sm">Favorite</span>
						</button>
						<button class="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white px-4 py-2 rounded-lg transition">
							<Bookmark class="w-5 h-5" />
							<span class="text-sm">Watchlist</span>
						</button>
					</div>

					<div class="bg-white/5 rounded-lg p-4 mb-6">
						<p class="text-gray-400 mb-3">What's your Vibe?</p>
						<div class="flex gap-2">
							<button class="text-2xl hover:scale-125 transition-transform" title="Sad">😢</button>
							<button class="text-2xl hover:scale-125 transition-transform" title="Love it">😍</button>
							<button class="text-2xl hover:scale-125 transition-transform" title="Great">😀</button>
						</div>
					</div>

					<section class="mb-6">
						<h3 class="text-lg font-semibold text-white mb-3">Overview</h3>
						<p class="text-gray-300 leading-relaxed">{movie.overview}</p>
					</section>

					<section class="grid grid-cols-2 md:grid-cols-4 gap-4">
						{#each movie.credits.crew.filter(c => ['Director', 'Novel', 'Screenplay'].includes(c.job)) as person}
							<div>
								<p class="text-gray-400 text-sm mb-1">{person.job}</p>
								<a href="/person/{person.id}" class="text-tmdb-light hover:underline">{person.name}</a>
							</div>
						{/each}
					</section>
				</div>
			</section>

			<section class="flex flex-col lg:flex-row gap-8 w-full overflow-hidden">
				<div class="flex-1 min-w-0">
					<CastList cast={movie.credits.cast.slice(0, 9)} />

					<ReviewSection review={movie.reviews.results[0]} />

					<MediaGallery
						backdrops={movie.backdrop_path ? [movie.backdrop_path] : []}
						posters={[movie.poster_path].filter(Boolean) as string[]}
						videos={movie.videos.results}
					/>
				</div>

				<aside class="lg:w-[350px] flex-shrink-0 min-w-0">
					<WatchOptions />

					<SocialLinks />

					<MovieInfoBlock
						status={movie.status}
						originalLanguage={movie.original_language}
						budget={movie.budget}
						revenue={movie.revenue}
						genres={movie.genres}
					/>

					<KeywordsList keywords={movie.keywords} />
				</aside>
			</section>
		{:else if activeTab === 'media'}
			<div class="py-12 text-center text-gray-400">
				<i class="fas fa-photo-video text-6xl mb-4"></i>
				<p>Media content for this movie</p>
				<p class="text-sm">Videos, backdrops, and posters</p>
			</div>
		{:else if activeTab === 'fandom'}
			<div class="py-12 text-center text-gray-400">
				<i class="fas fa-users text-6xl mb-4"></i>
				<p>Fandom content</p>
				<p class="text-sm">Links, trivia, and community</p>
			</div>
		{:else if activeTab === 'share'}
			<div class="py-12 text-center text-gray-400">
				<i class="fas fa-share-alt text-6xl mb-4"></i>
				<p>Share this movie</p>
				<p class="text-sm">Social media links</p>
			</div>
		{/if}
	</div>
</div>

<VideoModal
	bind:isOpen={showTrailerModal}
	videoUrl={movie.videos.results[0]?.key || ''}
	title={movie.videos.results[0]?.name || `${movie.title} - Trailer`}
/>