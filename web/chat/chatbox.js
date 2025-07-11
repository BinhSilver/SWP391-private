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
        // Sử dụng API động từ config
        if (!chatbotConfig || !chatbotConfig.apiUrl) {
            chatBox.innerHTML += `<div style="color:red;"><b>Lỗi:</b> Không tìm thấy cấu hình API!</div>`;
            return;
        }

        // Tạo FormData đúng format API
        const formData = new FormData();
        formData.append("query", userInput);
        formData.append("bot_id", chatbotConfig.botId);
        if (conversation_id && conversation_id.trim() !== "") {
            formData.append("conversation_id", conversation_id);
        }
        formData.append("model_name", chatbotConfig.modelName);
        formData.append("api_key", chatbotConfig.apiKey);

        // Debug log để kiểm tra request
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

        // Xử lý streaming response
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

// ================= Chatbot configuration and functionality =================
// Chatbot configuration and functionality
let chatbotConfig = null;
let conversation_id = "";
let aiMsgDiv = null;
window.attachedFile = null;

// Load chatbot configuration from server
async function loadChatbotConfig() {
    try {
        const response = await fetch('/SWP_HUY/chatbot-config');
        chatbotConfig = await response.json();
        console.log('Chatbot config loaded:', chatbotConfig);
    } catch (error) {
        console.error('Error loading chatbot config:', error);
        // Fallback to default config if server config fails
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

// Initialize chatbot
async function initChatbot() {
    await loadChatbotConfig();
    
    // Set up event listeners
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
    
    // Hide old chat container if exists
    const oldChat = document.getElementById('chat-container');
    if (oldChat) {
        oldChat.style.display = 'none';
    }
}

// Handle chat form submission
async function handleChatSubmit(e) {
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
    aiMsgDiv = appendBotMessage(""); // Tạo khung bot mới, sẽ update dần
    
    const formData = new FormData();
    formData.append("query", message);
    formData.append("bot_id", chatbotConfig.botId);
    formData.append("conversation_id", conversation_id);
    formData.append("model_name", chatbotConfig.modelName);
    formData.append("api_key", chatbotConfig.apiKey);
    
    if (window.attachedFile && window.attachedFile.name) {
        formData.append("attachs", window.attachedFile);
    }
    
    try {
        const response = await fetch(chatbotConfig.apiUrl, {
            method: "POST",
            headers: { "Authorization": "Bearer " + chatbotConfig.authToken },
            body: formData
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const reader = response.body.getReader();
        let aiMsg = "";
        
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
    } catch (error) {
        console.error('Error sending message to chatbot:', error);
        updateBotMessage("Xin lỗi, có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại.", aiMsgDiv);
    }
    
    // Reset file sau khi gửi
    window.attachedFile = null;
    fileInput.value = "";
}

// Handle file selection
function handleFileSelect() {
    const file = this.files[0];
    if (file) {
        window.attachedFile = file;
    } else {
        window.attachedFile = null;
    }
}

// Append user message to chat
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

// Append bot message to chat
function appendBotMessage(msg) {
    const chat = document.getElementById("chat-messages");
    const div = document.createElement("div");
    div.className = "msg-bot";
    div.innerHTML = `<span>${msg}</span>`;
    chat.appendChild(div);
    chat.scrollTop = chat.scrollHeight;
    return div;
}

// Update bot message
function updateBotMessage(msg, div) {
    if (div) {
        div.querySelector("span").innerText = msg;
        div.parentElement.scrollTop = div.parentElement.scrollHeight;
    }
}

// Initialize chatbot when DOM is loaded
document.addEventListener('DOMContentLoaded', initChatbot);