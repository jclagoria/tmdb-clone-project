<script lang="ts">
    import type { Movie } from "\$lib/types/movie";
    import { getTitle, getYear } from "$lib/types/movie";
    import { getImageUrl } from "$lib/api";
    import { Play } from "lucide-svelte";

    let { movie, onclick }: {movie: Movie; onclick: (e: MouseEvent) => void} = $props();

    const posterUrl = $derived(getImageUrl(movie.backdrop_path || movie.poster_path));
    const title = $derived(getTitle(movie));
    const year = $derived(getYear(movie));
</script>

<button
        class="flex-shrink-0 w-[280px] md:w-[320px] lg:w-[360px] cursor-pointer group text-left focus-visible:ring-2 focus-visible:ring-tmdb-light focus-visible:ring-offset-2 focus-visible:ring-offset-black rounded-lg"
        onclick={onclick}>
    <div class="relative rounded-lg overflow-hidden aspect-video poster-placeholder">
        {#if posterUrl}
            <img
                    src={posterUrl}
                    alt={title}
                    class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                    loading="lazy"
            />
        {:else}
            <div class="w-full h-full flex items-center justify-center text-4xl text-gray-600">
                &#127916;
            </div>
        {/if}

        <div
                class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"
        ></div>

        <div
                class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 group-focus-within:opacity-100 transition-opacity duration-300">
            <div
                    class="w-16 h-16 md:w-20 md:h-20 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                <Play class="w-8 h-8 md:w-10 md:h-10 text-white fill-white ml-1" />
            </div>
        </div>

        <div
                class="absolute bottom-0 left-0 right-0 p-4 translate-y-2 opacity-0 group-hover:translate-y-0 group-hover:opacity-100 transition-all duration-300">
            <h3 class="font-bold text-white text-lg line-clamp-1">{title}</h3>
            <p class="text-gray-300 text-sm">{year} • Trailer</p>
        </div>
    </div>
</button>