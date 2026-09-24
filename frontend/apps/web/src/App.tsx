import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { LoginPage } from "./pages/LoginPage";
import { HomePage } from "./pages/HomePage";
import { ProfilePage } from "./pages/ProfilePage";
import { SearchPage } from "./pages/SearchPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route path="/home" element={<HomePage />} />

        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/profile/:userId" element={<ProfilePage />} />

        <Route path="/search" element={<SearchPage />} />

        <Route
          path="*"
          element={<Navigate to="/home" replace />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;