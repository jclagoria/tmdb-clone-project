<script lang="ts">
	import { page } from '$app/stores';
	import { AlertTriangle, Home, ArrowLeft } from 'lucide-svelte';
</script>

<div class="min-h-screen bg-tmdb-dark flex items-center justify-center px-4">
	<div class="max-w-md w-full text-center">
		<div class="mb-8">
			<AlertTriangle class="w-24 h-24 text-tmdb-light mx-auto mb-4" />
			<h1 class="text-6xl font-bold text-white mb-2">{$page.status}</h1>
			<p class="text-xl text-gray-400">Oops! Something went wrong</p>
		</div>

		<div class="bg-tmdb-darker rounded-lg p-6 mb-6">
			<p class="text-gray-300">{$page.error?.message || 'An unexpected error occurred'}</p>
		</div>

		<div class="flex gap-4 justify-center">
			<a
				href="/"
				class="flex items-center gap-2 px-6 py-3 bg-tmdb-light text-white rounded-lg hover:bg-tmdb-lightest transition"
			>
				<Home class="w-5 h-5" />
				Go Home
			</a>
			<button
				onclick={() => window.history.back()}
				class="flex items-center gap-2 px-6 py-3 bg-gray-700 text-white rounded-lg hover:bg-gray-600 transition"
			>
				<ArrowLeft class="w-5 h-5" />
				Go Back
			</button>
		</div>
	</div>
</div>