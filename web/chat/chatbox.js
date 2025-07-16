function toggleChat() {
    const chat = document.getElementById("chatContainer");
    const isVisible = chat.style.visibility === "visible";

    if (!isVisible) {
        chat.style.visibility = "visible";
        chat.style.opacity = "1";
        setTimeout(() => {
            document.getElementById("userInput").focus();
        }, 100);
    } else {
        chat.style.visibility = "hidden";
        chat.style.opacity = "0";
    }
}

async function sendMessage() {
    let userInput = document.getElementById("userInput").value.trim();
    if (userInput === "") return;

    let chatBox = document.getElementById("chatBox");
    chatBox.innerHTML += `<div><b>Bạn:</b> ${userInput}</div>`;
    document.getElementById("userInput").value = "";

    try {
        if (!chatbotConfig || !chatbotConfig.apiUrl) {
            chatBox.innerHTML += `<div style="color:red;"><b>Lỗi:</b> Không tìm thấy cấu hình API!</div>`;
            return;
        }

        const formData = new FormData();
        formData.append("query", userInput);
        formData.append("bot_id", chatbotConfig.botId);
        if (conversation_id && conversation_id.trim() !== "") {
            formData.append("conversation_id", conversation_id);
        }
        formData.append("model_name", chatbotConfig.modelName);
        formData.append("api_key", chatbotConfig.apiKey);

        console.log('=== DEBUG REQUEST ===');
        console.log('API URL:', chatbotConfig.apiUrl);
        console.log('Auth Token:', chatbotConfig.authToken);
        console.log('Bot ID:', chatbotConfig.botId);
        console.log('API Key:', chatbotConfig.apiKey);
        console.log('Model Name:', chatbotConfig.modelName);
        console.log('Query:', userInput);
        console.log('====================');

        let response = await fetch(chatbotConfig.apiUrl, {
            method: "POST",
            headers: { "Authorization": "Bearer " + chatbotConfig.authToken },
            body: formData
        });

        if (!response.ok) {
            let text = await response.text();
            throw new Error(`Server error: ${response.status} - ${text}`);
        }

        const reader = response.body.getReader();
        let aiMsg = "";
        let isFirstMessage = true;

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            const text = new TextDecoder().decode(value);
            const lines = text.split("\n").filter(Boolean);

            for (const line of lines) {
                try {
                    const data = JSON.parse(line);
                    if (data.type === "message") {
                        aiMsg += data.content;
                        if (isFirstMessage) {
                            chatBox.innerHTML += `<div><b>AI:</b> <span id="aiResponse">${aiMsg}</span></div>`;
                            isFirstMessage = false;
                        } else {
                            document.getElementById("aiResponse").innerText = aiMsg;
                        }
                    } else if (data.type === "final") {
                        aiMsg = data.content.final_response;
                        if (document.getElementById("aiResponse")) {
                            document.getElementById("aiResponse").innerText = aiMsg;
                        } else {
                            chatBox.innerHTML += `<div><b>AI:</b> ${aiMsg}</div>`;
                        }
                    }
                } catch (parseError) {
                    console.warn('Failed to parse JSON line:', line, parseError);
                }
            }
            chatBox.scrollTop = chatBox.scrollHeight;
        }
    } catch (error) {
        console.error("Lỗi:", error);
        chatBox.innerHTML += `<div style="color:red;"><b>Lỗi:</b> Không thể kết nối AI!</div>`;
    }

    chatBox.scrollTop = chatBox.scrollHeight;
}

function handleKeyPress(event) {
    if (event.key === "Enter") {
        event.preventDefault();
        sendMessage();
    }
}

let chatbotConfig = null;
let conversation_id = "";
let aiMsgDiv = null;
window.attachedFile = null;

async function loadChatbotConfig() {
    try {
        const response = await fetch("/chatbot-config");
        chatbotConfig = await response.json();
        console.log('Chatbot config loaded:', chatbotConfig);
    } catch (error) {
        console.error('Error loading chatbot config:', error);
        chatbotConfig = {
            apiUrl: "https://ai.ftes.vn/api/ai/rag_agent_template/stream",
            apiKey: "AIzaSyAH5Su96L-fRZBAzWH46VD5ICXyf9Jpihs",
            botId: "943bf25b42058b8882474ccb",
            authToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NjI5MDQwMjM5M2MzNTg5Y2QwMjEzMSJ9.8Ze_T_XpEWOI3Mi3pS5XgLHXw92YmqDZIsOJtRILvVw",
            modelName: "gemini-2.5-flash-preview-05-20"
        };
        console.warn('Using fallback chatbot configuration');
    }
}

