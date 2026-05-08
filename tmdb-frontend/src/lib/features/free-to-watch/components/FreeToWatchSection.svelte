<script lang="ts">
    import HorizontalScrollSection from "$lib/components/HorizontalScrollSection.svelte";
    import { Monitor } from "lucide-svelte";
    import { createFreeToWatchStore } from "$lib/features/free-to-watch/store.svelte";

    const store = createFreeToWatchStore();

    $effect(() => {
        store.loadMovies();
        return () => {
            // Cleanup if needed
        };
    });

    function handleTabClick(tab: string) {
        store.setActiveTab(tab);
    }
</script>

<HorizontalScrollSection
    title="Free To Watch"
    Icon={Monitor}
    films={store.movies}
    tabs={store.tabs}
    bind:activeTab={store.activeTab}
    isLoading={store.isLoading}
    onTabChange={handleTabClick}
/>