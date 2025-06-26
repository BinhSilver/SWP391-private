
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css">
</head>
<body class="bg-gray-100">
    <nav class="bg-white p-4 shadow">
        <div class="container mx-auto flex justify-between items-center">
            <div class="flex space-x-4">
                <a href="index.jsp" class="text-blue-600">Home</a>
                <a href="userstatistic.jsp" class="text-blue-600">Static</a>
            </div>
            <div>
                <img src="https://via.placeholder.com/40" alt="Profile" class="rounded-full">
            </div>
        </div>
    </nav>
    <div class="container mx-auto p-6">
        <h2 class="text-2xl font-bold mb-4">User Management</h2>

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
                    <input type="text" name="search" value="${searchTerm}" placeholder="Search by name or email..."
                           class="border p-2 rounded">
                </div>
                <div>
                    <select name="role" class="border p-2 rounded">
                        <option value="All Roles" ${selectedRole == 'All Roles' ? 'selected' : ''}>All Roles</option>
                        <option value="1" ${selectedRole == '1' ? 'selected' : ''}>Free</option>
                        <option value="2" ${selectedRole == '2' ? 'selected' : ''}>Premium</option>
                        <option value="3" ${selectedRole == '3' ? 'selected' : ''}>Teacher</option>
                    </select>
                </div>
                <div>
                    <select name="status" class="border p-2 rounded">
                        <option value="All Status" ${selectedStatus == 'All Status' ? 'selected' : ''}>All Status</option>
                        <option value="Active" ${selectedStatus == 'Active' ? 'selected' : ''}>Active</option>
                        <option value="Inactive" ${selectedStatus == 'Inactive' ? 'selected' : ''}>Inactive</option>
                        <option value="Suspended" ${selectedStatus == 'Suspended' ? 'selected' : ''}>Suspended</option>
                    </select>
                </div>
                <button type="submit" class="bg-blue-600 text-white p-2 rounded">Filter</button>
                <button type="button" onclick="window.location.href = '${pageContext.request.contextPath}/userManagement'"
                        class="bg-gray-200 p-2 rounded">Clear</button>
            </form>
        </div>
        <div class="bg-white p-4 rounded-lg shadow">
            <h3 class="text-lg font-semibold mb-4">Users List</h3>
            <c:if test="${empty users}">
                <p class="text-gray-600 mb-4">Không tìm thấy người dùng nào phù hợp với bộ lọc.</p>
            </c:if>
            <c:if test="${not empty users}">
                <table class="w-full text-left">
                    <thead>
                        <tr class="bg-gray-100">
                            <th class="p-2">User</th>
                            <th class="p-2">Email</th>
                            <th class="p-2">Phone Number</th>
                            <th class="p-2">Role</th>
                            <th class="p-2">Status</th>
                            <th class="p-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr class="border-t">
                                <td class="p-2">
                                    <img src="${user.avatar != null ? user.avatar : 'https://via.placeholder.com/40'}" 
                                         alt="Avatar" class="rounded-full mr-2 inline-block">
                                    ${user.fullName} <br> ID: #${user.userID}
                                </td>
                                <td class="p-2">${user.email}</td>
                                <td class="p-2">${user.phoneNumber}</td>
                                <td class="p-2">
                                    <span class="px-2 py-1 rounded ${user.roleID == 2 ? 'bg-yellow-100 text-yellow-800' : user.roleID == 3 ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800'}">
                                        ${user.roleID == 1 ? 'Free' : user.roleID == 2 ? 'Premium' : 'Teacher'}
                                    </span>
                                </td>
                                <td class="p-2">
                                    <span class="px-2 py-1 rounded ${user.isActive && !user.isLocked ? 'bg-green-100 text-green-800' : !user.isActive ? 'bg-red-100 text-red-800' : 'bg-orange-100 text-orange-800'}">
                                        ${user.isActive && !user.isLocked ? 'Active' : !user.isActive ? 'Inactive' : 'Suspended'}
                                    </span>
                                </td>
                                <td class="p-2">
                                    <a href="${pageContext.request.contextPath}/userDetail?userId=${user.userID}" 
                                       class="text-blue-600 mr-2">View Details</a>
                                    <form action="${pageContext.request.contextPath}/userManagement" method="post" style="display:inline;">
                                        <input type="hidden" name="userId" value="${user.userID}">
                                        <c:choose>
                                            <c:when test="${user.isLocked}">
                                                <input type="hidden" name="action" value="active">
                                                <button type="submit" 
                                                        onclick="return confirm('Bạn có chắc chắn muốn kích hoạt người dùng này?')"
                                                        class="text-green-600">
                                                    Active
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="action" value="block">
                                                <button type="submit" 
                                                        onclick="return confirm('Bạn có chắc chắn muốn khóa người dùng này?')"
                                                        class="text-red-600">
                                                    Block
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="mt-4 flex justify-between items-center">
                    <form action="${pageContext.request.contextPath}/userManagement" method="get">
                        <input type="hidden" name="search" value="${searchTerm}">
                        <input type="hidden" name="role" value="${selectedRole}">
                        <input type="hidden" name="status" value="${selectedStatus}">
                        <select name="rowsPerPage" class="border p-2 rounded" onchange="this.form.submit()">
                            <c:forEach var="size" items="${[10, 20, 50]}">
                                <c:if test="${size <= totalUsers}">
                                    <option value="${size}" ${pageSize == size ? 'selected' : ''}>${size}</option>
                                </c:if>
                            </c:forEach>
                            <c:if test="${totalUsers > 0 && totalUsers < 10}">
                                <option value="${totalUsers}" ${pageSize == totalUsers ? 'selected' : ''}>${totalUsers}</option>
                            </c:if>
                        </select>
                    </form>
                    <div class="flex space-x-2">
                        <c:if test="${currentPage > 1}">
                            <a href="?page=1&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}&rowsPerPage=${pageSize}" 
                               class="px-3 py-1 bg-gray-200 rounded">«</a>
                        </c:if>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="?page=${i}&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}&rowsPerPage=${pageSize}" 
                               class="px-3 py-1 ${i == currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200'} rounded">${i}</a>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${totalPages}&role=${selectedRole}&status=${selectedStatus}&search=${searchTerm}&rowsPerPage=${pageSize}" 
                               class="px-3 py-1 bg-gray-200 rounded">»</a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
