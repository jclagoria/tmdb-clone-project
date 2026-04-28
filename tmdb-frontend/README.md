# TMDB Clone

A clone of TheMovieDB (TMDB) website built with modern web technologies.

## Project Idea

A movie and TV show browsing platform that replicates the core functionality and design of TMDB, featuring movie/TV discovery, trending content sections, and search capabilities.

## Architecture

> ⭐ **Feature-Based Architecture**
> 
> This project follows a feature-based architecture for scalability. Each feature (e.g., `trending`, `search`, `popular`) is self-contained in `src/lib/features/<feature>/` with its own API, types, store, and components.
> 
> See [docs/trending/](./docss/trending/) for a complete example implementation.

```
src/lib/
├── features/           ⭐ Feature-based modules
│   └── trending/
│       ├── api.ts
│       ├── types.ts
│       ├── store.svelte.ts
│       └── components/
├── components/         # Shared UI components
├── api/               # API utilities
└── types/             # Shared types
```

---

## Previous Architecture (Flat)

```
src/lib/
├── api/index.ts       # All API calls (single file)
├── components/        # All components flat
└── types/movie.ts     # Mixed types
```

---

The feature-based approach enables:
- **Isolation**: Each feature is independent and testable
- **Scalability**: Add new features without modifying shared code
- **Maintainability**: Changes are scoped to specific features

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