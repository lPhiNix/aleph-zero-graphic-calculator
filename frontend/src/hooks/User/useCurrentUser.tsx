import { useEffect, useState } from 'react';
import AxiosConfig from '../../services/axiosService.ts';

interface UserDto {
    publicId: string;
    username: string;
}

export function useCurrentUser() {
    const [user, setUser] = useState<UserDto | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const axios = AxiosConfig.getInstance();
                const res = await axios.get('/api/user');
                console.log("Respuesta /api/user:", res.data);
                setUser(res.data?.content); // <- ESTO ES LO CORRECTO
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