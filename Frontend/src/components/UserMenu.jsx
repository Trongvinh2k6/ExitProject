import React, { useState, useRef, useEffect } from "react";
import { Link } from "react-router-dom";
import { FiUser, FiSettings, FiLogOut, FiChevronDown } from "react-icons/fi";

function UserMenu({ user, onLogout }) {
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef(null);

    // Click ra ngoài để đóng dropdown
    useEffect(() => {
        function handleClickOutside(event) {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // 1. Tên hiển thị: Lấy fullname giải mã từ Token (user.fullname / user.fullName / user.name)
    const displayName = user?.fullname || user?.fullName || user?.name || user?.username || "User";
    
    // 2. Email hiển thị: Lấy từ sub hoặc email trong token
    const displayEmail = user?.email || user?.sub || "";

    // 3. Chữ cái đầu làm Avatar (Lấy từ fullname)
    const avatarLetter = displayName.charAt(0).toUpperCase();

    return (
        <div className="user-menu-container" ref={menuRef}>
            {/* Nút Trigger hiển thị Avatar & Fullname */}
            <button 
                className="user-menu-trigger" 
                onClick={() => setIsOpen(!isOpen)}
            >
                <div className="avatar-circle">
                    {user?.avatar ? (
                        <img src={user.avatar} alt="Avatar" />
                    ) : (
                        <span>{avatarLetter}</span>
                    )}
                </div>
                
                {/* Hiển thị FULLNAME ở đây thay vì email */}
                <div className="user-info-brief">
                    <span className="user-name">{displayName}</span>
                </div>

                <FiChevronDown className={`chevron-icon ${isOpen ? "open" : ""}`} />
            </button>

            {/* Menu Dropdown xổ xuống */}
            {isOpen && (
                <div className="user-dropdown-menu">
                    {/* Header hiển thị cả Fullname và Email bên trong menu */}
                    <div className="dropdown-header">
                        <p className="header-name">{displayName}</p>
                        {displayEmail && <p className="header-email">{displayEmail}</p>}
                    </div>

                    <div className="dropdown-divider"></div>

                    {/* Danh sách các nút chức năng */}
                    <ul className="dropdown-links">
                        <li>
                            <Link to="/profile" onClick={() => setIsOpen(false)}>
                                <FiUser className="link-icon" />
                                <span>Profile</span>
                            </Link>
                        </li>
                        <li>
                            <Link to="/settings" onClick={() => setIsOpen(false)}>
                                <FiSettings className="link-icon" />
                                <span>Settings</span>
                            </Link>
                        </li>
                    </ul>

                    <div className="dropdown-divider"></div>

                    {/* Nút Log out */}
                    <button className="logout-btn" onClick={onLogout}>
                        <FiLogOut className="link-icon" />
                        <span>Logout</span>
                    </button>
                </div>
            )}
        </div>
    );
}

export default UserMenu;