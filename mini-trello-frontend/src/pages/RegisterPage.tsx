import { Box, Button, CircularProgress, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { isAxiosError } from "axios";
import api from "../api/axios";
import { useAuthContext } from "../auth/AuthContext";

export default function RegisterPage() {
  const { login } = useAuthContext();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: ""
  });

  const [errors, setErrors] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    apiError: ""
  });

  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "", apiError: "" }); // clear error on change
  };

  const validateForm = () => {
    const newErrors: typeof errors = {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      apiError: ""
    };

    if (!formData.username.trim()) newErrors.username = "Username is required";
    if (!formData.email.trim()) newErrors.email = "Email is required";
    else if (!/\S+@\S+\.\S+/.test(formData.email))
      newErrors.email = "Enter a valid email";
    if (!formData.password) newErrors.password = "Password is required";
    else if (formData.password.length < 6)
      newErrors.password = "Password must be at least 6 characters";
    if (formData.confirmPassword !== formData.password)
      newErrors.confirmPassword = "Passwords do not match";

    setErrors(newErrors);
    return Object.values(newErrors).every((e) => !e);
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      const res = await api.post("/auth/register", {
        username: formData.username,
        email: formData.email,
        password: formData.password
      });

      login(res.data.token); // Auto-login
      navigate("/dashboard");
    } catch (err: unknown) {
      if (isAxiosError(err)) {
        const message =
          err.response?.data?.message || "Registration failed. Try again.";
        setErrors((prev) => ({ ...prev, apiError: message }));
      } else {
        setErrors((prev) => ({
          ...prev,
          apiError: "Unknown error. Please try again."
        }));
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box maxWidth={400} mx="auto" mt={10}>
      <Typography variant="h5" gutterBottom>
        Register
      </Typography>

      <form onSubmit={handleRegister}>
        <TextField
          fullWidth
          margin="normal"
          label="Username"
          name="username"
          value={formData.username}
          onChange={handleChange}
          error={!!errors.username}
          helperText={errors.username}
        />

        <TextField
          fullWidth
          margin="normal"
          label="Email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          error={!!errors.email}
          helperText={errors.email}
        />

        <TextField
          fullWidth
          margin="normal"
          label="Password"
          name="password"
          type="password"
          value={formData.password}
          onChange={handleChange}
          error={!!errors.password}
          helperText={errors.password}
        />

        <TextField
          fullWidth
          margin="normal"
          label="Confirm Password"
          name="confirmPassword"
          type="password"
          value={formData.confirmPassword}
          onChange={handleChange}
          error={!!errors.confirmPassword}
          helperText={errors.confirmPassword}
        />

        {errors.apiError && (
          <Typography color="error" variant="body2" mt={1}>
            {errors.apiError}
          </Typography>
        )}

        <Button
          type="submit"
          variant="contained"
          fullWidth
          sx={{ mt: 2 }}
          disabled={isLoading}
        >
          {isLoading ? <CircularProgress size={24} /> : "Register"}
        </Button>
      </form>
    </Box>
  );
}
