<script lang="ts">
	let {
		facebookUrl,
		twitterUrl,
		instagramUrl,
		homepageUrl
	}: {
		facebookUrl?: string;
		twitterUrl?: string;
		instagramUrl?: string;
		homepageUrl?: string;
	} = $props();
</script>

<div class="flex gap-3 mb-6">
	{#if facebookUrl}
		<a
			href={facebookUrl}
			target="_blank"
			rel="noopener noreferrer"
			class="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center hover:bg-tmdb-light transition-colors"
			aria-label="Facebook"
		>
			<i class="fab fa-facebook-f"></i>
		</a>
	{/if}
	{#if twitterUrl}
		<a
			href={twitterUrl}
			target="_blank"
			rel="noopener noreferrer"
			class="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center hover:bg-tmdb-light transition-colors"
			aria-label="Twitter"
		>
			<i class="fab fa-twitter"></i>
		</a>
	{/if}
	{#if instagramUrl}
		<a
			href={instagramUrl}
			target="_blank"
			rel="noopener noreferrer"
			class="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center hover:bg-tmdb-light transition-colors"
			aria-label="Instagram"
		>
			<i class="fab fa-instagram"></i>
		</a>
	{/if}
	{#if homepageUrl}
		<a
			href={homepageUrl}
			target="_blank"
			rel="noopener noreferrer"
			class="w-10 h-10 bg-white/10 rounded-full flex items-center justify-center hover:bg-tmdb-light transition-colors"
			aria-label="Website"
		>
			<i class="fas fa-link"></i>
		</a>
	{/if}
</div>