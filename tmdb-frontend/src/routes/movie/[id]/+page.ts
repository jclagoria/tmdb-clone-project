import type { PageLoad } from './$types';
import { getMovieDetails } from '$lib/api';

export const load: PageLoad = async ({ params }) => {
	const movieId = params.id;
	const movie = await getMovieDetails(movieId);

	return {
		movie
	};
};