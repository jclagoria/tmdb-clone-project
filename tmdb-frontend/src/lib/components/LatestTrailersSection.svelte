<script lang="ts">
    import { onMount } from 'svelte';
    import EmblaCarousel from 'embla-carousel-svelte';
    import type { EmblaCarouselType } from 'embla-carousel';
    import { ChevronLeft, ChevronRight, PlayCircle } from 'lucide-svelte';
    import TrailerCard from '$lib/components/TrailerCard.svelte';
    import VideoModal from '$lib/components/VideoModal.svelte';
    import { createLatestTrailersStore } from '$lib/features/latest-trailers/store.svelte';

    const store = createLatestTrailersStore();

    let emblaApi: EmblaCarouselType | null = $state(null);
    let modalOpen = $state(false);

    $effect(() => {
        modalOpen = store.isModalOpen;
    });

    onMount(() => {
        store.loadMovies();
    });

    function handleModalClose() {
        store.closeModal();
    }

    function handleTabClick(tab: string) {
        store.setActiveTab(tab);
    }

    function handlePrev() {
        emblaApi?.scrollPrev();
    }

    function handleNext() {
        emblaApi?.scrollNext();
    }

    function handleMovieClick(movie: any) {
        store.openModal(movie);
    }
</script>

<section class="section-backdrop py-8 px-4" style="content-visibility: auto;">
    <div class="max-w-7xl mx-auto">
        <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <PlayCircle class="w-5 h-5 text-tmdb-light" />
                Latest Trailers
            </h2>
            <div class="flex gap-4 flex-wrap">
                {#each store.tabs as tab}
                    <button
                            onclick={() => handleTabClick(tab)}
                            class="pb-1 px-3 text-sm font-semibold transition cursor-pointer
                            {store.activeTab === tab.toLowerCase().replace(/\s+/g, '')
                                ? 'tab-active'
                                : 'tab-inactive'}"
                    >
                        {tab}
                    </button>
                {/each}
            </div>
        </div>

        {#if store.isLoading && store.movies.length === 0}
            <div class="flex justify-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
            </div>
        {:else if store.error && store.movies.length === 0}
            <div class="text-center py-12 text-red-400">
                <p>{store.error}</p>
            </div>
        {:else}
            <div class="relative">
                <button
                        onclick={handlePrev}
                        class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex items-center justify-center -translate-x-4"
                >
                    <ChevronLeft class="w-6 h-6 text-white" />
                </button>

                <div
                        use:EmblaCarousel={{ plugins: [], options: { loop: false, dragFree: false, skipSnaps: false } }}
                        onemblaInit={(e: CustomEvent<EmblaCarouselType>) => (emblaApi = e.detail)}
                        class="overflow-hidden"
                >
                    <div class="flex gap-4">
                        {#each store.movies as movie (movie.id)}
                            <div class="flex-[0_0_75%] md:flex-[0_0_50%] lg:flex-[0_0_33%] min-w-0">
                                <TrailerCard {movie} onclick={() => handleMovieClick(movie)} />
                            </div>
                        {/each}
                    </div>
                </div>

                <button
                        onclick={handleNext}
                        class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex items-center justify-center translate-x-4"
                >
                    <ChevronRight class="w-6 h-6 text-white" />
                </button>
            </div>
        {/if}
    </div>
</section>

<VideoModal
        bind:isOpen={modalOpen}
        videoUrl={store.selectedMovie?.trailer_url || ''}
        title={store.selectedMovie?.title || ''}
        onClose={handleModalClose}
/>