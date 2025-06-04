import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Callback from "./pages/Callback.tsx";
import Dashboard from "./pages/Dashboard.tsx";
import GraphPage from "./pages/GraphPage.tsx";

function App() {

  return (
      <Router>
          <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/oauth2/callback" element={<Callback />} />
              <Route path={"/"} element={<Dashboard />} />
              <Route path="/calculator"    element={<GraphPage />} />
              {/* otras rutas */}
          </Routes>
      </Router>
  )
}

export default App;
