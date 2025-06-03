<%-- 
    Document   : CourseJSP
    Created on : Jun 3, 2025, 3:06:46 PM
    Author     : LAPTOP LENOVO
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Wasabii - Khóa Học Tiếng Nhật</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
  <link rel="stylesheet" href="/css/index.css">
</head>
<body>
  <div class="page-wrapper">
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg bg-light py-3">
      <div class="container-fluid">
        <div class="row align-items-center w-100">
          <div class="col-1 d-flex justify-content-evenly">
            <img src="/image/logo.jpg" alt="Wasabii Logo" class="img-fluid" style="max-height: 50px;">
          </div>
          <div class="col-6">
            <div class="nav-links d-flex justify-content-evenly align-items-center h-100">
              <a class="nav-link px-2" href="index.html">Trang Chủ</a>
              <a class="nav-link px-2" href="#">Giới Thiệu</a>
              <a class="nav-link px-2" href="courses.html">Khóa Học</a>
              <a class="nav-link px-2" href="#">Liên Hệ</a>
              <a class="nav-link px-2" href="#">Premium</a>
            </div>
          </div>
          <div class="col-3 d-flex justify-content-end">
            <div class="input-group">
              <span class="input-group-text"><i class="fas fa-search"></i></span>
              <input type="search" class="form-control" placeholder="Tìm kiếm khóa học..." aria-label="Tìm kiếm khóa học">
            </div>
          </div>
          <div class="col-2 d-flex justify-content-end align-items-center gap-2">
            <a class="btn-wasabii">Đăng Nhập</a>
            <a class="btn-wasabii">Đăng Ký</a>
          </div>
        </div>
      </div>
    </nav>

    <!-- Danh sách khóa học -->
    <section class="course-list container">
      <h2>Danh Sách Khóa Học</h2>
      <a href="create-course.html" class="btn-primary">+ Thêm Khóa Học Mới</a>
      <div class="course-grid">
        <div class="course-card">
          <h4>Giao tiếp hàng ngày</h4>
          <p>Khóa học dành cho người mới bắt đầu với các mẫu câu thực tế.</p>
          <div class="course-meta">Sơ cấp - 4 tuần</div>
          <a href="#" class="btn-primary">Xem Chi Tiết</a>
        </div>
        <div class="course-card">
          <h4>Luyện thi JLPT N5</h4>
          <p>Trang bị kiến thức từ vựng, ngữ pháp và kỹ năng làm bài thi.</p>
          <div class="course-meta">Sơ cấp - 6 tuần</div>
          <a href="#" class="btn-primary">Xem Chi Tiết</a>
        </div>
        <div class="course-card">
          <h4>Ngữ pháp nâng cao</h4>
          <p>Nắm chắc các mẫu ngữ pháp thường gặp trong giao tiếp và JLPT.</p>
          <div class="course-meta">Trung cấp - 8 tuần</div>
          <a href="#" class="btn-primary">Xem Chi Tiết</a>
        </div>
        <div class="course-card">
          <h4>Hán tự Kanji</h4>
          <p>Học Kanji hiệu quả qua hình ảnh, câu chuyện và luyện viết.</p>
          <div class="course-meta">Mọi cấp độ</div>
          <a href="#" class="btn-primary">Xem Chi Tiết</a>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div>
          <strong>Về Wasabii</strong>
          <p>Nền tảng học tiếng Nhật dễ tiếp cận, thân thiện và cá nhân hóa cho người Việt.</p>
        </div>
        <div>
          <strong>Liên hệ</strong>
          <p>Email: support@wasabii.vn</p>
          <p>Điện thoại: +84 987 654 321</p>
          <p>Địa chỉ: 123 Đường Nhật Bản, Quận 1, TP.HCM</p>
        </div>
        <div>
          <strong>Kết nối</strong>
          <p><a href="#">Facebook</a> | <a href="#">YouTube</a> | <a href="#">TikTok</a></p>
        </div>
      </div>
      <div class="copyright">© 2025 Wasabii. Tất cả bản quyền được bảo lưu.</div>
    </footer>
  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <script src="/js/cherry-blossom.js"></script>
</body>
</html>
