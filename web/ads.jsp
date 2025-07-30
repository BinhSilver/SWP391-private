<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty authUser && authUser.roleID == 1}">
<style>
    .promo-bar {
        position: fixed;
        bottom: 20px;
        left: 50%;
        transform: translateX(-50%) scale(1.2);
        background: linear-gradient(to right, #ff3300, #ff6600);
        color: white;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 16px;
        font-size: 13px;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.2);
        z-index: 9999;
        font-family: Arial, sans-serif;
        min-width: 40%;
        gap: 10px;
        display: none; /* Ẩn ban đầu */
    }

    .promo-text {
        font-weight: bold;
        white-space: nowrap;
    }

    .promo-courses button {
        margin: 0 3px;
        background: white;
        color: #d40000;
        border: none;
        padding: 4px 8px;
        border-radius: 6px;
        font-weight: bold;
        cursor: pointer;
        font-size: 12px;
    }

    .promo-action button {
        background-color: yellow;
        color: #ff0000;
        border: none;
        padding: 6px 12px;
        border-radius: 6px;
        font-weight: bold;
        font-size: 13px;
        cursor: pointer;
        transition: background-color 0.2s ease;
    }

    .promo-action button:hover {
        background-color: #fff176;
    }

    .close-btn {
        background: transparent;
        color: white;
        font-size: 16px;
        border: none;
        cursor: pointer;
    }

    .blink-button {
        background-color: #e74c3c;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 6px;
        font-size: 16px;
        font-weight: bold;
        animation: blink 1s infinite;
        cursor: pointer;
    }

    @keyframes blink {
        0%, 100% {
            opacity: 1;
        }
        50% {
            opacity: 0.03;
        }
    }
</style>

<div id="promo-bar" class="promo-bar">
    <span class="promo-text">HỌC PHÍ <strong>0 ĐỒNG!</strong></span>
    <div class="promo-courses">
        <button>N5</button>
        <button>N3</button>
        <button>N2</button>
        <button>N1</button>
    </div>
    <div class="promo-action">
        <button class="blink-button" onclick="window.location.href = '<c:url value='/login' />'">Đăng ký ngay</button>
    </div>
    <button id="close-btn" class="close-btn">✕</button>
</div>

<script>
    const promoBar = document.getElementById("promo-bar");
    const closeBtn = document.getElementById("close-btn");

    // Hiển thị sau 5 giây
    setTimeout(() => {
        promoBar.style.display = "flex";
    }, 5000);

    // Khi người dùng nhấn tắt
    closeBtn.addEventListener("click", function () {
        promoBar.style.display = "none";

        // Hiện lại sau 15 giây
        setTimeout(() => {
            promoBar.style.display = "flex";
        }, 15000);
    });
</script>
</c:if>
