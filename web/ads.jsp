<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty authUser && authUser.roleID == 1}">
    <style>
        #ads-box {
            position: fixed;
            bottom: 20px;
            left: 20px; /* Chuyển sang góc trái */
            width: 400px; /* To hơn */
            background-color: #fff3e0;
            border: 1px solid #ffcc80;
            padding: 28px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.25);
            z-index: 9999;
            display: none;
            border-radius: 12px;
            font-family: "Segoe UI", sans-serif;
        }

        #ads-box h4 {
            margin: 0 0 12px 0;
            font-size: 22px;
            color: #e65100;
        }

        #ads-box p {
            margin: 0;
            font-size: 16px;
            color: #4e342e;
        }

        #ads-box form {
            margin-top: 16px;
            text-align: right;
        }

        #ads-box button {
            background-color: #fb8c00;
            color: #fff;
            border: none;
            padding: 10px 20px;
            cursor: pointer;
            border-radius: 6px;
            font-size: 15px;
            font-weight: bold;
        }

        #ads-box button:hover {
            background-color: #f57c00;
        }
    </style>

    <div id="ads-box">
        <h4>🌟 Nâng cấp Premium ngay</h4>
        <p>Loại bỏ quảng cáo, học nhanh hơn, thêm tính năng cao cấp chỉ dành cho bạn.</p>
        <form action="${pageContext.request.contextPath}/payment">
            <button type="submit">Nâng cấp ngay</button>
        </form>
    </div>

    <script>
        setTimeout(function () {
            document.getElementById("ads-box").style.display = "block";
        }, 15000);
    </script>
</c:if>
