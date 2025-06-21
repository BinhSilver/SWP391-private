<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết người dùng</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css">
        <link rel="stylesheet" href="css/usermanagecss.css">
          <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    </head>
    <body class="bg-gray-100">
           <%@ include file="admin/navofadmin.jsp" %>
   
        <div class="container mx-auto p-6">
            <h2 class="text-2xl font-bold mb-4">Chi tiết người dùng</h2>

            <c:if test="${not empty sessionScope.message}">
                <p class="text-green-600 mb-4">${sessionScope.message}</p>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <p class="text-red-600 mb-4">${sessionScope.error}</p>
                <c:remove var="error" scope="session"/>
            </c:if>

            <c:choose>
                <c:when test="${empty user}">
                    <p class="text-red-600 mb-4">Không tìm thấy thông tin người dùng.</p>
                    <a href="${pageContext.request.contextPath}/userManagement" class="text-blue-600">Quay lại Quản lý người dùng</a>
                </c:when>
                <c:otherwise>
                    <div class="bg-white p-6 rounded-lg shadow">
                        <div class="flex items-center mb-6">
                            <img src="${pageContext.request.contextPath}/avatar?userId=${user.userID}" 
                                 alt="Avatar" class="rounded-full mr-4 w-24 h-24 object-cover"
                                 onerror="this.src='https://via.placeholder.com/100';">
                            <div>
                                <h3 class="text-xl font-semibold">${user.fullName}</h3>
                                <p class="text-gray-600">${user.email}</p>
                            </div>
                        </div>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <p><strong>ID người dùng:</strong> ${user.userID}</p>
                                <p><strong>Họ và tên:</strong> ${user.fullName}</p>
                                <p><strong>Email:</strong> ${user.email}</p>
                                <p><strong>Số điện thoại:</strong> ${user.phoneNumber != null ? user.phoneNumber : 'N/A'}</p>
                                <p><strong>Vai trò:</strong> 
                                    <span class="px-2 py-1 rounded 
                                          ${user.roleID == 2 ? 'bg-yellow-100 text-yellow-800' : 
                                            user.roleID == 3 ? 'bg-blue-100 text-blue-800' : 
                                            user.roleID == 4 ? 'bg-purple-100 text-purple-800' : 
                                            'bg-gray-100 text-gray-800'}">
                                        ${user.roleID == 1 ? 'Free' : 
                                          user.roleID == 2 ? 'Premium' : 
                                          user.roleID == 3 ? 'Teacher' : 'Admin'}
                                    </span>
                                </p>
                                <p><strong>Trạng thái:</strong> 
                                    <span class="px-2 py-1 rounded 
                                          ${user.isActive && !user.isLocked ? 'bg-green-100 text-green-800' : 
                                            !user.isActive ? 'bg-red-100 text-red-800' : 
                                            'bg-orange-100 text-orange-800'}">
                                        ${user.isActive && !user.isLocked ? 'Kích hoạt' : !user.isActive ? 'Không hoạt động' : 'Bị khóa'}
                                    </span>
                                </p>
                            </div>
                            <div>
                                <p><strong>Ngày sinh:</strong> 
                                    <c:choose>
                                        <c:when test="${user.birthDate != null}">
                                            <fmt:formatDate value="${user.birthDate}" pattern="dd/MM/yyyy"/>
                                            (Tuổi: ${user.age})
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Trình độ tiếng Nhật:</strong> ${user.japaneseLevel != null ? user.japaneseLevel : 'N/A'}</p>
                                <p><strong>Địa chỉ:</strong> ${user.address != null ? user.address : 'N/A'}</p>
                                <p><strong>Quốc gia:</strong> ${user.country != null ? user.country : 'N/A'}</p>
                                <p><strong>Giới tính:</strong> ${user.gender != null ? user.gender : 'N/A'}</p>
                                <p><strong>Ngày tạo:</strong> 
                                    <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </p>
                            </div>
                        </div>
                        <div class="mt-6 flex space-x-4">
                            <a href="${pageContext.request.contextPath}/userManagement" 
                               class="bg-gray-200 text-gray-800 p-2 rounded hover:bg-gray-300">Quay lại Quản lý người dùng</a>
                            <form action="${pageContext.request.contextPath}/userDetail" method="post" style="display:inline;">
                                <input type="hidden" name="userId" value="${user.userID}">
                                <c:choose>
                                    <c:when test="${user.isLocked}">
                                        <input type="hidden" name="action" value="active">
                                        <button type="submit" 
                                                onclick="return confirm('Bạn có chắc chắn muốn kích hoạt người dùng này?')"
                                                class="bg-green-600 text-white p-2 rounded hover:bg-green-700">
                                            Kích hoạt
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="hidden" name="action" value="block">
                                        <button type="submit" 
                                                onclick="return confirm('Bạn có chắc chắn muốn khóa người dùng này?')"
                                                class="bg-red-600 text-white p-2 rounded hover:bg-red-700">
                                            Khóa
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
                     <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>