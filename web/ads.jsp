<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- ===== ADVERTISEMENT COMPONENT ===== -->
<!-- Điều kiện hiển thị: Chỉ hiển thị cho Free users (không phải teacher, premium, admin) -->
<c:if test="${not empty authUser}">
    <c:set var="premiumService" value="<%= new service.PremiumService() %>" />
    <c:set var="isPremium" value="${premiumService.isUserPremium(authUser.userID)}" />
    <c:set var="isTeacherOrAdmin" value="${authUser.roleID == 3 || authUser.roleID == 4}" />
    
    <c:if test="${!isPremium && !isTeacherOrAdmin}">

<!-- ===== CSS STYLES FOR PROMO BAR ===== -->
<style>
    /* ===== MAIN PROMO BAR STYLE ===== */
    .promo-bar {
        position: fixed;           /* Cố định vị trí */
        bottom: 20px;             /* Cách bottom 20px */
        left: 50%;                /* Căn giữa theo chiều ngang */
        transform: translateX(-50%) scale(1.2); /* Căn giữa và phóng to 1.2x */
        background: linear-gradient(to right, #ff3300, #ff6600); /* Gradient đỏ-cam */
        color: white;             /* Màu chữ trắng */
        display: flex;            /* Flexbox layout */
        align-items: center;      /* Căn giữa theo chiều dọc */
        justify-content: space-between; /* Phân bố đều các phần tử */
        padding: 8px 16px;       /* Padding trong */
        font-size: 13px;         /* Kích thước chữ */
        border-radius: 10px;      /* Bo góc */
        box-shadow: 0 4px 10px rgba(0,0,0,0.2); /* Bóng đổ */
        z-index: 9999;           /* Độ ưu tiên cao nhất */
        font-family: Arial, sans-serif; /* Font chữ */
        min-width: 40%;          /* Chiều rộng tối thiểu */
        gap: 10px;               /* Khoảng cách giữa các phần tử */
        display: none;           /* Ẩn ban đầu */
    }

    /* ===== PROMO TEXT STYLE ===== */
    .promo-text {
        font-weight: bold;        /* Chữ đậm */
        white-space: nowrap;      /* Không xuống dòng */
    }

    /* ===== COURSE BUTTONS STYLE ===== */
    .promo-courses button {
        margin: 0 3px;           /* Margin giữa các button */
        background: white;        /* Nền trắng */
        color: #d40000;          /* Chữ đỏ */
        border: none;            /* Không viền */
        padding: 4px 8px;        /* Padding */
        border-radius: 6px;      /* Bo góc */
        font-weight: bold;       /* Chữ đậm */
        cursor: pointer;         /* Con trỏ pointer */
        font-size: 12px;         /* Kích thước chữ */
    }

    /* ===== ACTION BUTTON STYLE ===== */
    .promo-action button {
        background-color: yellow; /* Nền vàng */
        color: #ff0000;          /* Chữ đỏ */
        border: none;            /* Không viền */
        padding: 6px 12px;       /* Padding */
        border-radius: 6px;      /* Bo góc */
        font-weight: bold;       /* Chữ đậm */
        font-size: 13px;         /* Kích thước chữ */
        cursor: pointer;         /* Con trỏ pointer */
        transition: background-color 0.2s ease; /* Hiệu ứng hover */
    }

    /* ===== HOVER EFFECT ===== */
    .promo-action button:hover {
        background-color: #fff176; /* Màu vàng nhạt khi hover */
    }

    /* ===== CLOSE BUTTON STYLE ===== */
    .close-btn {
        background: transparent;  /* Nền trong suốt */
        color: white;            /* Chữ trắng */
        font-size: 16px;         /* Kích thước chữ */
        border: none;            /* Không viền */
        cursor: pointer;         /* Con trỏ pointer */
    }

    /* ===== BLINKING BUTTON STYLE ===== */
    .blink-button {
        background-color: #e74c3c; /* Nền đỏ */
        color: white;            /* Chữ trắng */
        padding: 10px 20px;      /* Padding */
        border: none;            /* Không viền */
        border-radius: 6px;      /* Bo góc */
        font-size: 16px;         /* Kích thước chữ */
        font-weight: bold;       /* Chữ đậm */
        animation: blink 1s infinite; /* Hiệu ứng nhấp nháy */
        cursor: pointer;         /* Con trỏ pointer */
    }

    /* ===== BLINK ANIMATION ===== */
    @keyframes blink {
        0%, 100% {
            opacity: 1;          /* Hiển thị đầy đủ */
        }
        50% {
            opacity: 0.03;       /* Gần như ẩn */
        }
    }
</style>

<!-- ===== PROMO BAR HTML STRUCTURE ===== -->
<div id="promo-bar" class="promo-bar">
    <!-- ===== PROMO TEXT ===== -->
    <span class="promo-text">HỌC PHÍ <strong>0 ĐỒNG!</strong></span>
    
    <!-- ===== COURSE LEVELS ===== -->
    <div class="promo-courses">
        <button>N5</button>  <!-- Trình độ N5 -->
        <button>N3</button>  <!-- Trình độ N3 -->
        <button>N2</button>  <!-- Trình độ N2 -->
        <button>N1</button>  <!-- Trình độ N1 -->
    </div>
    
    <!-- ===== CALL-TO-ACTION ===== -->
    <div class="promo-action">
        <!-- Button "Đăng ký ngay" với hiệu ứng nhấp nháy -->
        <button class="blink-button" onclick="window.location.href = '<c:url value='/CoursesServlet' />'">Đăng ký ngay</button>
    </div>
    
    <!-- ===== CLOSE BUTTON ===== -->
    <button id="close-btn" class="close-btn">✕</button>
</div>

<!-- ===== JAVASCRIPT FUNCTIONALITY ===== -->
<script>
    // ===== GET DOM ELEMENTS =====
    const promoBar = document.getElementById("promo-bar");    // Lấy element promo bar
    const closeBtn = document.getElementById("close-btn");    // Lấy element nút đóng

    // ===== SHOW PROMO BAR AFTER 5 SECONDS =====
    // Hiển thị quảng cáo sau 5 giây khi trang load
    setTimeout(() => {
        promoBar.style.display = "flex";  // Hiển thị promo bar
    }, 5000);  // 5000ms = 5 giây

    // ===== CLOSE BUTTON EVENT LISTENER =====
    // Xử lý khi người dùng nhấn nút đóng
    closeBtn.addEventListener("click", function () {
        promoBar.style.display = "none";  // Ẩn promo bar

        // ===== RE-SHOW AFTER 15 SECONDS =====
        // Hiện lại quảng cáo sau 15 giây nếu đã tắt
        setTimeout(() => {
            promoBar.style.display = "flex";  // Hiển thị lại promo bar
        }, 15000);  // 15000ms = 15 giây
    });
</script>

    </c:if>
</c:if>
<!-- ===== END ADVERTISEMENT COMPONENT ===== -->
