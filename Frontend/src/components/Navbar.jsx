import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";

function Navbar() {

    const navigate = useNavigate();

    const [showMenu, setShowMenu] = useState(false);

    const accessToken = localStorage.getItem("accessToken");

    // Lấy user từ localStorage
    const userData = localStorage.getItem("user");

    let user = null;

    try {
        if (userData && userData !== "undefined") {
            user = JSON.parse(userData);
        }
    } catch (error) {
        console.error("User data không hợp lệ:", userData);
        user = null;
    }


    const handleLogout = () => {

        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");

        setShowMenu(false);

        navigate("/login");
    };


    return (
        <nav className="navbar">

            <div className="navbar-container">

                {/* Logo */}
                <Link to="/" className="logo">
                    SKATEBOARD
                </Link>


                {/* Menu */}
                <div className="nav-links">

                    <Link to="/">
                        Home
                    </Link>

                    <Link to="/products">
                        Products
                    </Link>

                </div>


                {/* Bên phải */}
                <div className="nav-actions">

                    {/* CHƯA LOGIN */}
                    {!accessToken ? (

                        <>
                            <Link to="/login">
                                Login
                            </Link>

                            <Link
                                to="/register"
                                className="register-btn"
                            >
                                Register
                            </Link>
                        </>

                    ) : (

                        /* ĐÃ LOGIN */
                        <>

                            {/* Cart */}
                            <Link
                                to="/cart"
                                className="cart-link"
                            >
                                🛒 Cart
                            </Link>


                            {/* Profile */}
                            <div className="profile-menu">

                                <button
                                    type="button"
                                    className="profile-button"
                                    onClick={() =>
                                        setShowMenu(!showMenu)
                                    }
                                >

                                    {/* Avatar */}
                                    <div className="avatar">

                                        {user?.username
                                            ?.charAt(0)
                                            ?.toUpperCase() || "U"}

                                    </div>


                                    {/* Username */}
                                    <span>
                                        {user?.username || "User"}
                                    </span>


                                    {/* Arrow */}
                                    <span className="arrow">
                                        ▾
                                    </span>

                                </button>


                                {/* Dropdown */}
                                {showMenu && (

                                    <div className="dropdown-menu">

                                        <Link
                                            to="/profile"
                                            onClick={() =>
                                                setShowMenu(false)
                                            }
                                        >
                                            Profile
                                        </Link>


                                        <Link
                                            to="/settings"
                                            onClick={() =>
                                                setShowMenu(false)
                                            }
                                        >
                                            Settings
                                        </Link>


                                        <button
                                            type="button"
                                            onClick={handleLogout}
                                        >
                                            Logout
                                        </button>

                                    </div>

                                )}

                            </div>

                        </>

                    )}

                </div>

            </div>

        </nav>
    );
}

export default Navbar;

