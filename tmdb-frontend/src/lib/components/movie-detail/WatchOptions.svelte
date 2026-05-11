<script lang="ts">
	import { Play } from 'lucide-svelte';

	let {
		providerName = 'Apple TV',
		posterUrl
	}: {
		providerName?: string;
		posterUrl?: string;
	} = $props();
</script>

<div class="mb-6">
	<button class="w-full bg-white/10 hover:bg-white/20 rounded-lg p-4 flex items-center gap-3 transition">
		<div class="w-10 h-10 bg-tmdb-light rounded flex items-center justify-center">
			<Play class="w-5 h-5 text-tmdb-dark" />
		</div>
		<div class="text-left">
			<p class="text-white font-medium">Watch Now</p>
			<p class="text-gray-400 text-sm">{providerName}</p>
		</div>
	</button>
</div>