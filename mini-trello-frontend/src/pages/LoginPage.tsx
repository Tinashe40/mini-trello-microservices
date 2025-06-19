import {
  Box,
  Button,
  TextField,
  Typography,
  Snackbar,
  Alert,
  type AlertColor
} from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "../api/axios";
import { isAxiosError } from "axios";
import { useAuthContext } from "../auth/AuthContext";

export default function LoginPage() {
  const { login } = useAuthContext();
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [snack, setSnack] = useState<{
    open: boolean;
    message: string;
    severity: AlertColor;
  }>({
    open: false,
    message: "",
    severity: "info"
  });

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      setSnack({
        open: true,
        message: "All fields are required",
        severity: "warning"
      });
      return;
    }

    try {
      const res = await axios.post("/auth/login", { username, password });
      login(res.data.token);
      navigate("/dashboard");
    } catch (err) {
      if (isAxiosError(err)) {
        setSnack({
          open: true,
          message: err.response?.data?.message || "Login failed",
          severity: "error"
        });
      } else {
        setSnack({
          open: true,
          message: "Unknown error occurred",
          severity: "error"
        });
      }
    }
  };

  return (
    <Box maxWidth={400} mx="auto" mt={10}>
      <Typography variant="h5" gutterBottom>
        Login
      </Typography>
      <form onSubmit={handleLogin}>
        <TextField
          fullWidth
          margin="normal"
          label="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <TextField
          fullWidth
          margin="normal"
          label="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <Button type="submit" variant="contained" fullWidth sx={{ mt: 2 }}>
          Login
        </Button>
      </form>

      <Snackbar
        open={snack.open}
        autoHideDuration={4000}
        onClose={() => setSnack({ ...snack, open: false })}
      >
        <Alert
          severity={snack.severity}
          onClose={() => setSnack({ ...snack, open: false })}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
