
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chatbot tiếng Nhật N5 với giọng nói</title>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@400;700&display=swap" rel="stylesheet">
    <script src="https://code.responsivevoice.org/responsivevoice.js?key=YC77U5uD"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <meta name="current-user-id" content="${sessionScope.authUser.userID}">
    <meta name="current-username" content="${sessionScope.authUser.fullName}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/voiceAI.css">
</head>
<body>
    <c:if test="${empty sessionScope.authUser}">
        <c:redirect url="/LoginJSP/LoginIndex.jsp"/>
    </c:if>
    <%@ include file="Home/nav.jsp" %>

    <div class="container">
        <h1 class="chat-title">Chatbot với giọng nói</h1>
        <div class="chat-container">
            <div id="chatOutput"></div>
        </div>
        <div class="button-container">
            <button id="toggleButton" onclick="toggleRecognition()" class="control-icon mic-icon">
                <img src="${pageContext.request.contextPath}/image/microphone.png" width="24" height="24" alt="Toggle Mic">
            </button>
        </div>
    </div>

    <script>
        // Kiểm tra hỗ trợ SpeechRecognition
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        if (!SpeechRecognition) {
            console.error('SpeechRecognition not supported');
            alert('Trình duyệt không hỗ trợ nhận diện giọng nói. Vui lòng sử dụng Chrome hoặc Edge.');
        }

        // Khởi tạo SpeechRecognition
        const recognition = new SpeechRecognition();
        recognition.lang = 'ja-JP';
        recognition.interimResults = false;
        recognition.maxAlternatives = 1;

        // Tham chiếu đến các phần tử DOM
        const toggleButton = document.getElementById('toggleButton');
        const chatOutput = document.getElementById('chatOutput');
        let isListening = false;

        // Xử lý kết quả nhận diện giọng nói
        recognition.onresult = async (event) => {
            const transcript = event.results[0][0].transcript;
            console.log('Speech recognized:', transcript);
            appendMessage(transcript, 'user-message');
            await sendToAI(transcript);
        };

        // Xử lý lỗi nhận diện
        recognition.onerror = (event) => {
            console.error('Speech recognition error:', event.error);
            let errorMessage = 'Lỗi nhận diện giọng nói: ';
            switch (event.error) {
                case 'no-speech':
                    errorMessage += 'Không phát hiện giọng nói. Vui lòng thử lại.';
                    break;
                case 'audio-capture':
                    errorMessage += 'Không thể truy cập micro. Vui lòng kiểm tra quyền.';
                    break;
                case 'not-allowed':
                    errorMessage += 'Quyền truy cập micro bị từ chối.';
                    break;
                default:
                    errorMessage += event.error;
            }
            appendMessage(errorMessage, 'error-message');
            toggleRecognition();
        };

        // Khi nhận diện kết thúc
        recognition.onend = () => {
            if (isListening) {
                console.log('Speech recognition ended');
                appendMessage('Đã dừng nhận diện. Nhấn để tiếp tục.', 'error-message');
                toggleButton.classList.remove('voice-icon');
                toggleButton.classList.add('mic-icon');
                isListening = false;
            }
        };

        // Chuyển đổi trạng thái nhận diện
        function toggleRecognition() {
            if (!isListening) {
                console.log('Starting speech recognition');
                appendMessage('Đang nghe...', 'error-message');
                try {
                    recognition.start();
                    toggleButton.classList.remove('mic-icon');
                    toggleButton.classList.add('voice-icon');
                    isListening = true;
                } catch (error) {
                    console.error('Error starting recognition:', error);
                    appendMessage('Lỗi khi bắt đầu nhận diện giọng nói.', 'error-message');
                }
            } else {
                console.log('Stopping speech recognition');
                recognition.stop();
                toggleButton.classList.remove('voice-icon');
                toggleButton.classList.add('mic-icon');
                isListening = false;
            }
        }

        // Hàm thêm tin nhắn vào giao diện
        function appendMessage(message, className) {
            if (!chatOutput) {
                console.error('chatOutput element not found');
                return;
            }
            console.log('Appending message:', message, className);

            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${className}`;

            const wrapper = document.createElement('div');
            wrapper.className = className === 'user-message' ? 'user-message-wrapper'  : className === 'ai-message' ? 'ai-message-wrapper': 'error-message-wrapper';
                                                                
            const content = document.createElement('div');
            content.className = 'message-content';
            content.innerText = message;

            if (className === 'ai-message') {
                const avatarContainer = document.createElement('div');
                avatarContainer.className = 'avatar-container';
                const avatar = document.createElement('img');
                avatar.src = '${pageContext.request.contextPath}/image/ai-avatar.png';
                avatar.alt = 'AI Avatar';
                avatar.className = 'avatar';
                avatarContainer.appendChild(avatar);
                wrapper.appendChild(avatarContainer);
                wrapper.appendChild(content);
                messageDiv.appendChild(wrapper);
            } else if (className === 'user-message') {
                const avatarContainer = document.createElement('div');
                avatarContainer.className = 'avatar-container';
                const avatar = document.createElement('img');
                avatar.src = '${sessionScope.authUser.avatar}' || '${pageContext.request.contextPath}/image/ai-avatar.png';
                avatar.alt = 'User Avatar';
                avatar.className = 'avatar';
                avatarContainer.appendChild(avatar);
                wrapper.appendChild(avatarContainer);
                wrapper.appendChild(content);
                messageDiv.appendChild(wrapper);
            } else {
                // error-message không có avatar
                wrapper.appendChild(content);
                messageDiv.appendChild(wrapper);
            }

            chatOutput.appendChild(messageDiv);
            requestAnimationFrame(() => {
                chatOutput.scrollTop = chatOutput.scrollHeight;
            });
            console.log('Message appended to DOM:', messageDiv.outerHTML);
        }

        // Gửi văn bản đến API
        async function sendToAI(query) {
            console.log('Sending to AI:', query);
            const formData = new FormData();
            formData.append("query", query);
            formData.append("bot_id", "8828357f92f7696d8d1d5220");
            formData.append("conversation_id", "demo-conversation-" + Date.now());
            formData.append("model_name", "gemini-2.5-flash-preview-05-20");
            formData.append("api_key", "AIzaSyAH5Su96L-fRZBAzWH46VD5ICXyf9Jpihs");

            try {
                const response = await fetch('https://ai.ftes.vn/api/ai/rag_agent_template/stream', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NjI5MDQwMjM5M2MzNTg5Y2QwMjEzMSJ9.8Ze_T_XpEWOI3Mi3pS5XgLHXw92YmqDZIsOJtRILvVw'
                    },
                    body: formData
                });

                if (!response.ok) {
                    console.log('API Error:', response.status, response.statusText);
                    if (response.status === 401) {
                        appendMessage('Lỗi: Không được phép truy cập API. Vui lòng kiểm tra token hoặc API key.', 'error-message');
                    } else {
                        appendMessage(`Lỗi API: ${response.status} ${response.statusText}`, 'error-message');
                    }
                    return;
                }

                const reader = response.body?.getReader();
                let finalResponse = '';
                while (true) {
                    const {done, value} = await reader.read();
                    if (done) break;
                    const text = new TextDecoder().decode(value);
                    console.log('Stream data:', text);
                    const lines = text.split('\n').filter(Boolean);
                    for (const line of lines) {
                        try {
                            const data = JSON.parse(line);
                            console.log('Parsed data:', data);
                            if (data.type === 'message') {
                                finalResponse += data.content;
                            } else if (data.type === 'final') {
                                finalResponse = data.content.final_response;
                                handleAIResponse(finalResponse);
                            }
                        } catch (parseError) {
                            console.error('Error parsing stream data:', parseError);
                        }
                    }
                }
            } catch (error) {
                console.error('API Error:', error);
                appendMessage(`Lỗi khi gọi API: ${error.message}`, 'error-message');
            }
        }

        // Xử lý phản hồi từ API
        function handleAIResponse(response) {
            console.log('API Response:', response);
            let cleanedResponse;
            if (response.startsWith('「Có」') || response.startsWith('Có')) {
                cleanedResponse = response.replace(/^(「Có」|Có)\s*/, '').trim();
            } else if (response.startsWith('「Không」') || response.startsWith('Không')) {
                cleanedResponse = 'Câu hỏi không đúng, vui lòng hỏi lại.';
            } else {
                cleanedResponse = response;
            }
            appendMessage(cleanedResponse, 'ai-message');
            if (window.responsiveVoice && responsiveVoice.isPlaying) {
                responsiveVoice.speak(cleanedResponse, 'Japanese Female', {
                    onend: () => console.log('Speech finished')
                });
            } else {
                console.error('ResponsiveVoice not loaded or not supported');
                appendMessage('Lỗi: Không thể phát giọng nói. Vui lòng kiểm tra ResponsiveVoice.', 'error-message');
            }
        }

        // Kiểm tra tin nhắn tĩnh khi tải trang
        window.onload = () => {
            appendMessage('Chào mừng bạn đến với Chatbot tiếng Nhật !', 'ai-message');
        };
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
</body>
</html>
