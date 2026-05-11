<script lang="ts">
	import VideoModal from '$lib/components/VideoModal.svelte';
	import type { Video } from '$lib/types/movie';

	let {
		videos = []
	}: {
		videos?: Video[];
	} = $props();

	let isOpen = $state(false);
	let currentVideo = $state<Video | null>(null);

	export function open(video?: Video) {
		const targetVideo = video || videos.find(v => v.type === 'Trailer') || videos[0];
		if (targetVideo) {
			currentVideo = targetVideo;
			isOpen = true;
		}
	}

	export function close() {
		isOpen = false;
	}
</script>

<VideoModal
	bind:isOpen
	videoUrl={currentVideo?.key || ''}
	title={currentVideo?.name || 'Trailer'}
/>