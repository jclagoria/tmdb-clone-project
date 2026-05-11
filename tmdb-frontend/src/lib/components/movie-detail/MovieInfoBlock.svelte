<script lang="ts">
	import { formatCurrency } from '$lib/types/movie';
	import type { Genre } from '$lib/types/movie';

	let {
		status,
		originalLanguage,
		budget,
		revenue,
		genres = []
	}: {
		status?: string;
		originalLanguage?: string;
		budget?: number;
		revenue?: number;
		genres?: Genre[];
	} = $props();

	const languageNames: Record<string, string> = {
		en: 'English',
		es: 'Spanish',
		fr: 'French',
		de: 'German',
		it: 'Italian',
		ja: 'Japanese',
		ko: 'Korean',
		zh: 'Chinese',
		pt: 'Portuguese',
		ru: 'Russian',
		hi: 'Hindi'
	};
</script>

<div class="bg-white/5 rounded-lg p-4 mb-6">
	{#if status}
		<div class="mb-3">
			<p class="text-gray-400 text-sm">Status</p>
			<p class="text-white text-sm">{status}</p>
		</div>
	{/if}

	{#if originalLanguage}
		<div class="mb-3">
			<p class="text-gray-400 text-sm">Original Language</p>
			<p class="text-white text-sm">{languageNames[originalLanguage] || originalLanguage}</p>
		</div>
	{/if}

	{#if genres.length > 0}
		<div class="mb-3">
			<p class="text-gray-400 text-sm">Genres</p>
			<div class="flex flex-wrap gap-1">
				{#each genres as genre}
					<span class="text-tmdb-light hover:underline cursor-pointer text-sm">{genre.name}</span>
				{/each}
			</div>
		</div>
	{/if}

	{#if budget && budget > 0}
		<div class="mb-3">
			<p class="text-gray-400 text-sm">Budget</p>
			<p class="text-white text-sm">{formatCurrency(budget)}</p>
		</div>
	{/if}

	{#if revenue && revenue > 0}
		<div>
			<p class="text-gray-400 text-sm">Revenue</p>
			<p class="text-white text-sm">{formatCurrency(revenue)}</p>
		</div>
	{/if}
</div>