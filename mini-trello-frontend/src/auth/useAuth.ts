import {jwtDecode} from "jwt-decode";
export const useAuth = () => {
  const token = localStorage.getItem("token");
  const user = token ? jwtDecode(token) : null;
  return { token, user };
};