async function generateFlashcards(lessonId) {
    const fileInput = document.getElementById("chat-file-input");
    const userInput = document.getElementById("chat-input").value.trim();
    if (!userInput && !fileInput.files[0]) {
        alert("Vui lòng nhập văn bản hoặc chọn file PDF/Word!");
        return;
    }

    const chatMessages = document.getElementById("chat-messages");
    let displayMsg = userInput;
    if (fileInput.files[0]) {
        displayMsg += (userInput ? " " : "") + fileInput.files[0].name;
    }
    appendUserMessage(displayMsg);

    try {
        // Sử dụng JSON thay vì FormData để đồng bộ với GenerateVocabularyServlet
        const requestBody = {
            inputText: userInput,
            lessonId: lessonId
        };

        console.log('=== DEBUG FLASHCARD REQUEST ===');
        console.log('Request Body:', JSON.stringify(requestBody));
        console.log('============================');

        const response = await fetch("/generate-vocabulary", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + chatbotConfig.authToken
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, Response: ${errorText}`);
        }

        const data = await response.json();
        if (data.error) {
            appendBotMessage("Lỗi: " + data.error);
            return;
        }

        const flashcards = data.vocabulary; // Đổi từ flashcards thành vocabulary để đồng bộ với servlet
        let flashcardHtml = "<div><b>AI:</b> Flashcards đã được tạo:<br><ul>";
        flashcards.forEach((fc, index) => {
            flashcardHtml += `
                <li>
                    <strong>${fc.word}</strong>: ${fc.meaning} (${fc.reading})<br>
                    Ví dụ: ${fc.example}
                    <input type="hidden" name="lessons[${lessonId}][vocabText][${index}]" value="${fc.word}:${fc.meaning}:${fc.reading}:${fc.example}">
                </li>`;
        });
        flashcardHtml += "</ul></div>";
        appendBotMessage(flashcardHtml);

        // Thêm flashcard vào giao diện lesson tương ứng
        const lessonBlock = document.querySelector(`.lesson-block[data-lesson-index="${lessonId}"] .vocab-entry-container`);
        flashcards.forEach((fc, index) => {
            const inputGroup = document.createElement("div");
            inputGroup.className = "input-group mb-2";
            inputGroup.innerHTML = `
                <input type="text" class="form-control vocab-text" name="lessons[${lessonId}][vocabText][${index}]" value="${fc.word}:${fc.meaning}:${fc.reading}:${fc.example}" readonly />
                <input type="file" class="form-control vocab-image" name="lessons[${lessonId}][vocabImage][${index}]" accept="image/*" />
                <button type="button" class="btn btn-outline-success btn-add-vocab ms-2">+</button>
                <button type="button" class="btn btn-outline-danger btn-remove-vocab ms-1">-</button>
            `;
            lessonBlock.appendChild(inputGroup);
        });

    } catch (error) {
        console.error("Lỗi khi tạo flashcard:", error);
        appendBotMessage("Lỗi: Không thể tạo flashcard! " + error.message);
    }

    chatMessages.scrollTop = chatMessages.scrollHeight;
    fileInput.value = "";
    document.getElementById("chat-input").value = "";
}

async function initChatbot() {
    await loadChatbotConfig();

    const chatForm = document.getElementById("chat-form");
    const chatAttachBtn = document.getElementById("chat-attach-btn");
    const chatFileInput = document.getElementById("chat-file-input");
    const chatBubbleIcon = document.getElementById("chat-bubble-icon");
    const chatBubbleContainer = document.getElementById("chat-bubble-container");
    const closeChatBubble = document.getElementById("close-chat-bubble");

    if (chatForm) {
        chatForm.onsubmit = handleChatSubmit;
    }

    if (chatAttachBtn) {
        chatAttachBtn.addEventListener('click', function() {
            chatFileInput.click();
        });
    }

    if (chatFileInput) {
        chatFileInput.addEventListener('change', handleFileSelect);
    }

    if (chatBubbleIcon && chatBubbleContainer && closeChatBubble) {
        chatBubbleIcon.onclick = () => { 
            chatBubbleContainer.style.display = 'block'; 
            chatBubbleIcon.style.display = 'none'; 
        };
        closeChatBubble.onclick = () => { 
            chatBubbleContainer.style.display = 'none'; 
            chatBubbleIcon.style.display = 'flex'; 
        };
    }

    // Thêm sự kiện cho nút tạo flashcard
    document.querySelectorAll(".btn-generate-flashcards").forEach(btn => {
        btn.addEventListener("click", function() {
            const lessonIndex = this.dataset.lessonIndex;
            document.getElementById("flashcard-lesson-id").value = lessonIndex;
            document.getElementById("chat-bubble-container").style.display = "block";
            document.getElementById("chat-bubble-icon").style.display = "none";
        });
    });

    const flashcardForm = document.getElementById("flashcard-form");
    if (flashcardForm) {
        flashcardForm.onsubmit = async function(e) {
            e.preventDefault();
            const lessonId = document.getElementById("flashcard-lesson-id").value;
            await generateFlashcards(lessonId);
        };
    }

    const oldChat = document.getElementById('chat-container');
    if (oldChat) {
        oldChat.style.display = 'none';
    }
}

function handleChatSubmit(e) {
    e.preventDefault();

    if (!chatbotConfig) {
        console.error('Chatbot config not loaded');
        return;
    }

    const input = document.getElementById("chat-input");
    const fileInput = document.getElementById("chat-file-input");
    const message = input.value.trim();
    let displayMsg = message;

    if (window.attachedFile && window.attachedFile.name) {
        displayMsg += (message ? " " : "") + window.attachedFile.name;
    }

    if (!message && !(window.attachedFile && window.attachedFile.name)) return;

    appendUserMessage(displayMsg);
    input.value = "";
    aiMsgDiv = appendBotMessage("");

    const formData = new FormData();
    formData.append("query", message);
    formData.append("bot_id", chatbotConfig.botId);
    formData.append("conversation_id", conversation_id);
    formData.append("model_name", chatbotConfig.modelName);
    formData.append("api_key", chatbotConfig.apiKey);

    if (window.attachedFile && window.attachedFile.name) {
        formData.append("attachs", window.attachedFile);
    }

    fetch(chatbotConfig.apiUrl, {
        method: "POST",
        headers: { "Authorization": "Bearer " + chatbotConfig.authToken },
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.body.getReader();
    })
    .then(reader => {
        let aiMsg = "";
        const readStream = async () => {
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                const text = new TextDecoder().decode(value);
                const lines = text.split("\n").filter(Boolean);

                for (const line of lines) {
                    try {
                        const data = JSON.parse(line);
                        if (data.type === "message") {
                            aiMsg += data.content;
                            updateBotMessage(aiMsg, aiMsgDiv);
                        } else if (data.type === "final") {
                            aiMsg = data.content.final_response;
                            updateBotMessage(aiMsg, aiMsgDiv);
                        }
                    } catch (parseError) {
                        console.warn('Failed to parse JSON line:', line, parseError);
                    }
                }
            }
        };
        return readStream();
    })
    .catch(error => {
        console.error('Error sending message to chatbot:', error);
        updateBotMessage("Xin lỗi, có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại.", aiMsgDiv);
    })
    .finally(() => {
        window.attachedFile = null;
        fileInput.value = "";
    });
}

function handleFileSelect() {
    const file = this.files[0];
    if (file) {
        window.attachedFile = file;
    } else {
        window.attachedFile = null;
    }
}

function appendUserMessage(msg) {
    const chat = document.getElementById("chat-messages");
    const div = document.createElement("div");
    div.className = "msg-user";
    const span = document.createElement("span");
    span.innerText = msg;
    div.appendChild(span);
    chat.appendChild(div);
    chat.scrollTop = chat.scrollHeight;
}

function appendBotMessage(msg) {
    const chat = document.getElementById("chat-messages");
    const div = document.createElement("div");
    div.className = "msg-bot";
    div.innerHTML = `<span>${msg}</span>`;
    chat.appendChild(div);
    chat.scrollTop = chat.scrollHeight;
    return div;
}

function updateBotMessage(msg, div) {
    if (div) {
        div.querySelector("span").innerText = msg;
        div.parentElement.scrollTop = div.parentElement.scrollHeight;
    }
}

document.addEventListener('DOMContentLoaded', initChatbot);