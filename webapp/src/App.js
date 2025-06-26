import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import MainPage from "./pages/MainPage";
import ProfilePage from "./pages/ProfilePage";
import SharedFilePage from "./pages/SharedFilePage";

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AuthPage />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/main" element={<MainPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/shared/:sharedId" element={<SharedFilePage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;