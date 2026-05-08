<script lang="ts">
    import HorizontalScrollSection from "$lib/components/HorizontalScrollSection.svelte";
    import { Star } from "lucide-svelte";
    import { createWhatsPopularStore } from "$lib/features/whats-popular/store.svelte";

    const store = createWhatsPopularStore();

    $effect(() => {
        store.loadWhatsPopular();
        return () => {
            // Cleanup if needed
        };
    });

    function handleTabClick(tab: string) {
        store.setActiveTab(tab);
    }
</script>

<HorizontalScrollSection
    title="What's Popular"
    Icon={Star}
    films={store.movies}
    tabs={store.tabs}
    bind:activeTab={store.activeTab}
    isLoading={store.isLoading}
    onTabChange={handleTabClick}
/>