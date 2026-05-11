<script lang="ts">
	import type { Keyword } from '$lib/types/movie';

	let {
		keywords = []
	}: {
		keywords?: Keyword[];
	} = $props();
</script>

<div class="mb-6">
	<h3 class="text-gray-400 text-sm mb-3">Keywords</h3>
	<div class="flex flex-wrap gap-2">
		{#each keywords.slice(0, 15) as keyword}
			<a
				href="/keyword/{keyword.id}"
				class="bg-white/10 hover:bg-tmdb-light/20 text-gray-300 px-3 py-1 rounded-full text-sm transition-colors"
			>
				{keyword.name}
			</a>
		{/each}
	</div>
</div>