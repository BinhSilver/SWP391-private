<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Tạo Phòng</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        form { max-width: 500px; margin: auto; }
        label { display: block; margin: 10px 0 5px; }
        input, select { width: 100%; padding: 8px; margin-bottom: 10px; }
        button { padding: 10px 20px; background-color: #0077cc; color: white; border: none; cursor: pointer; }
        button:hover { background-color: #005fa3; }
        .error { color: red; }
    </style>
    <script>
        function validateForm() {
            const form = document.forms["createRoomForm"];
            const minAge = form["minAge"].value;
            const maxAge = form["maxAge"].value;
            const languageLevel = form["languageLevel"].value;
            const genderPreference = form["genderPreference"].value;

            if (!genderPreference) {
                alert("Vui lòng chọn giới tính mong muốn.");
                return false;
            }
            if (!minAge || minAge < 16) {
                alert("Tuổi tối thiểu phải từ 16 trở lên.");
                return false;
            }
            if (!maxAge || maxAge < 16) {
                alert("Tuổi tối đa phải từ 16 trở lên.");
                return false;
            }
            if (parseInt(minAge) >= parseInt(maxAge)) {
                alert("Tuổi tối đa phải lớn hơn tuổi tối thiểu.");
                return false;
            }
            if (!languageLevel) {
                alert("Vui lòng chọn trình độ tiếng Nhật.");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<% 
    String contextPath = request.getContextPath();
    String error = request.getParameter("error");
    if (error != null) {
%>
    <p class="error">Lỗi: <%= error.equals("missing_parameters") ? "Vui lòng nhập đầy đủ thông tin." :
                            error.equals("invalid_age_range") ? "Độ tuổi không hợp lệ." :
                            error.equals("invalid_language_level") ? "Trình độ tiếng Nhật không hợp lệ." :
                            error.equals("invalid_age_format") ? "Định dạng độ tuổi không đúng." :
                            error.equals("create_failed") ? "Không thể tạo phòng." :
                            "Lỗi máy chủ. Vui lòng thử lại." %></p>
<% } %>
<form name="createRoomForm" action="<%= contextPath %>/createRoom" method="post" onsubmit="return validateForm()">
    <label for="genderPreference">Giới tính mong muốn:</label>
    <select id="genderPreference" name="genderPreference" required>
        <option value="" disabled selected>Chọn giới tính</option>
        <option value="Không xác định">Không xác định</option>
        <option value="Nam">Nam</option>
        <option value="Nữ">Nữ</option>
    </select>

    <label for="minAge">Độ tuổi mong muốn:</label>
    <input type="number" id="minAge" name="minAge" placeholder="Tuổi tối thiểu" required min="16" value="16">
    <input type="number" id="maxAge" name="maxAge" placeholder="Tuổi tối đa" required min="16" value="16">

    <label for="languageLevel">Trình độ tiếng Nhật:</label>
    <select id="languageLevel" name="languageLevel" required>
        <option value="" disabled selected>Chọn trình độ</option>
        <option value="N5">N5</option>
        <option value="N4">N4</option>
        <option value="N3">N3</option>
        <option value="N2">N2</option>
        <option value="N1">N1</option>
        <option value="Chưa xác định">Chưa xác định</option>
    </select>

    <label>
        <input type="checkbox" name="allowApproval" value="true">
        Tôi muốn xét duyệt người tham gia
    </label>

    <button type="submit">Tạo Phòng</button>
</form>
</body>
</html>