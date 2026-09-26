import { Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import UserMenu from "./UserMenu";

function Navbar() {
    const navigate = useNavigate();
    const accessToken = localStorage.getItem("accessToken");

    let user = null;

    if (accessToken) {
        try {
            // 解碼 Token
            const decoded = jwtDecode(accessToken);
            
            user = {
                id: decoded.id,
                email: decoded.sub,
                // Lấy claim fullname từ token bạn đã cấu hình ở Spring Boot
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
        navigate("/login");
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
                </div>

                <div className="nav-right">
                    <Link to="/cart" className="cart-btn">
                        🛒 Cart
                    </Link>

                    {accessToken ? (
                        /* Đảm bảo truyền biến user đã được decode ở trên vào đây */
                        <UserMenu user={user} onLogout={handleLogout} />
                    ) : (
                        <div className="auth-buttons">
                            <Link to="/login" className="login-btn">
                                Login
                            </Link>
                            <Link to="/register" className="register-btn">
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