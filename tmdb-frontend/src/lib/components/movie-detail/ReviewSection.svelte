<script lang="ts">
	import type { Review } from '$lib/types/movie';

	let {
		review,
		onReadAllReviews
	}: {
		review: Review;
		onReadAllReviews?: () => void;
	} = $props();

	const ratingPercentage = $derived((review.author_details.rating / 5) * 100);

	function formatDate(dateString: string): string {
		return new Date(dateString).toLocaleDateString('en-US', {
			year: 'numeric',
			month: 'long',
			day: 'numeric'
		});
	}

	function getInitial(name: string): string {
		return name.charAt(0).toUpperCase();
	}
</script>

<section class="mb-8">
	<h2 class="text-lg font-semibold text-white mb-4">Social</h2>

	<div class="flex gap-4 mb-4">
		<button onclick={onReadAllReviews} class="text-tmdb-light hover:underline">
			Reviews {review ? '' : ''}
		</button>
		<button class="text-tmdb-light hover:underline">
			Discussions
		</button>
	</div>

	{#if review}
		<div class="bg-white/5 rounded-lg p-4">
			<div class="flex items-center gap-3 mb-3">
				<div class="w-10 h-10 rounded-full bg-tmdb-light flex items-center justify-center text-white font-bold">
					{getInitial(review.author_details.name || review.author)}
				</div>
				<div>
					<span class="text-white font-medium">
						A review by {review.author_details.name || review.author}
					</span>
					<p class="text-gray-400 text-sm">
						Written by {review.author_details.username || review.author} on {formatDate(review.created_at)}
					</p>
				</div>
				<div class="ml-auto bg-green-500 text-white px-2 py-1 rounded text-sm font-bold">
					{Math.round(ratingPercentage)}%
				</div>
			</div>
			<p class="text-gray-300 text-sm mb-3">
				{review.content.length > 300 ? review.content.slice(0, 300) + '...' : review.content}
			</p>
			<p class="text-white font-bold">{review.author_details.rating}/5</p>
			{#if onReadAllReviews}
				<button onclick={onReadAllReviews} class="text-tmdb-light hover:underline text-sm mt-2">
					Read All Reviews
				</button>
			{/if}
		</div>
	{/if}
</section>