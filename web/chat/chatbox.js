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

    let requestData = {
        message: userInput
    };

    try {
        let response = await fetch("/SWP_HUY/aiGe", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });

        let data = await response.json();
        let botResponse = data.response || "Không có phản hồi từ AI.";
        chatBox.innerHTML += `<div><b>AI:</b> ${botResponse}</div>`;
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