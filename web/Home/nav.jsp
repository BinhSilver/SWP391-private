<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
.navbar {
    position: relative;
}
.nav-links .nav-link {
    font-size: 1rem;
    color: #333;
    transition: color 0.3s;
}
.nav-links .nav-link:hover {
    color: #28a745;
}
.input-group {
    max-width: 250px;
}
.btn-wasabii {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 0.25rem;
    font-size: 0.9rem;
}
.btn-wasabii:hover {
    background-color: #218838;
}
.search-container {
    position: relative;
}
#searchResults {
    position: absolute;
    top: 100%;
    right: 0;
    width: 300px;
    max-height: 400px;
    overflow-y: auto;
    background: white;
    border: 1px solid #ddd;
    border-radius: 0.25rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    z-index: 1000;
    display: none;
    padding: 0;
}
#searchResults.show {
    display: block;
}
#searchResults .list-group-item {
    border: none;
    padding: 0.75rem;
    display: block; /* Ensure visibility */
}
#searchResults .list-group-item h5 {
    margin: 0;
    font-size: 1rem;
    color: #333; /* Ensure text is visible */
    display: block;
}
#searchResults .list-group-item p {
    margin: 0.25rem 0 0;
    font-size: 0.85rem;
    color: #666;
    display: block;
}
#searchResults .btn-wasabii {
    font-size: 0.8rem;
    padding: 0.25rem 0.5rem;
    margin-top: 0.5rem;
}
.dropdown-menu {
    min-width: 120px;
}
@media (max-width: 992px) {
    .input-group {
        max-width: 200px;
    }
    #searchResults {
        width: 100%;
    }
}
</style>

<nav class="navbar navbar-expand-lg bg-light py-3">
    <div class="container-fluid">
        <div class="row align-items-center w-100">
            <!-- Logo -->
            <div class="col-1 d-flex justify-content-evenly">
                <img src="<c:url value='/image/logo.jpg'/>" alt="Wasabii Logo" class="img-fluid" style="max-height: 50px;">
            </div>

            <!-- Navigation Links -->
            <div class="col-6">
                <div class="nav-links d-flex justify-content-evenly align-items-center h-100">
                    <a class="nav-link px-2" href="<c:url value='/index.jsp'/>">Trang Chủ</a>
                    <a class="nav-link px-2" href="#">Giới Thiệu</a>
                    <c:choose>
                        <c:when test="${authUser.roleID == 1 || authUser.roleID == 2}">
                            <a class="nav-link px-2" href="CoursesServlet">Khóa Học</a>
                        </c:when>
                        <c:when test="${authUser.roleID == 3 || authUser.roleID == 4}">
                            <a class="nav-link px-2" href="teacher_dashboard">Khóa Học</a>
                        </c:when>
                    </c:choose>
                    <a class="nav-link px-2" href="#">Liên Hệ</a>
                    <a class="nav-link px-2" href="<c:url value='/PaymentJSP/Payment.jsp'/>">Premium</a>
                    <a class="nav-link px-2" href="#">FlashCard</a>
                </div>
            </div>

            <!-- Search -->
            <div class="col-3 d-flex justify-content-end search-container">
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-search"></i></span>
                    <input type="search" class="form-control" id="searchCourseInput" placeholder="Tìm kiếm khóa học..." aria-label="Tìm kiếm khóa học">
                </div>
                <div id="searchResults"></div>
            </div>

            <!-- Auth Links -->
            <div class="col-2 d-flex justify-content-end align-items-center gap-2">
                <c:choose>
                    <c:when test="${empty authUser}">
                        <a href="<c:url value='/login' />" class="btn-wasabii">Đăng Nhập</a>
                        <a href="<c:url value='/register' />" class="btn-wasabii">Đăng Ký</a>
                    </c:when>
                    <c:otherwise>
                        <div class="dropdown">
                            <button class="btn btn-wasabii dropdown-toggle d-flex align-items-center gap-2" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                <ion-icon name="person-outline"></ion-icon>
                                <span>${authUser.fullName}</span>
                            </button>
                            <ul class="dropdown-menu" aria-labelledby="userDropdown">
                                <li><a class="dropdown-item" href="<c:url value='/Profile/profile-view.jsp' />">Xem Profile</a></li>
                                <c:if test="${authUser.roleID == 1}">
                                    <li><a class="dropdown-item" href="<c:url value='/statis.jsp' />">Admin</a></li>
                                </c:if>
                                <li><a class="dropdown-item" href="<c:url value='/logout' />">Đăng Xuất</a></li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
    $('#searchCourseInput').on('input', function() {
        var query = $(this).val().trim();
        var $searchResults = $('#searchResults');
        if (query.length > 0) {
            $.ajax({
                url: '<c:url value="/SearchCourse" />',
                type: 'GET',
                data: { query: query },
                dataType: 'json',
                success: function(data) {
                    console.log('Raw search response:', data); // Debug: Log raw JSON response
                    var resultsHtml = '';
                    if (data && Array.isArray(data) && data.length > 0) {
                        resultsHtml = '<ul class="list-group">';
                        $.each(data, function(index, course) {
                            console.log('Course object:', course); // Debug: Log each course object
                            if (course.isHidden === false) { // Strict check for false
                                // Escape HTML to prevent XSS and ensure display
                                var title = course.title ? String(course.title).replace(/</g, '&lt;').replace(/>/g, '&gt;') : 'No title available';
                                var description = course.description ? String(course.description).replace(/</g, '&lt;').replace(/>/g, '&gt;') : 'No description available';
                                resultsHtml += `
                                    <li class="list-group-item">
                                        <h5>${title}</h5>
                                        <p>${description}</p>
                                        <a href="<c:url value='/courseDetails.jsp'/>?courseID=${course.courseID || ''}" class="btn btn-sm btn-wasabii">Xem chi tiết</a>
                                    </li>`;
                            }
                        });
                        resultsHtml += '</ul>';
                        $searchResults.addClass('show');
                    } else {
                        resultsHtml = '<div class="alert alert-info m-0">Không tìm thấy khóa học nào.</div>';
                        $searchResults.addClass('show');
                    }
                    console.log('Rendered HTML:', resultsHtml); // Debug: Log the generated HTML
                    $searchResults.html(resultsHtml);
                },
                error: function(xhr, status, error) {
                    console.error('AJAX error:', status, error, xhr.responseText); // Debug: Log AJAX errors
                    $searchResults.html('<div class="alert alert-danger m-0">Đã xảy ra lỗi khi tìm kiếm: ' + error + '</div>').addClass('show');
                }
            });
        } else {
            $searchResults.empty().removeClass('show');
        }
    });

    // Hide search results when clicking outside
    $(document).on('click', function(e) {
        if (!$(e.target).closest('.search-container').length) {
            $('#searchResults').empty().removeClass('show');
        }
    });
});
</script>