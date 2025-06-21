<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chat với AI</title>

    <!-- Font đẹp -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <!-- CSS nội bộ -->
    <style>
        body, input, button {
            font-family: 'Poppins', sans-serif;
        }

        #chatButton {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background-color: #f488ad;
            color: white;
            padding: 12px 20px;
            border-radius: 25px;
            cursor: pointer;
            font-size: 16px;
            box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
            z-index: 9999;
        }

        #chatButton:hover {
            background-color: #e76b95;
        }

        #chatContainer {
            position: fixed;
            bottom: 80px;
            right: 20px;
            width: 300px;
            height: 400px;
            background: white;
            border-radius: 10px;
            border: 1px solid #ccc;
            display: none;
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3);
            z-index: 9999;
            display: flex;
            flex-direction: column;
        }

        #chatHeader {
            background-color: #f488ad;
            color: white;
            padding: 10px;
            text-align: center;
            font-size: 16px;
            font-weight: bold;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            position: relative;
        }

        #chatHeader button {
            position: absolute;
            right: 10px;
            top: 5px;
            background: none;
            border: none;
            color: white;
            font-size: 18px;
            cursor: pointer;
        }

        #chatBox {
            padding: 10px;
            flex: 1;
            overflow-y: auto;
            background-color: #f9f9f9;
        }

        #butt {
            display: flex;
            padding: 5px 10px;
        }

        #userInput {
            flex: 1;
            padding: 5px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        #buttonChat {
            padding: 6px 10px;
            margin-left: 5px;
            cursor: pointer;
            background-color: #f488ad;
            color: white;
            border: none;
            border-radius: 5px;
        }

        #buttonChat:hover {
            background-color: #e76b95;
        }

        #suggestBox {
            padding: 5px 10px;
            text-align: center;
        }

        #suggestBox button {
            padding: 6px 10px;
            background-color: #f0f0f0;
            border: 1px solid #ddd;
            border-radius: 20px;
            cursor: pointer;
            font-size: 13px;
        }

        #suggestBox button:hover {
            background-color: #ffe3ed;
            color: #d94c82;
        }

        /* Tin nhắn bọc viền */
        .userMessage, .aiMessage {
            background-color: #ffe3ed;
            padding: 8px 12px;
            margin: 6px 0;
            border-radius: 12px;
            max-width: 90%;
            clear: both;
            display: inline-block;
            font-size: 14px;
            line-height: 1.4;
        }

        .userMessage {
            background-color: #fcd5e5;
            align-self: flex-end;
            float: right;
            color: #333;
        }

        .aiMessage {
            background-color: #f6f6f6;
            float: left;
            color: #333;
        }
    </style>

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
