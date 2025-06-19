import { jwtDecode } from "jwt-decode";
import { createContext, useContext, useState } from "react";

interface UserPayload {
  userId: string;
  username: string;
  email: string;
  role: string;
  exp: number;
  iat: number;
}

interface AuthContextType {
  token: string | null;
  user: UserPayload | null;
  login: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  user: null,
  login: () => {},
  logout: () => {},
});

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem("token"));
  const [user, setUser] = useState<UserPayload | null>(
    token ? jwtDecode<UserPayload>(token) : null
  );

  const login = (newToken: string) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
    setUser(jwtDecode<UserPayload>(newToken));
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuthContext = () => useContext(AuthContext);
