<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
                    <a class="nav-link px-2" href="<c:url value='HomeServlet'/>">Trang Chủ</a>
                    <a class="nav-link px-2" href="${pageContext.request.contextPath}/introduce.jsp">Giới Thiệu</a>
                    <a class="nav-link px-2" href="CoursesServlet">Khóa Học</a>
                    <a class="nav-link px-2" href="#">Liên Hệ</a>
                    <c:choose>
                        <c:when test="${empty authUser}">
                            <a class="nav-link px-2" href="<c:url value='login'/>">Premium</a>
                        </c:when>
                        <c:otherwise>
                            <a class="nav-link px-2" href="<c:url value='/payment'/>">Premium</a>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${empty authUser}">
                            <a class="nav-link px-2" href="<c:url value='login'/>">FlashCard</a>
                        </c:when>
                        <c:otherwise>
                            <a class="nav-link px-2" href="<c:url value='/flashcard'/>">FlashCard</a>
                        </c:otherwise>
                    </c:choose>
                    <a class="nav-link px-2" href="search.jsp">Tra Cứu</a>
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
                        <a href="<c:url value='login' />" class="btn-wasabii">Đăng Nhập</a>
                        <a href="<c:url value='login?signup=true' />" class="btn-wasabii">Đăng Ký</a>
                    </c:when>
                    <c:otherwise>
                        <div class="dropdown">
                            <a class="btn dropdown-toggle d-flex align-items-center" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                <ion-icon name="person-circle-outline" style="font-size: 24px;"></ion-icon>
                                <span class="ms-2">${authUser.fullName}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                <li>
                                    <a class="dropdown-item" href="<c:url value='profile'/>">Profile</a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<c:url value='chatrealtime.jsp'/>">chat</a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<c:url value='videocall.jsp'/>">Video Call</a>
                                </li>
                                 <li>
                                    <a class="dropdown-item" href="<c:url value='voiceai.jsp'/>">Call With AI</a>
                                </li>
                                <c:if test="${authUser.roleID == 3}">
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='teacher_dashboard'/>">Dashboard</a>
                                    </li>
                                </c:if>
                                <c:if test="${authUser.roleID == 4}">
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='statis.jsp'/>">Admin</a>
                                    </li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="<c:url value='/logout'/>">Đăng Xuất</a>
                                </li>

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
    $(document).ready(function () {
        $('#searchCourseInput').on('input', function () {
            const query = $(this).val().trim();
            const $searchResults = $('#searchResults');
            $searchResults.empty().removeClass('show');

            if (query.length > 0) {
                $.ajax({
                    url: '<c:url value="/SearchCourse" />',
                    type: 'GET',
                    data: {query: query},
                    dataType: 'json',
                    success: function (data) {
                        console.log('Raw search response:', data);
                        if (data && Array.isArray(data) && data.length > 0) {
                            const $ul = $('<ul>').addClass('list-group');
                            $.each(data, function (_, course) {
                                if (course.isHidden === false) {
                                    const courseTitle = course.title ? String(course.title) : 'Không có tiêu đề';
                                    const courseDesc = course.description ? String(course.description) : 'Không có mô tả';
                                    const courseId = course.courseID ? String(course.courseID) : '';

                                    const $li = $('<li>').addClass('list-group-item')
                                            .append($('<h5>').text(courseTitle))
                                            .append($('<p>').text(courseDesc))
                                            .append($('<a>')
                                                    .addClass('btn btn-sm btn-wasabii')
                                                    .attr('href', '${pageContext.request.contextPath}/CourseDetailServlet?id=' + encodeURIComponent(courseId))
                                                    .text('Xem chi tiết'));
                                    $ul.append($li);
                                }
                            });
                            if ($ul.children().length > 0) {
                                $searchResults.append($ul).addClass('show');
                            } else {
                                $searchResults.append('<div class="alert alert-info m-0">Không tìm thấy khóa học nào.</div>').addClass('show');
                            }
                        } else {
                            $searchResults.append('<div class="alert alert-info m-0">Không tìm thấy khóa học nào.</div>').addClass('show');
                        }
                        console.log('Rendered HTML:', $searchResults.html());
                    },
                    error: function (xhr, status, error) {
                        console.error('AJAX error:', status, error, xhr.responseText);
                        $searchResults.append('<div class="alert alert-danger m-0">Đã xảy ra lỗi khi tìm kiếm: ' + error + '</div>').addClass('show');
                    }
                });
            }
        });


    });
</script>