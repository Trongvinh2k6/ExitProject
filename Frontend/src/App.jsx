import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import Navbar from "./components/Navbar";

import Home from "./pages/Home";
import Products from "./pages/Products";
import ProductDetail from "./pages/ProductDetail";

import Register from "./pages/Register";
import Login from "./pages/Login";
import Profile from './pages/Profile';
import Cart from './pages/Cart';

function App() {

    return (

        <BrowserRouter>

            <Navbar />

            <Routes>

                <Route
                    path="/"
                    element={<Home />}
                />

                <Route
                    path="/products"
                    element={<Products />}
                />

                <Route 
                    path="/register" 
                    element={<Register />} 
                />

                <Route 
                    path="/login" 
                    element={<Login />} 
                />

                <Route
                    path="/products/:id"
                    element={<ProductDetail />}
                />

                <Route 
                    path="/profile" 
                    element={<Profile />} 
                />

                <Route 
                    path="/cart" 
                    element={<Cart />} 
                />

            </Routes>


        </BrowserRouter>

    );
}

export default App;

