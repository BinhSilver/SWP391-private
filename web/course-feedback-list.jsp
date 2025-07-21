<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Feedback" %>
<%@ page import="model.User" %>
<%
    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks");
    User authUser = (User) session.getAttribute("authUser");
    Integer currentUserId = (authUser != null) ? authUser.getUserID() : null;
    Integer completed = (Integer) request.getAttribute("completed"); // 1 nếu đã hoàn thành khóa học
    String courseId = request.getParameter("id"); // Lấy id từ URL
    Feedback myFeedback = null;
    if (feedbacks != null && currentUserId != null) {
        for (Feedback f : feedbacks) {
            if (f.getUserID() == currentUserId) {
                myFeedback = f;
                break;
            }
        }
    }
%>
<script>
  window.currentUserId = <%= currentUserId != null ? currentUserId : "null" %>;
  window.contextPath = '<%= request.getContextPath() %>';
</script>
<style>
  .star { color: #ccc; transition: color 0.2s; font-size: 1.5em; cursor: pointer; }
  .star.selected { color: #ffb400 !important; }
</style>
<div class="feedback-section" style="max-width:900px;margin:40px auto 0;display:flex;gap:40px;align-items:flex-start;">
    <div class="feedback-form" style="flex:1;min-width:300px;">
        <h3 style="margin-bottom:16px;">Đánh giá & Feedback khóa học</h3>
        <% if (completed != null && completed == 1 && myFeedback == null) { %>
            <!-- FORM FEEDBACK -->
            <div id="feedbackFormBlock">
                <form id="feedbackForm" method="post" action="<%= request.getContextPath() %>/course/feedback">
                    <input type="hidden" name="userId" value="<%= currentUserId %>" />
                    <input type="hidden" name="courseId" value="<%= courseId %>" />
                    <input type="hidden" name="redirectUrl" value="CourseDetailServlet?id=<%= courseId %>" />
                    <label style="font-weight:bold;">Số sao:
                        <span id="starRating" style="cursor:pointer;font-size:1.5em;">
                            <span class="star" data-value="1">&#9733;</span>
                            <span class="star" data-value="2">&#9733;</span>
                            <span class="star" data-value="3">&#9733;</span>
                            <span class="star" data-value="4">&#9733;</span>
                            <span class="star" data-value="5">&#9733;</span>
                        </span>
                        <input type="hidden" name="rating" id="ratingInput" value="5" />
                    </label>
                    <br/>
                    <label style="font-weight:bold;">Nội dung:<br/>
                        <textarea name="content" required style="width:100%;height:60px;margin:8px 0;"></textarea>
                    </label>
                    <br/>
                    <button type="submit" style="background:#ff6600;color:#fff;padding:8px 20px;border:none;border-radius:4px;">Gửi feedback</button>
                </form>
            </div>
            <script>
            document.addEventListener('DOMContentLoaded', function() {
                var stars = document.querySelectorAll('#starRating .star');
                var ratingInput = document.getElementById('ratingInput');
                var currentRating = parseInt(ratingInput.value) || 5;
                function updateStars(rating) {
                    stars.forEach(function(star, idx) {
                        if (idx < rating) {
                            star.classList.add('selected');
                        } else {
                            star.classList.remove('selected');
                        }
                    });
                }
                stars.forEach(function(star, idx) {
                    star.addEventListener('mouseenter', function() {
                        updateStars(idx+1);
                    });
                    star.addEventListener('mouseleave', function() {
                        updateStars(currentRating);
                    });
                    star.addEventListener('click', function() {
                        currentRating = idx+1;
                        ratingInput.value = currentRating;
                        updateStars(currentRating);
                    });
                });
                updateStars(currentRating);
            });
            </script>
        <% } %>

        <!-- Danh sách feedbacks -->
        <% if (feedbacks != null && !feedbacks.isEmpty()) { %>
            <% for (Feedback f : feedbacks) { %>
                <% boolean isOwner = (authUser != null && authUser.getUserID() == f.getUserID()); %>
                <div class="feedback-item" style="display:flex;align-items:flex-start;gap:16px;padding:16px 0;border-bottom:1px solid #eee;">
                    <img src="<%= request.getContextPath() + "/avatar?userId=" + f.getUserID() %>" class="avatar" width="48" height="48" style="border-radius:50%;object-fit:cover;" />
                    <div style="flex:1;">
                        <div style="display:flex;align-items:center;gap:8px;">
                            <b style="font-size:1.1em;"><%= f.getUserName() %></b>
                            <span class="stars">
                                <% for (int i = 1; i <= 5; i++) { %>
                                    <span style="color:<%= i <= f.getRating() ? "#ffb400" : "#ccc" %>;font-size:1.1em;">&#9733;</span>
                                <% } %>
                            </span>
                            <span style="color:#aaa;font-size:0.95em;margin-left:8px;"><%= f.getCreatedAt() %></span>
                        </div>
                        <% if (isOwner) { %>
                        <div id="feedbackDisplay-<%= f.getFeedbackID() %>">
                            <div style="margin:8px 0 4px 0;white-space:pre-line;"> <%= f.getContent() %> </div>
                            <div style="display:flex;align-items:center;gap:12px;">
                                <button type="button" onclick="showEditFeedback(<%= f.getFeedbackID() %>)" 
                                    style="background:#888;color:#fff;font-size:1.1em;padding:4px 14px;border:none;border-radius:4px;">Sửa</button>
                                <button type="button" onclick="deleteFeedback(<%= f.getFeedbackID() %>);" style="background:none;border:none;color:red;font-size:1.1em;">Xóa</button>
                            </div>
                        </div>
                        <form id="editFeedbackForm-<%= f.getFeedbackID() %>" style="display:none;">
                            <input type="hidden" name="feedbackId" value="<%= f.getFeedbackID() %>" />
                            <input type="hidden" name="userId" value="<%= f.getUserID() %>" />
                            <label style="font-weight:bold;">Số sao:
                                <span id="editStarRating-<%= f.getFeedbackID() %>" style="cursor:pointer;font-size:1.5em;">
                                    <% for (int i = 1; i <= 5; i++) { %>
                                        <span class="star<%= i <= f.getRating() ? " selected" : "" %>" data-value="<%= i %>">&#9733;</span>
                                    <% } %>
                                </span>
                                <input type="hidden" name="rating" id="editRatingInput-<%= f.getFeedbackID() %>" value="<%= f.getRating() %>" />
                            </label>
                            <br/>
                            <label style="font-weight:bold;">Nội dung:<br/>
                                <textarea name="content" required style="width:100%;height:60px;margin:8px 0;"><%= f.getContent() %></textarea>
                            </label>
                            <br/>
                            <button type="button" onclick="updateFeedback(<%= f.getFeedbackID() %>)" style="background:#ff6600;color:#fff;padding:8px 20px;border:none;border-radius:4px;">Cập nhật</button>
                            <button type="button" onclick="cancelEditFeedback(<%= f.getFeedbackID() %>)" style="background:#ccc;color:#333;padding:8px 20px;border:none;border-radius:4px;margin-left:8px;">Hủy</button>
                        </form>
                        <script>
                        function showEditFeedback(feedbackId) {
                            document.querySelectorAll('[id^="editFeedbackForm-"]').forEach(f => f.style.display = 'none');
                            document.querySelectorAll('[id^="feedbackDisplay-"]').forEach(f => f.style.display = '');
                            document.getElementById('editFeedbackForm-' + feedbackId).style.display = '';
                            document.getElementById('feedbackDisplay-' + feedbackId).style.display = 'none';
                        }
                        function cancelEditFeedback(feedbackId) {
                            document.getElementById('editFeedbackForm-' + feedbackId).style.display = 'none';
                            document.getElementById('feedbackDisplay-' + feedbackId).style.display = '';
                        }
                        // Khởi tạo hiệu ứng chọn sao cho form chỉnh sửa
                        (function() {
                            var stars = document.querySelectorAll('#editStarRating-<%= f.getFeedbackID() %> .star');
                            var ratingInput = document.getElementById('editRatingInput-<%= f.getFeedbackID() %>');
                            var currentRating = parseInt(ratingInput.value);
                            function updateStars(rating) {
                                stars.forEach(function(star, idx) {
                                    if (idx < rating) {
                                        star.classList.add('selected');
                                    } else {
                                        star.classList.remove('selected');
                                    }
                                });
                            }
                            stars.forEach(function(star, idx) {
                                star.addEventListener('mouseenter', function() {
                                    updateStars(idx+1);
                                });
                                star.addEventListener('mouseleave', function() {
                                    updateStars(currentRating);
                                });
                                star.addEventListener('click', function() {
                                    currentRating = idx+1;
                                    ratingInput.value = currentRating;
                                    updateStars(currentRating);
                                });
                            });
                            updateStars(currentRating);
                        })();
                        </script>
                        <% } else { %>
                        <div style="margin:8px 0 4px 0;white-space:pre-line;"> <%= f.getContent() %> </div>
                        <div style="display:flex;align-items:center;gap:12px;">
                            <button class="like-btn" onclick="voteFeedback(<%= f.getFeedbackID() %>, 1)" style="background:none;border:none;color:#ff6600;font-size:1.1em;cursor:pointer;">👍 <span class="like-count"><%= f.getTotalLikes() %></span></button>
                            <button class="dislike-btn" onclick="voteFeedback(<%= f.getFeedbackID() %>, -1)" style="background:none;border:none;color:#888;font-size:1.1em;cursor:pointer;">👎 <span class="dislike-count"><%= f.getTotalDislikes() %></span></button>
                        </div>
                        <% } %>
                    </div>
                </div>
            <% } %>
        <% } else { %>
            <p style="color:#888;">Chưa có feedback nào cho khóa học này.</p>
        <% } %>
    </div>
</div>
<script src="<%= request.getContextPath() %>/js/feedback.js"></script> 