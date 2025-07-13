<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Giới thiệu - Wasabii</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Font -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"> 
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>">
        <style>
            body {
                font-family: 'JetBrains Mono', monospace;
                background-color: #ffe0ec;
                color: #5a0000;
            }

            .wasabii-about-container {
                max-width: 1200px;
                margin: auto;
                background: #ffcbe6;
                border-radius: 20px;
                padding: 40px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
                display: flex;
                flex-wrap: wrap;
                gap: 30px;
                align-items: center;
            }

            .wasabii-about-text {
                flex: 1;
            }

            .wasabii-about-text h1 {
                font-size: 36px;
                color: #b71c1c;
                margin-bottom: 20px;
            }

            .wasabii-about-text p {
                font-size: 18px;
                line-height: 1.8;
                margin-bottom: 16px;
            }

            .about-image {
                flex: 1;
                text-align: center;
            }

            .about-image img {
                width: 100%;
                max-width: 500px;
                border-radius: 15px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            }

            .more-info {
                background-color: #fff0f5;
                padding: 30px;
                margin-top: 40px;
                border-radius: 20px;
                max-width: 1200px;
                margin-left: auto;
                margin-right: auto;
            }

            .more-info h3 {
                color: #c2185b;
                margin-bottom: 15px;
            }

            .more-info ul {
                list-style: none;
                padding-left: 0;
            }

            .more-info ul li::before {
                content: "🌸";
                margin-right: 8px;
            }

            .more-info ul li {
                font-size: 16px;
                margin-bottom: 10px;
            }

            .feature-box {
                background: #ffe8f0;
                border-left: 5px solid #ec407a;
                padding: 15px 20px;
                border-radius: 10px;
                margin-bottom: 20px;
            }

            @media (max-width: 768px) {
                .wasabii-about-container {
                    flex-direction: column;
                    text-align: center;
                }
            }
        </style>
    </head>
    <body>
        <%@ include file="Home/nav.jsp" %>
        <%@ include file="chatBoxjsp/chatBox.jsp" %>
        <div class="wasabii-about-container">
            <div class="wasabii-about-text">
                <h1>Giới thiệu về Wasabii</h1>
                <p>Wasabii là nền tảng học tiếng Nhật hiện đại, kết hợp giữa công nghệ và phương pháp học hiệu quả.</p>
                <p>Chúng tôi mang đến trải nghiệm học tập tương tác, cá nhân hóa với nhiều công cụ hỗ trợ học viên đạt mục tiêu nhanh chóng.</p>
            </div>

            <div class="about-image">
                <!-- 👉 Chèn link ảnh tùy bạn -->
                <img src="<c:url value='/image/homepage.jpg'/>" alt="Học tiếng Nhật cùng Wasabii">
            </div>
        </div>

        <div class="more-info mt-5">
            <h3>🌟 Những tính năng nổi bật của Wasabii</h3>

            <div class="feature-box">
                <strong>📞 Video Call luyện giao tiếp trực tiếp:</strong>
                Học viên có thể tham gia các buổi trò chuyện trực tuyến với giáo viên bản ngữ hoặc bạn bè để rèn luyện phản xạ nói.
            </div>

            <div class="feature-box">
                <strong>📚 Tra cứu từ vựng thông minh:</strong>
                Công cụ tra cứu tích hợp với phát âm, hình ảnh, và ví dụ minh họa sinh động giúp việc ghi nhớ dễ dàng hơn.
            </div>

            <div class="feature-box">
                <strong>🤖 Chatbot AI trợ lý học tập:</strong>
                Được hỗ trợ bởi AI, chatbot của Wasabii luôn sẵn sàng giải đáp thắc mắc, gợi ý bài học và kiểm tra nhanh kiến thức.
            </div>

            <div class="feature-box">
                <strong>📝 Bài học chuẩn JLPT:</strong>
                Các khóa học được chia theo cấp độ N5 → N1, giúp bạn học đúng lộ trình, đúng mục tiêu.
            </div>

            <div class="feature-box">
                <strong>🌐 Học mọi lúc, mọi nơi:</strong>
                Hệ thống hỗ trợ đa nền tảng: Web, Mobile, máy tính bảng.
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>