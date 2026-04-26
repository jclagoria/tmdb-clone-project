# TMDB Clone

A clone of TheMovieDB (TMDB) website built with modern web technologies.

## Project Idea

A movie and TV show browsing platform that replicates the core functionality and design of TMDB, featuring movie/TV discovery, trending content sections, and search capabilities.

## Architecture

```
┌─────────────────────────────────────────────┐
│                 SvelteKit                    │
│  (SSR + Client-side hydration)             │
├─────────────────────────────────────────────┤
│  Pages: Home | Search                       │
├─────────────────────────────────────────────┤
│  Components: Header, Hero, Section,        │
│  Carousel, MovieCard, Footer                │
├─────────────────────────────────────────────┤
│  Data Layer (API Abstraction)              │
│  - Mock mode for development                │
│  - Ready for TMDB API integration           │
├─────────────────────────────────────────────┤
│  State: Svelte 5 Runes ($state, $derived)  │
└─────────────────────────────────────────────┘
```

## Technologies

- **Framework:** SvelteKit + Svelte 5
- **Language:** TypeScript
- **Styling:** Tailwind CSS v4
- **UI Components:** shadcn-svelte
- **Carousel:** embla-carousel-svelte
- **Images:** @unpic/svelte
- **Icons:** lucide-svelte
- **Validation:** Zod
- **Build:** Vite

## Getting Started

```bash
# Install dependencies
pnpm install

# Start development server
pnpm dev

# Build for production
pnpm build
```

## Environment

- Node.js 20+
- pnpm (package manager)