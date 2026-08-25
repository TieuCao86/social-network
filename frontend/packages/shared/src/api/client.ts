import axios, {
    type AxiosInstance,
    type CreateAxiosDefaults
} from "axios";

export function createApiClient(
    baseURL: string,
    config?: CreateAxiosDefaults
): AxiosInstance {
    return axios.create({
        baseURL,
        timeout: 10000,
        headers: {
            "Content-Type": "application/json",
        },
        ...config,
    });
}