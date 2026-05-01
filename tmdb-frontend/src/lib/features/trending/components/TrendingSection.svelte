<script lang="ts">
    import HorizontalScrollSection from '$lib/components/HorizontalScrollSection.svelte';
    import { TrendingUp } from 'lucide-svelte';
    import { createTrendingStore } from "$lib/features/trending/store.svelte.js";

    const store = createTrendingStore();

    $effect(() => {
        store.loadTrending();
    });

    function handleTabClick(tab: string) {
        store.setActiveTab(tab);
    }
</script>

<HorizontalScrollSection
    title="Trending"
    Icon={TrendingUp}
    films={store.movies}
    tabs={store.tabs}
    bind:activeTab={store.activeTab}
    isLoading={store.isLoading}
    onTabChange={handleTabClick}
/>