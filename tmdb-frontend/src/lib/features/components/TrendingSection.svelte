<script lang="ts">
    import MovieCard from '$lib/components/MovieCard.svelte';
    import { ChevronLeft, ChevronRight, TrendingUp } from 'lucide-svelte';
    import { createTrendingStore } from "$lib/features/trending/store.svelte";

    const store = createTrendingStore();

    let scrollContainer: HTMLDivElement | null = $state(null);

    $effect(() => {
        store.loadTrending();
    });

    function scroll(direction: 'left' | 'right') {
        if (scrollContainer) {
            scrollContainer.scrollBy({
                left: direction === 'left' ? -400 : 400,
                behavior: 'smooth'
            });
        }
    }

    function isActive(tab: string): boolean {
        const tabValue = tab.toLowerCase() === 'today' ? 'day' : 'week';
        return store.activeTab === tabValue;
    }

    function handleTabClick(tab: string) {
        const value = tab.toLowerCase() === 'today' ? 'day' : 'week';
        store.setActiveTab(value);
    }
</script>

<section class="section-backdrop py-8 px-4">
    <div class="max-w-7xl mx-auto">
        <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-white flex items-center gap-2">
                <TrendingUp class="w-5 h-5 text-tmdb-light" />
                Trending
            </h2>
            <div class="flex gap-4">
                {#each store.tabs as tab}
                    <button
                        onclick={() => handleTabClick(tab)}
                        class="pb-1 px-3 text-sm font-semibold transition cursor-pointer
                            {isActive(tab) ? 'tab-active' : 'tab-inactive'}"
                    >
                        {tab}
                    </button>
                {/each}
            </div>
        </div>

        <div class="relative">
            {#if store.isLoading}
                <div class="flex justify-center py-12">
                    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-tmdb-light"></div>
                </div>
            {:else}
                <button
                    onclick={() => scroll('left')}
                    class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex"
                >
                    <ChevronLeft class="w-6 h-6" />
                </button>

                <div bind:this={scrollContainer} class="flex gap-4 overflow-x-auto hide-scrollbar pb-4">
                    {#each store.movies as movie (movie.id)}
                        <MovieCard {movie} />
                    {/each}
                </div>

                <button
                    onclick={() => scroll('right')}
                    class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 hover:bg-black/70 rounded-full p-2 hidden md:flex"
                >
                    <ChevronRight class="w-6 h-6" />
                </button>
            {/if}
        </div>
    </div>
</section>