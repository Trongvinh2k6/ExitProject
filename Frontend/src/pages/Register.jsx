import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/authService";
import "./AuthPage.css";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
    address: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

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
      await register(formData);
      navigate("/login");
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Đăng ký không thành công. Vui lòng thử lại"
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
            <p className="banner-subtitle">JOIN OUR COMMUNITY</p>
            <h1 className="banner-heading">
              Great ideas grow <br />
              with style.
            </h1>
            <p className="banner-description">
              Tạo tài khoản để nhận ưu đãi thành viên và lưu trữ thông tin đơn hàng của bạn.
            </p>
          </div>
          <div></div>
        </div>

        {/* Form bên phải */}
        <div className="auth-form-side">
          <div className="form-header">
            <p className="form-category">SKATEBOARD ACCOUNT</p>
            <h2 className="form-title">Create your account</h2>
            <p className="form-subtext">
              Điền thông tin bên dưới để tạo tài khoản mới.
            </p>
          </div>

          {error && <div className="error-banner">{error}</div>}

          <form onSubmit={handleSubmit} className="styled-form">
            <div className="input-block">
              <label>Họ và tên</label>
              <input
                type="text"
                name="name"
                placeholder="Nguyen Van A"
                value={formData.name}
                onChange={handleChange}
                required
                className="styled-input"
              />
            </div>

            <div className="input-block">
              <label>Email</label>
              <input
                type="email"
                name="email"
                placeholder="researcher@example.com"
                value={formData.email}
                onChange={handleChange}
                required
                className="styled-input"
              />
            </div>

            <div className="input-block">
              <label>Mật khẩu</label>
              <input
                type="password"
                name="password"
                placeholder="Ít nhất 6 kí tự"
                value={formData.password}
                onChange={handleChange}
                required
                className="styled-input"
              />
            </div>

            <div className="input-block">
              <label>Số điện thoại</label>
              <input
                type="text"
                name="phone"
                placeholder="0987654321"
                value={formData.phone}
                onChange={handleChange}
                className="styled-input"
              />
            </div>

            <div className="input-block">
              <label>Địa chỉ</label>
              <input
                type="text"
                name="address"
                placeholder="Nhập địa chỉ của bạn"
                value={formData.address}
                onChange={handleChange}
                className="styled-input"
              />
            </div>

            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? "Đang tạo tài khoản..." : "Tạo tài khoản"}
            </button>
          </form>

          <p className="switch-auth-text">
            Đã có tài khoản?
            <button
              type="button"
              className="switch-btn"
              onClick={() => navigate("/login")}
            >
              Đăng nhập ngay
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Register;