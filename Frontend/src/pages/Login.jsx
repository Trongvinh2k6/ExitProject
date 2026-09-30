import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import "./AuthPage.css";

function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };



const handleSubmit = async (e) => {
  e.preventDefault();

  setError("");
  setLoading(true);

  try {
    const response = await login(formData);

    console.log("Login response:", response);

    // Lấy data từ response
    const data = response.data;

    // Lấy access token
    const accessToken = data.accessToken;

    // Lấy thông tin user
    const user = data.user;

    // Lưu access token
    localStorage.setItem("accessToken", accessToken);

    // Lưu user
    localStorage.setItem(
      "user",
      JSON.stringify(user)
    );

    // 3. THÊM DÒNG NÀY: Lưu riêng userId để trang Profile sử dụng
    if (user && user.id) {
      localStorage.setItem("userId", user.id);
    }

    console.log("Saved user:", user);

    navigate("/");

  } catch (err) {
    console.error(err);

    setError(
      err.response?.data?.message ||
      "Email hoặc mật khẩu không chính xác"
    );
  } finally {
    setLoading(false);
  }
};




  return (
    <div className="auth-wrapper">
      <div className="auth-card-container">
        {/* Banner bên trái */}
        <div className="auth-banner-side">
          <div className="brand-logo">
            <div className="brand-icon">S</div>
            <span className="brand-title">SKATEBOARD</span>
          </div>
          <div className="banner-content">
            <p className="banner-subtitle">STEP INTO YOUR STYLE</p>
            <h1 className="banner-heading">
              Find your <br />
              perfect shoes.
            </h1>
            <p className="banner-description">
              Khám phá bộ sưu tập giày thể thao cá tính và phong cách dành riêng cho bạn.
            </p>
          </div>
          <div></div>
        </div>

        {/* Form bên phải */}
        <div className="auth-form-side">
          <div className="form-header">
            <p className="form-category">SKATEBOARD ACCOUNT</p>
            <h2 className="form-title">Welcome back</h2>
            <p className="form-subtext">
              Đăng nhập để tiếp tục trải nghiệm mua sắm của bạn.
            </p>
          </div>

          {error && <div className="error-banner">{error}</div>}

          <form onSubmit={handleSubmit} className="styled-form">
            <div className="input-block">
              <label>Email</label>
              <input
                type="email"
                name="username"
                placeholder="researcher@example.com"
                value={formData.username}
                onChange={handleChange}
                required
                className="styled-input"
              />
            </div>

            <div className="input-block">
              <label>Mật khẩu</label>
              <div className="input-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="Nhập mật khẩu"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  className="styled-input"
                />
                <button
                  type="button"
                  className="toggle-password-btn"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? "👁️‍🗨️" : "👁️"}
                </button>
              </div>
            </div>

            <div className="form-row-helpers">
              <label className="remember-me">
                <input type="checkbox" /> Ghi nhớ thiết bị
              </label>
              <a href="#forgot" className="forgot-link">
                Quên mật khẩu?
              </a>
            </div>

            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? "Đang xử lý..." : "Đăng nhập"}
            </button>
          </form>

          <div className="divider">
            <span>HOẶC</span>
          </div>

          <button type="button" className="google-btn">
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
              />
            </svg>
            Tiếp tục với Google
          </button>

          <p className="switch-auth-text">
            Chưa có tài khoản?
            <button
              type="button"
              className="switch-btn"
              onClick={() => navigate("/auth/register")}
            >
              Tạo tài khoản mới
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;