async function sendMessage() {
    let userInput = document.getElementById("userInput").value.trim();
    if (userInput === "")
        return;
let fullPrompt = `Bạn là một trợ lý AI dạy tiếng Nhật cho người Việt.

Yêu cầu:
- Chỉ trả lời câu hỏi liên quan đến từ vựng, ngữ pháp hoặc câu mẫu tiếng Nhật.
- Nếu câu hỏi không liên quan đến tiếng Nhật, hãy trả lời: "Xin lỗi, tôi chỉ hỗ trợ câu hỏi liên quan đến tiếng Nhật."
- Trả lời bằng tiếng Việt.
- Trình bày ngắn gọn, rõ ràng bằng gạch đầu dòng.
- Nếu là từ vựng, đưa từ bằng tiếng Nhật (kanji nếu có, kana, romaji) và nghĩa tiếng Việt.
- Không được chèn thêm nội dung không liên quan hoặc lặp lại câu hỏi của người dùng.
- Không dùng dấu ** hoặc ký hiệu markdown nào.
Câu hỏi: ${userInput}
`;

    let requestData = {
        message: fullPrompt
    };

    let chatBox = document.getElementById("chatBox");
    chatBox.innerHTML += `<div><b>Bạn:</b> ${userInput}</div>`;
    document.getElementById("userInput").value = "";

    try {
        let response = await fetch("/SWP_HUY/aiGe", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(requestData)
        });

        let data = await response.json();
        let botResponse = data.response || "Không có phản hồi từ AI.";
        chatBox.innerHTML += `<div><b>AI:</b> ${botResponse}</div>`;
    } catch (error) {
        console.error("Lỗi kết nối AI:", error);
        chatBox.innerHTML += `<div style="color:red;"><b>Lỗi:</b> Không thể kết nối AI!</div>`;
    }

    chatBox.scrollTop = chatBox.scrollHeight;
}

document.getElementById('chatbot-fab').onclick = function() {
    var box = document.getElementById('chatbot-box');
    if (box.style.display === 'none' || box.style.display === '') {
        box.style.display = 'block';
    } else {
        box.style.display = 'none';
    }
};
