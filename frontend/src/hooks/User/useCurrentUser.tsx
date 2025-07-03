import { useEffect, useState } from 'react';
import AxiosConfig from '../../services/axiosService.ts';

/**
 * User Data Transfer Object (DTO) interface for user info returned from the API.
 */
interface UserDto {
    publicId: string;
    username: string;
}

/**
 * useCurrentUser
 * Custom hook to retrieve and manage the current user's data from the backend.
 * Fetches user info from /api/user and exposes the user and loading state.
 *
 * @returns {object} { user, loading }
 */
export function useCurrentUser() {
    const [user, setUser] = useState<UserDto | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const axios = AxiosConfig.getInstance();
                const res = await axios.get('/api/user');
                console.log("Respuesta /api/user:", res.data); // For debugging
                setUser(res.data?.content);
            } catch (e) {
                setUser(null);
            } finally {
                setLoading(false);
            }
        };
        fetchUser();
    }, []);

    return { user, loading };
}