<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chat với AI</title>

    <!-- Font đẹp -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">


    <!-- JS chat box -->
    <script src="chat/chatbox.js"></script>
</head>

<body>

<section id="chatbox">
    <div id="chatButton" onclick="toggleChat()">💬 Hỗ trợ thông tin</div>

    <div id="chatContainer">
        <div id="chatHeader">
            <span>Chat với AI</span>
            <button onclick="toggleChat()">✖</button>
        </div>

        <div id="chatBox">
          <div class="aiMessage">
        <b>AI:</b> 🎌 Xin chào! Tôi là trợ lý Wasabii. Hãy hỏi tôi về tiếng Nhật hoặc các khóa học nhé!
    </div>
        </div>

        <div id="suggestBox">
            <button onclick="sendSampleQuestion()">Gợi ý: Đồ ăn tiếng Nhật là gì?</button>
        </div>

        <div id="butt">
            <input type="text" id="userInput" placeholder="Nhập tin nhắn..." onkeypress="handleKeyPress(event)" />
            <button id="buttonChat" onclick="sendMessage()">Gửi</button>
        </div>
    </div>
</section>

<script>
    function sendSampleQuestion() {
        document.getElementById("userInput").value = "Đồ ăn tiếng Nhật là gì?";
        sendMessage();
    }
</script>

</body>
</html>
