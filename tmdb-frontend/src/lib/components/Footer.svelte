<script lang="ts">
	import { goto } from '$app/navigation';
</script>

<footer class="bg-tmdb-dark py-12 px-4 border-t border-white/10">
	<div class="max-w-7xl mx-auto">
		<div class="flex flex-col md:flex-row items-start justify-between gap-8">
			<div class="flex flex-col items-center md:items-start gap-4">
				<svg class="w-12 h-12" viewBox="0 0 32 32" fill="none">
					<path d="M16 2L2 9.5v13L16 30l14-7.5v-13L16 2z" fill="#01b6e9"/>
					<path d="M16 8.5L8 12.5v7l8 4 8-4v-7l-8-4z" fill="#0d253f"/>
					<circle cx="16" cy="16" r="3" fill="#01b6e9"/>
				</svg>
				<button 
					onclick={() => goto('/')}
					class="px-6 py-2 bg-tmdb-light text-tmdb-dark font-semibold rounded hover:bg-tmdb-lightest transition"
				>
					Join the Community
				</button>
			</div>
			
			<div>
				<h3 class="font-bold mb-3 text-tmdb-light">THE BASICS</h3>
				<ul class="space-y-2 text-gray-400 text-sm">
					<li><a href="/" class="hover:text-white transition">About TMDB</a></li>
					<li><a href="/" class="hover:text-white transition">Contact Us</a></li>
					<li><a href="/" class="hover:text-white transition">API Documentation</a></li>
					<li><a href="/" class="hover:text-white transition">API for Business</a></li>
					<li><a href="/" class="hover:text-white transition">System Status</a></li>
				</ul>
			</div>
			
			<div>
				<h3 class="font-bold mb-3 text-tmdb-light">GET INVOLVED</h3>
				<ul class="space-y-2 text-gray-400 text-sm">
					<li><a href="/" class="hover:text-white transition">Contribution Bible</a></li>
					<li><a href="/" class="hover:text-white transition">Add New Movie</a></li>
					<li><a href="/" class="hover:text-white transition">Add New TV Show</a></li>
				</ul>
			</div>
			
			<div>
				<h3 class="font-bold mb-3 text-tmdb-light">COMMUNITY</h3>
				<ul class="space-y-2 text-gray-400 text-sm">
					<li><a href="/" class="hover:text-white transition">Guidelines</a></li>
					<li><a href="/" class="hover:text-white transition">Discussions</a></li>
					<li><a href="/" class="hover:text-white transition">Leaderboard</a></li>
					<li><a href="/" class="hover:text-white transition">Support Forums</a></li>
				</ul>
			</div>
			
			<div>
				<h3 class="font-bold mb-3 text-tmdb-light">LEGAL</h3>
				<ul class="space-y-2 text-gray-400 text-sm">
					<li><a href="/" class="hover:text-white transition">Terms of Use</a></li>
					<li><a href="/" class="hover:text-white transition">API Terms of Use</a></li>
					<li><a href="/" class="hover:text-white transition">Privacy Policy</a></li>
					<li><a href="/" class="hover:text-white transition">DMCA Policy</a></li>
				</ul>
			</div>
		</div>
		
		<div class="mt-12 pt-6 border-t border-white/10 text-center text-gray-500 text-sm">
			Build cfa4bc8 (10113)
		</div>
	</div>
</footer>