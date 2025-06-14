const container = document.querySelector('.container');
const registerBtn = document.querySelector('.register-btn');
const loginBtn = document.querySelector('.login-btn');

registerBtn.addEventListener('click', () => {
    container.classList.add('active');
    container.classList.remove('active-change');
});

loginBtn.addEventListener('click', () => {
    container.classList.remove('active');
    container.classList.remove('active-change');
});

const changePassBtn = document.createElement('button');

changePassBtn.addEventListener('click', () => {
    container.classList.add('active-change');
    container.classList.remove('active');
});

document.addEventListener("DOMContentLoaded", function () {
    var showRegisterForm = "${showRegisterForm}";
    if (showRegisterForm === "true") {
        document.querySelector(".form-box.register").classList.add("active");
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const forgotPasswordLink = document.querySelector(".forgot-link a");
    const container = document.querySelector(".container");

    // Khi nhấn vào "Change Password?", hiển thị form đổi mật khẩau
    forgotPasswordLink.addEventListener("click", function (event) {
        event.preventDefault();
        container.classList.add("active-change");
    });

    // Đóng form khi nhấn ra ngoài
    document.addEventListener("click", function (event) {
        const changePasswordForm = document.querySelector(".form-box.change-password");
        if (!changePasswordForm.contains(event.target) && !forgotPasswordLink.contains(event.target)) {
            container.classList.remove("active-change");
        }
    });
});
