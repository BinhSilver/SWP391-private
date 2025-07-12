<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("LoginJSP/LoginIndex.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gửi Email Hàng Loạt</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    <script src="https://cdn.ckeditor.com/4.25.1/standard/ckeditor.js"></script> <!-- Nâng cấp lên 4.25.1 LTS -->
    <style>
        .form-container {
            max-width: 800px;
            margin: 0 auto;
        }
        .error-message {
            color: red;
            font-weight: bold;
        }
        .success-message {
            color: green;
            font-weight: bold;
        }
    </style>
</head>
<body class="bg-gray-100">
    <%@ include file="admin/navofadmin.jsp" %>
    <div class="container mx-auto p-6">
        <div class="form-container bg-white shadow-lg rounded-lg p-6">
            <h2 class="text-2xl font-semibold mb-4 text-center">Gửi Email Hàng Loạt</h2>
            
            <c:if test="${not empty error}">
                <p class="error-message text-center">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p class="success-message text-center">${success}</p>
            </c:if>

            <form action="<c:url value='/send-bulk-email-admin'/>" method="post" enctype="multipart/form-data" class="space-y-4">
                <!-- Role Selection -->
                <div>
                    <label class="block text-sm font-medium text-gray-700">Chọn người nhận:</label>
                    <div class="mt-2 space-y-2">
                        <div class="flex items-center">
                            <input type="checkbox" id="sendToAll" name="sendToAll" class="h-4 w-4 text-blue-600">
                            <label for="sendToAll" class="ml-2 text-sm text-gray-900">Tất cả người dùng</label>
                        </div>
                        <div class="flex items-center">
                            <input type="checkbox" id="sendToFree" name="sendToFree" class="h-4 w-4 text-blue-600">
                            <label for="sendToFree" class="ml-2 text-sm text-gray-900">Người dùng Free</label>
                        </div>
                        <div class="flex items-center">
                            <input type="checkbox" id="roleAdmin" name="roles" value="Admin" class="h-4 w-4 text-blue-600">
                            <label for="roleAdmin" class="ml-2 text-sm text-gray-900">Admin</label>
                        </div>
                        <div class="flex items-center">
                            <input type="checkbox" id="roleTeacher" name="roles" value="Teacher" class="h-4 w-4 text-blue-600">
                            <label for="roleTeacher" class="ml-2 text-sm text-gray-900">Teacher</label>
                        </div>
                        <div class="flex items-center">
                            <input type="checkbox" id="roleStudent" name="roles" value="Student" class="h-4 w-4 text-blue-600">
                            <label for="roleStudent" class="ml-2 text-sm text-gray-900">Student</label>
                        </div>
                    </div>
                </div>

                <!-- Subject -->
                <div>
                    <label for="subject" class="block text-sm font-medium text-gray-700">Tiêu đề:</label>
                    <input type="text" id="subject" name="subject" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm p-2" required>
                </div>

                <!-- Content -->
                <div>
                    <label for="content" class="block text-sm font-medium text-gray-700">Nội dung:</label>
                    <textarea id="content" name="content" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm"></textarea>
                </div>

                <!-- File Attachment -->
                <div>
                    <label for="attachment" class="block text-sm font-medium text-gray-700">Tệp đính kèm (tùy chọn):</label>
                    <input type="file" id="attachment" name="attachment" class="mt-1 block w-full">
                </div>

                <!-- Submit Button -->
                <div class="text-center">
                    <button type="submit" class="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600">Gửi Email</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Initialize CKEditor
        CKEDITOR.replace('content', {
            height: 300,
            filebrowserUploadUrl: '/upload', // Cấu hình nếu cần
            removePlugins: 'elementspath',
            toolbar: [
                { name: 'basicstyles', items: ['Bold', 'Italic', 'Underline', 'Strike'] },
                { name: 'paragraph', items: ['NumberedList', 'BulletedList', 'Blockquote'] },
                { name: 'links', items: ['Link', 'Unlink'] },
                { name: 'insert', items: ['Image', 'Table', 'HorizontalRule'] },
                { name: 'styles', items: ['Format', 'Font', 'FontSize'] },
                { name: 'colors', items: ['TextColor', 'BGColor'] },
                { name: 'tools', items: ['Maximize'] }
            ]
        });

        // Xử lý logic checkbox
        document.getElementById('sendToAll').addEventListener('change', function() {
            const checkboxes = document.querySelectorAll('input[name="roles"], #sendToFree');
            checkboxes.forEach(cb => cb.disabled = this.checked);
            if (this.checked) {
                checkboxes.forEach(cb => cb.checked = false);
            }
        });

        document.getElementById('sendToFree').addEventListener('change', function() {
            const roleCheckboxes = document.querySelectorAll('input[name="roles"]');
            roleCheckboxes.forEach(cb => cb.disabled = this.checked);
            if (this.checked) {
                roleCheckboxes.forEach(cb => cb.checked = false);
            }
        });
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
</body>
</html>