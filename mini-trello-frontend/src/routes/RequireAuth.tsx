import { Navigate } from "react-router-dom";
import { useAuthContext } from "../auth/AuthContext";

export const RequireAuth = ({ children }: { children: React.ReactNode }) => {
  const { token } = useAuthContext();
  return token ? <>{children}</> : <Navigate to="/login" />;
};
