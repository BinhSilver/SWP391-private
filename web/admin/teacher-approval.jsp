<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <title>Xác nhận giáo viên - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="container mt-5">
        <div class="row">
            <div class="col-12">
                <h2 class="mb-4">
                    <i class="fas fa-user-graduate text-primary"></i>
                    Danh sách tài khoản chờ xác nhận giáo viên
                </h2>
                
                <!-- Thông báo -->
                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> ${sessionScope.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% session.removeAttribute("success"); %>
                </c:if>
                
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> ${sessionScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% session.removeAttribute("error"); %>
                </c:if>
                
                <c:choose>
                    <c:when test="${empty pendingTeachers}">
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            Không có tài khoản giáo viên nào đang chờ xác nhận.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="card">
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th><i class="fas fa-user"></i> Email</th>
                                                <th><i class="fas fa-id-card"></i> Họ tên</th>
                                                <th><i class="fas fa-calendar"></i> Ngày đăng ký</th>
                                                <th><i class="fas fa-certificate"></i> Chứng chỉ</th>
                                                <th><i class="fas fa-cogs"></i> Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="user" items="${pendingTeachers}">
                                            <tr>
                                                <td>
                                                    <i class="fas fa-envelope text-muted me-2"></i>
                                                    ${user.email}
                                                </td>
                                                <td>
                                                    <i class="fas fa-user text-muted me-2"></i>
                                                    ${user.fullName}
                                                </td>
                                                <td>
                                                    <i class="fas fa-clock text-muted me-2"></i>
                                                    <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty user.certificatePath}">
                                                            <c:set var="cloudinaryUrl" value="https://res.cloudinary.com/dolxyowej/raw/upload/fl_attachment:certificate.pdf/${user.certificatePath}" />
                                                            <a href="${cloudinaryUrl}"
                                                               class="btn btn-sm btn-outline-primary"
                                                               target="_blank"
                                                               rel="noopener"
                                                               download="certificate.pdf">
                                                                <i class="fas fa-file-pdf"></i> Xem/Tải chứng chỉ
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted"><i class="fas fa-times-circle"></i> Không có</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group" role="group">
                                                        <form method="post" style="display:inline;">
                                                            <input type="hidden" name="action" value="approve"/>
                                                            <input type="hidden" name="userId" value="${user.userID}"/>
                                                            <button type="submit" 
                                                                    class="btn btn-success btn-sm me-1" 
                                                                    onclick="return confirm('Xác nhận tài khoản giáo viên này?')">
                                                                <i class="fas fa-check"></i> Xác nhận
                                                            </button>
                                                        </form>
                                                        
                                                        <form method="post" style="display:inline;">
                                                            <input type="hidden" name="action" value="reject"/>
                                                            <input type="hidden" name="userId" value="${user.userID}"/>
                                                            <button type="submit" 
                                                                    class="btn btn-danger btn-sm" 
                                                                    onclick="return confirm('Từ chối tài khoản giáo viên này?')">
                                                                <i class="fas fa-times"></i> Từ chối
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/statis.jsp" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Quay lại Dashboard
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 