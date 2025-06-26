
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>JapanLearn Admin - User Detail</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css">
    <style>
        .status-pill {
            @apply px-2 py-1 rounded-full text-xs font-semibold inline-block mr-2;
        }
        .status-active { @apply bg-green-100 text-green-800; }
        .status-inactive { @apply bg-gray-100 text-gray-800; }
        .status-locked { @apply bg-red-100 text-red-800; }
        .status-unlocked { @apply bg-green-100 text-green-800; }
        .gradient-bg {
            background: linear-gradient(135deg, #6b48ff, #a16eff);
            min-height: 100vh;
        }
    </style>
</head>
<body class="font-sans gradient-bg text-white">
    <nav class="bg-white p-4 shadow text-black">
        <div class="container mx-auto flex justify-between items-center">
            <div class="flex space-x-4">
                <a href="#" class="text-blue-600">Dashboard</a>
                <a href="#" class="text-blue-600">Users</a>
                <a href="#" class="text-blue-600">Courses</a>
                <a href="#" class="text-blue-600">Analytics</a>
            </div>
            <div>
                <img src="https://via.placeholder.com/40" alt="Profile" class="rounded-full">
            </div>
        </div>
    </nav>

    <div class="container mx-auto p-6">
        <c:choose>
            <c:when test="${user == null}">
                <div class="text-center py-10">
                    <div class="inline-block bg-gray-100 p-4 rounded-lg text-black">
                        <p class="text-gray-500">User not found</p>
                        <p class="text-gray-600 mb-4">The requested user could not be found in the system.</p>
                        <a href="${pageContext.request.contextPath}/userManagement" class="bg-blue-600 text-white px-4 py-2 rounded">← Back to Users</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="bg-white p-6 rounded-lg shadow text-black">
                    <div class="flex items-center mb-6">
                        <div class="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center mr-4">
                            <span class="text-gray-600 text-2xl">👤</span>
                        </div>
                        <div>
                            <h2 class="text-2xl font-bold">${user.fullName}</h2>
                            <div class="flex space-x-2 mt-2">
                                <span class="status-pill ${user.isActive ? 'status-active' : 'status-inactive'}">
                                    ${user.isActive ? 'Active' : 'Inactive'}
                                </span>
                                <span class="status-pill ${user.isLocked ? 'status-locked' : 'status-unlocked'}">
                                    ${user.isLocked ? 'Locked' : 'Unlocked'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-6">
                        <div>
                            <h3 class="text-lg font-semibold text-blue-600 mb-4">Personal Information</h3>
                            <p><span class="text-gray-600">📧</span> ${user.email != null ? user.email : 'N/A'}</p>
                            <p><span class="text-gray-600">📞</span> ${user.phoneNumber != null ? user.phoneNumber : 'N/A'}</p>
                            <p><span class="text-gray-600">♂️</span> ${user.gender != null ? user.gender : 'N/A'}</p>
                            <p><span class="text-gray-600">👶</span> ${user.birthDate != null ? user.getAge() : 'N/A'} years old</p>
                        </div>
                        <div>
                            <h3 class="text-lg font-semibold text-blue-600 mb-4">Additional Information</h3>
                            <p><span class="text-gray-600">🎓</span> ${user.japaneseLevel != null ? user.japaneseLevel : 'N/A'}</p>
                            <p><span class="text-gray-600">🌐</span> ${user.country != null ? user.country : 'N/A'}</p>
                            <p><span class="text-gray-600">📍</span> ${user.address != null ? user.address : 'N/A'}</p>
                            <p><span class="text-gray-600">📅</span> ${user.createdAt != null ? user.createdAt : 'N/A'}</p>
                        </div>
                    </div>

                    <div class="mt-6 flex space-x-4 justify-end">
                        <a href="${pageContext.request.contextPath}/userManagement" class="bg-gray-500 text-white px-4 py-2 rounded">← Back to List</a>
                        <a href="${pageContext.request.contextPath}/editUser?userId=${user.userID}" class="bg-blue-600 text-white px-4 py-2 rounded">✏️ Edit User</a>
                        <a href="${pageContext.request.contextPath}/userAction?userId=${user.userID}&action=delete" class="bg-red-600 text-white px-4 py-2 rounded">🗑️ Delete User</a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
```