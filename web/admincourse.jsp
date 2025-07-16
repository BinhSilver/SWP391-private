<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css">
        <link rel="stylesheet" href="css/usermanagecss.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <title>Quản lý khóa học</title>
    </head>
    <body class="bg-gray-100">
        <%@ include file="admin/navofadmin.jsp" %>

        <div class="container mx-auto p-6">
            <h2 class="text-2xl font-bold mb-4">Quản lý khóa học</h2>

            <!-- Hiển thị thông báo thành công -->
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>
            <!-- Hiển thị thông báo lỗi -->
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="error" scope="session"/>
            </c:if>

            <div class="bg-white p-4 rounded-lg shadow mb-4 flex justify-between items-center">
                <form action="${pageContext.request.contextPath}/courseManagement" method="get" class="flex space-x-4">
                    <div>
                        <input type="text" name="search" value="${searchTerm}" placeholder="Tìm kiếm theo tiêu đề..."
                               class="border p-2 rounded">
                    </div>
                    <div>
                        <select name="status" class="border p-2 rounded">
                            <option value="All Status" ${selectedStatus == 'All Status' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="Visible" ${selectedStatus == 'Visible' ? 'selected' : ''}>Hiển thị</option>
                            <option value="Hidden" ${selectedStatus == 'Hidden' ? 'selected' : ''}>Ẩn</option>
                        </select>
                    </div>
                    <div>
                        <select name="suggested" class="border p-2 rounded">
                            <option value="All Suggested" ${selectedSuggested == 'All Suggested' ? 'selected' : ''}>Tất cả gợi ý</option>
                            <option value="Suggested" ${selectedSuggested == 'Suggested' ? 'selected' : ''}>Được gợi ý</option>
                            <option value="Not Suggested" ${selectedSuggested == 'Not Suggested' ? 'selected' : ''}>Không gợi ý</option>
                        </select>
                    </div>
                    <button type="submit" class="bg-blue-600 text-white p-2 rounded">Lọc</button>
                    <button type="button" onclick="window.location.href = '${pageContext.request.contextPath}/courseManagement'"
                            class="bg-gray-200 p-2 rounded">Xóa bộ lọc</button>
                </form>
                <a href="coursestatistic.jsp" class="bg-green-600 text-white p-2 rounded hover:bg-green-700 transition Sta">Thống kê</a>
            </div>

            <div class="bg-white p-4 rounded-lg shadow">
                <h3 class="text-lg font-semibold mb-4">Danh sách khóa học</h3>
                <c:if test="${empty courses}">
                    <p class="text-gray-600 mb-4">Không tìm thấy khóa học nào phù hợp với bộ lọc.</p>
                </c:if>
                <c:if test="${not empty courses}">
                    <table class="w-full text-left">
                        <thead>
                            <tr class="bg-gray-100">
                                <th class="p-2">Tiêu đề</th>
                                <th class="p-2">Mô tả</th>
                                <th class="p-2">Đánh giá trung bình</th>
                                <th class="p-2">Trạng thái</th>
                                <th class="p-2">Gợi ý</th>
                                <th class="p-2">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="course" items="${courses}">
                                <tr class="border-t">
                                    <td class="p-2">${course.title}</td>
                                    <td class="p-2">${course.description}</td>
                                    <td class="p-2">
                                        <c:set var="avgRating" value="${courseRatings[course.courseID]}" />
                                        <c:choose>
                                            <c:when test="${not empty avgRating}">
                                                ${avgRating} / 5
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có đánh giá
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="p-2">
                                        <span class="px-2 py-1 rounded ${course.hidden ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}">
                                            ${course.hidden ? 'Ẩn' : 'Hiển thị'}
                                        </span>
                                    </td>
                                    <td class="p-2">
                                        <form action="${pageContext.request.contextPath}/courseManagement" method="post" style="display:inline;">
                                            <input type="hidden" name="courseId" value="${course.courseID}">
                                            <c:choose>
                                                <c:when test="${course.suggested}">
                                                    <input type="hidden" name="action" value="unsuggest">
                                                    <button type="submit" 
                                                            onclick="return confirm('Bạn có chắc chắn muốn bỏ gợi ý khóa học này?')"
                                                            class="px-2 py-1 rounded bg-red-100 text-red-800 hover:bg-red-200">
                                                        Bỏ gợi ý
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="suggest">
                                                    <button type="submit" 
                                                            onclick="return confirm('Bạn có chắc chắn muốn gợi ý khóa học này?')"
                                                            class="px-2 py-1 rounded bg-blue-100 text-blue-800 hover:bg-blue-200">
                                                        Gợi ý
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </td>
                                    <td class="p-2">
                                        <a href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}"
                                           class="view-details-btn mr-2">Xem chi tiết</a>
                                        <form action="${pageContext.request.contextPath}/courseManagement" method="post" style="display:inline;">
                                            <input type="hidden" name="courseId" value="${course.courseID}">
                                            <c:choose>
                                                <c:when test="${course.hidden}">
                                                    <input type="hidden" name="action" value="show">
                                                    <button type="submit" 
                                                            onclick="return confirm('Bạn có chắc chắn muốn hiển thị khóa học này?')"
                                                            class="text-green-600">
                                                        Bật
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="hide">
                                                    <button type="submit" 
                                                            onclick="return confirm('Bạn có chắc chắn muốn ẩn khóa học này?')"
                                                            class="text-red-600">
                                                        Ẩn
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
                                <a href="?page=1&status=${selectedStatus}&suggested=${selectedSuggested}&search=${searchTerm}" 
                                   class="px-3 py-1 bg-gray-200 rounded">«</a>
                            </c:if>
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <a href="?page=${i}&status=${selectedStatus}&suggested=${selectedSuggested}&search=${searchTerm}" 
                                   class="px-3 py-1 ${i == currentPage ? 'bg-blue-600 text-white' : 'bg-gray-200'} rounded">${i}</a>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <a href="?page=${totalPages}&status=${selectedStatus}&suggested=${selectedSuggested}&search=${searchTerm}" 
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