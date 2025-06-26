<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css">
        <link rel="stylesheet" href="css/usermanagecss.css">
        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <title>Quản lý người dùng</title>
    </head>
    <body class="bg-gray-100">

        <%@ include file="admin/navofadmin.jsp" %>

        <div class="container mx-auto p-6">
            <h2 class="text-2xl font-bold mb-4">Quản lý người dùng</h2>

            <c:if test="${not empty sessionScope.message}">
                <p class="text-green-600 mb-4">${sessionScope.message}</p>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <p class="text-red-600 mb-4">${sessionScope.error}</p>
                <c:remove var="error" scope="session"/>
            </c:if>
            <div class="bg-white p-4 rounded-lg shadow mb-4 flex justify-between items-center">
                <form action="${pageContext.request.contextPath}/userManagement" method="get" class="flex space-x-4">
                    <div>
                        <input type="text" name="search" value="${searchTerm}" placeholder="Tìm kiếm theo tên hoặc email..."
                               class="border p-2 rounded">
                    </div>
                    <div>
                        <select name="role" class="border p-2 rounded">
                            <option value="All Roles" ${selectedRole == 'All Roles' ? 'selected' : ''}>Tất cả vai trò</option>
                            <option value="1" ${selectedRole == '1' ? 'selected' : ''}>Miễn phí</option>
                            <option value="2" ${selectedRole == '2' ? 'selected' : ''}>Premium</option>
                            <option value="3" ${selectedRole == '3' ? 'selected' : ''}>Giáo viên</option>
                            <option value="4" ${selectedRole == '4' ? 'selected' : ''}>Quản trị viên</option>
                        </select>
                    </div>
                    <div>
                        <select name="status" class="border p-2 rounded">
                            <option value="All Status" ${selectedStatus == 'All Status' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="Active" ${selectedStatus == 'Active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="Inactive" ${selectedStatus == 'Inactive' ? 'selected' : ''}>Không hoạt động</option>
                            <option value="Suspended" ${selectedStatus == 'Suspended' ? 'selected' : ''}>Bị đình chỉ</option>
                        </select>
                    </div>
                    <button type="submit" class="bg-blue-600 text-white p-2 rounded">Lọc</button>
                    <button type="button" onclick="window.location.href = '${pageContext.request.contextPath}/userManagement'"
                            class="bg-gray-200 p-2 rounded">Xóa bộ lọc</button>
                            
                </form>
                   <a href="userstatistic.jsp" class="bg-green-600 text-white p-2 rounded hover:bg-green-700 transition Sta">Thống kê</a>       
            </div>
            <div class="bg-white p-4 rounded-lg shadow">
                <h3 class="text-lg font-semibold mb-4">Danh sách người dùng</h3>
                <c:if test="${empty users}">
                    <p class="text-gray-600 mb-4">Không tìm thấy người dùng nào phù hợp với bộ lọc.</p>
                </c:if>
                <c:if test="${not empty users}">
                    <table class="w-full text-left">
                        <thead>
                            <tr class="bg-gray-100">
                                <th class="p-2">Ảnh đại diện</th>
                                <th class="p-2">Họ và tên</th>
                                <th class="p-2">Email</th>
                                <th class="p-2">Số điện thoại</th>
                                <th class="p-2">Vai trò</th>
                                <th class="p-2">Trạng thái</th>
                                <th class="p-2">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr class="border-t">
                                    <td class="p-2">

                                        <img src="${pageContext.request.contextPath}/avatar?userId=${user.userID}" 
                                             alt="Avatar" class="rounded-full h-12 w-12 object-cover"
                                             onerror="this.onerror=null; this.src='https://via.placeholder.com/60';">

                                    </td>
                                    <td class="p-2">${user.fullName}</td>
                                    <td class="p-2">${user.email}</td>
                                    <td class="p-2">${user.phoneNumber}</td>
                                    <td class="p-2">
                                        <span class="px-2 py-1 rounded 
                                              ${user.roleID == 2 ? 'bg-yellow-100 text-yellow-800' : 
                                                user.roleID == 3 ? 'bg-blue-100 text-blue-800' : 
                                                user.roleID == 4 ? 'bg-purple-100 text-purple-800' : 
                                                'bg-gray-100 text-gray-800'}">
                                                  ${user.roleID == 1 ? 'Miễn phí' : 
                                                    user.roleID == 2 ? 'Premium' : 
                                                    user.roleID == 3 ? 'Giáo viên' : 'Quản trị viên'}
                                              </span>
                                        </td>
                                        <td class="p-2">
                                            <span class="px-2 py-1 rounded ${user.isActive && !user.isLocked ? 'bg-green-100 text-green-800' : 
                                                                             !user.isActive ? 'bg-red-100 text-red-800' : 
                                                                             'bg-orange-100 text-orange-800'}">
                                                      ${user.isActive && !user.isLocked ? 'Hoạt động' : !user.isActive ? 'Không hoạt động' : 'Bị đình chỉ'}
                                                  </span>
                                            </td>
                                            <td class="p-2">
                                                <a href="${pageContext.request.contextPath}/userDetail?userId=${user.userID}" 
                                                   class="view-details-btn mr-2">Xem chi tiết</a>
                                                <form action="${pageContext.request.contextPath}/userManagement" method="post" style="display:inline;">
                                                    <input type="hidden" name="userId" value="${user.userID}">
                                                    <c:choose>
                                                        <c:when test="${user.isLocked}">
                                                            <input type="hidden" name="action" value="active">
                                                            <button type="submit" 
                                                                    onclick="return confirm('Bạn có chắc chắn muốn kích hoạt người dùng này?')"
                                                                    class="text-green-600">
                                                                Mở
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="hidden" name="action" value="block">
                                                            <button type="submit" 
                                                                    onclick="return confirm('Bạn có chắc chắn muốn khóa người dùng này?')"
                                                                    class="text-red-600">
                                                                Khóa
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="mt-4 flex justify-end items-center">
                                <div class="flex space-x-2">
                                    <c:if test="${currentPage > 1}">
                                        <a href="?page=1&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}" 
                                           class="px-3 py-1 bg-gray-200 rounded">«</a>
                                    </c:if>
                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                        <a href="?page=${i}&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}" 
                                           class="px-3 py-1 ${i == currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200'} rounded">${i}</a>
                                    </c:forEach>
                                    <c:if test="${currentPage < totalPages}">
                                        <a href="?page=${totalPages}&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}" 
                                           class="px-3 py-1 bg-gray-200 rounded">»</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
                <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
                <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
            </body>
        </html>
