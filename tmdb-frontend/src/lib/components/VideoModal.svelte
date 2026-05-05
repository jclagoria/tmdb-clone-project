<script lang="ts">
    import { X } from 'lucide-svelte';
    import { fade, scale } from 'svelte/transition';

    let {
        isOpen = $bindable(false),
        videoUrl = '',
        title = '',
        onClose
    }: {
        isOpen: boolean;
        videoUrl: string;
        title: string;
        onClose?: () => void;
    } = $props();

    let lastFocusedElement: HTMLElement | null = $state(null);

    function getEmbedUrl(url: string): string {
        if (!url) return '';
        const ytMatch = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]+)/);
        if (ytMatch) {
            return `https://www.youtube.com/embed/${ytMatch[1]}?autoplay=1`;
        }
        return url;
    }

    const embedSrc = $derived(getEmbedUrl(videoUrl));

    function close() {
        isOpen = false;
        onClose?.();
        lastFocusedElement?.focus();
    }

    function handleBackdropClick(e: MouseEvent) {
        if (e.target === e.currentTarget) {
            close();
        }
    }

    function handleKeydown(e: KeyboardEvent) {
        if (e.key === 'Escape') {
            close();
        }
        if (e.key === 'Tab') {
            e.preventDefault();
        }
    }

    $effect(() => {
        if (isOpen) {
            lastFocusedElement = document.activeElement as HTMLElement;
        }
    });
</script>

<svelte:window onkeydown={handleKeydown} />

{#if isOpen}
    <div
            class="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-sm"
            role="presentation"
            transition:fade={{ duration: 200 }}
            onclick={handleBackdropClick}
            onkeydown={handleKeydown}
            tabindex="-1"
    >
        <div
                class="relative w-full max-w-4xl mx-4 bg-black rounded-lg overflow-hidden"
                transition:scale={{ duration: 200, start: 0.95 }}
        >
            <button
                    onclick={close}
                    class="absolute top-4 right-4 z-10 p-2 rounded-full bg-black/50 hover:bg-black/70 text-white transition focus-visible:ring-2 focus-visible:ring-white"
                    aria-label="Close"
            >
                <X class="w-6 h-6" />
            </button>

            <div class="aspect-video bg-black">
                {#if embedSrc}
                    <iframe
                            src={embedSrc}
                            title={title}
                            class="w-full h-full"
                            frameborder="0"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowfullscreen
                    ></iframe>
                {:else}
                    <div class="w-full h-full flex items-center justify-center text-gray-400">
                        <p>Video not available</p>
                    </div>
                {/if}
            </div>

            <div class="p-4 bg-gray-900">
                <h3 id="modal-title" class="text-white font-semibold text-lg">{title}</h3>
                <p id="modal-desc" class="text-gray-400 text-sm">Official Trailer</p>
            </div>
        </div>
    </div>
{/if}