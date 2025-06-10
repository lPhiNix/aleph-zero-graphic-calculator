import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Callback from "./pages/Callback.tsx";
import Dashboard from "./pages/Dashboard.tsx";
import Calculator from "./pages/Calculator.tsx";

function App() {

  return (
      <Router>
          <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/oauth2/callback" element={<Callback />} />
              <Route path={"/"} element={<Dashboard />} />
              <Route path="/calculator"    element={<Calculator />} />
          </Routes>
      </Router>
  )
}

export default App;
