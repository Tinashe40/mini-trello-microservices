import axios from "axios";
import {jwtDecode} from "jwt-decode";

const instance = axios.create({
  baseURL: "http://localhost:8080/api/", // API Gateway Base URL
  headers: {
    "Content-Type": "application/json",
  },
});

interface DecodedToken {
  sub: string;
  userId: string | number;
  email: string;
  role: string;
  exp: number;
  iat: number;
}

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;

    try {
      const decoded: DecodedToken = jwtDecode(token);

      config.headers["X-Username"] = decoded.sub;
      config.headers["X-User-Id"] = decoded.userId;
      config.headers["X-Email"] = decoded.email;
      config.headers["X-User-Role"] = decoded.role;
    } catch (error) {
      console.warn("Failed to decode JWT:", error);
    }
  }

  return config;
});

export default instance;
