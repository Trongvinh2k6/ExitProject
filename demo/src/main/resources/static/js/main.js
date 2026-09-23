// 1. Hàm bổ trợ tự động đính kèm Token và làm mới Token nếu hết hạn (401)
async function fetchWithAuth(url, options = {}) {
    let token = localStorage.getItem('accessToken');

    options.headers = {
        ...options.headers,
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    let response = await fetch(url, options);

    // Nếu Token hết hạn (401), thử refresh token bằng cookie
    if (response.status === 401) {
        const refreshResponse = await fetch('/auth/refresh-with-cookie', { method: 'POST' });
        
        if (refreshResponse.ok) {
            const refreshResult = await refreshResponse.json();
            const newToken = refreshResult.data.accessToken;
            localStorage.setItem('accessToken', newToken);
            
            // Thử lại request ban đầu với Token mới
            options.headers['Authorization'] = `Bearer ${newToken}`;
            response = await fetch(url, options);
        } else {
            // Refresh Token cũng hết hạn -> Chuyển về trang chủ/đăng nhập
            localStorage.clear();
            alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!');
            window.location.href = '/home';
        }
    }

    return response;
}

// 2. Hàm Đăng nhập
async function login(username, password) {
    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (response.ok && result.data) {
            // Lưu accessToken và thông tin user vào localStorage
            localStorage.setItem('accessToken', result.data.accessToken);
            localStorage.setItem('userInfo', JSON.stringify(result.data.user));
            alert('Đăng nhập thành công!');
            location.reload(); // Tải lại trang để cập nhật trạng thái người dùng
        } else {
            alert('Đăng nhập thất bại: ' + (result.message || 'Sai tài khoản hoặc mật khẩu'));
        }
    } catch (error) {
        console.error('Lỗi khi đăng nhập:', error);
    }
}

// 3. Lấy danh sách sản phẩm
async function loadProducts(brand = null, category = null) {
    let url = '/products';
    const params = new URLSearchParams();
    
    if (brand) params.append('brand', brand);
    if (category) params.append('category', category);
    if (params.toString()) url += '?' + params.toString();

    try {
        const response = await fetch(url);
        const result = await response.json();

        if (response.ok && result.data) {
            renderProductCards(result.data);
        }
    } catch (error) {
        console.error('Lỗi tải sản phẩm:', error);
    }
}

// 4. Render sản phẩm ra giao diện HTML
function renderProductCards(products) {
    const container = document.getElementById('product-list');
    if (!container) return;

    container.innerHTML = products.map(product => `
        <div class="bg-card p-4 rounded-xl border border-gray-800 flex flex-col justify-between">
            <img src="${product.imageUrl || 'https://via.placeholder.com/200'}" alt="${product.name}" class="w-full h-48 object-cover rounded-lg mb-4"/>
            <div>
                <h3 class="text-white font-bold text-lg">${product.name}</h3>
                <p class="text-brand-500 font-semibold mt-1">${product.price ? product.price.toLocaleString() : 0} VNĐ</p>
            </div>
            <button onclick="addToCart(${product.id})" class="mt-4 w-full bg-brand-500 hover:bg-brand-600 text-white font-medium py-2 rounded-lg transition">
                Thêm vào giỏ
            </button>
        </div>
    `).join('');
}

// 5. Thêm sản phẩm vào giỏ hàng (Đã sửa dùng fetchWithAuth)
async function addToCart(productId) {
    const user = JSON.parse(localStorage.getItem('userInfo'));

    if (!user) {
        alert('Vui lòng đăng nhập trước!');
        return;
    }

    try {
        const response = await fetchWithAuth(`/users/${user.id}/cart/items`, {
            method: 'POST',
            body: JSON.stringify({
                productId: productId,
                quantity: 1
            })
        });

        const result = await response.json();
        if (response.ok) {
            alert('Đã thêm sản phẩm vào giỏ hàng!');
            if (typeof updateCartBadge === 'function') {
                updateCartBadge();
            }
        } else {
            alert('Lỗi: ' + (result.message || 'Không thể thêm vào giỏ hàng'));
        }
    } catch (error) {
        console.error('Lỗi thêm giỏ hàng:', error);
    }
}

// 6. Tự động chạy khi trang web hoàn tất tải DOM
document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});