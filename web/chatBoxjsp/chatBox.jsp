<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<html>
<head>
    <title>Chatbot Course</title>
    <style>
        #chatBox {
            width: 600px; height: 400px;
            border: 1px solid #ccc;
            overflow-y: auto;
            padding: 10px;
            margin-bottom: 10px;
            background-color: #f9f9f9;
        }
        .user { font-weight: bold; color: #0055cc; margin-top: 5px; }
        .bot { font-style: italic; color: #009900; margin-left: 10px; }
    </style>
</head>
<body>
<h2>💬 Hỏi đáp về khóa học</h2>
<div id="chatBox"></div>
<input type="text" id="userInput" placeholder="Nhập câu hỏi..." style="width:400px;" />
<button onclick="sendMessage()">Gửi</button>

<script src="chat/chatbox.js"></script>
</body>
</html>
