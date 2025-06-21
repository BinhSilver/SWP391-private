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

<button id="chatbot-fab"><i class="fa fa-comment"></i></button>
<div id="chatbot-box" style="display:none; position:fixed; bottom:100px; right:32px; z-index:10000;">
    <%@ include file="/chatBoxjsp/chatBox.jsp" %>
</div>

<script src="chat/chatbox.js"></script>
</body>
</html>
