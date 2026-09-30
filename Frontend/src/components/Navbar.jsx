import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import UserMenu from "./UserMenu";

function Navbar() {
    const navigate = useNavigate();
    const accessToken = localStorage.getItem("accessToken");

    let user = null;

    if (accessToken) {
        try {
            const decoded = jwtDecode(accessToken);
            user = {
                id: decoded.id,
                email: decoded.sub,
                fullname: decoded.fullname 
            };
        } catch (error) {
            console.error("Token không hợp lệ:", error);
            user = null;
        }
    }

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        localStorage.removeItem("userId");
        navigate("/auth/login");
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="logo">
                    SKATEBOARD
                </Link>

                <div className="nav-links">
                    <Link to="/">Home</Link>
                    <Link to="/products">Products</Link>
                    {/* Thêm link Orders ở đây nếu người dùng đã đăng nhập */}
                    {accessToken && <Link to="/orders">My Orders</Link>}
                </div>

                <div className="nav-right">
                    <Link to="/cart" className="cart-btn">
                        🛒 Cart
                    </Link>

                    {accessToken ? (
                        <UserMenu user={user} onLogout={handleLogout} />
                    ) : (
                        <div className="auth-buttons">
                            <Link to="/auth/login" className="login-btn">
                                Login
                            </Link>
                            <Link to="/auth/register" className="register-btn">
                                Register
                            </Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;