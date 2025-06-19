import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import AuthLayout from "./pages/AuthLayout";
import {Dashboard} from "./pages/Dashboard";
import { AuthProvider } from "./auth/AuthContext";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<AuthLayout />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
