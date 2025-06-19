// src/pages/AuthLayout.tsx
import { Box, Button, Paper, Typography } from "@mui/material";
import { useState } from "react";
import RegisterPage from "./RegisterPage";
import LoginPage from "./LoginPage";

export default function AuthLayout() {
  const [isRegistering, setIsRegistering] = useState(false);

  return (
    <Box
      display="flex"
      height="100vh"
      alignItems="center"
      justifyContent="center"
      bgcolor="#f4f6f8"
    >
      <Paper elevation={3} sx={{ padding: 4, width: 400 }}>
        <Typography variant="h4" gutterBottom align="center">
          {isRegistering ? "Create Account" : "Welcome Back"}
        </Typography>
        {isRegistering ? <RegisterPage /> : <LoginPage />}

        <Box mt={2} textAlign="center">
          <Button onClick={() => setIsRegistering(!isRegistering)}>
            {isRegistering
              ? "Already have an account? Login"
              : "Don't have an account? Register"}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
