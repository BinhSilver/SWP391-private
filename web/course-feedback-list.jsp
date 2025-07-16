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
<div class="feedback-section" style="max-width:900px;margin:40px auto 0;display:flex;gap:40px;align-items:flex-start;">
    <div class="feedback-form" style="flex:1;min-width:300px;">
        <h3 style="margin-bottom:16px;">Đánh giá & Feedback khóa học</h3>
        <% if (completed != null && completed == 1) { %>
            <% if (myFeedback == null) { %>
                <form id="feedbackForm" method="post" action="<%= request.getContextPath() %>/course/feedback">
                    <input type="hidden" name="userId" value="<%= currentUserId %>" />
                    <input type="hidden" name="courseId" value="<%= courseId %>" />
                    <input type="hidden" name="redirectUrl" value="CourseDetailServlet?id=<%= courseId %>" />
                    <label style="font-weight:bold;">Số sao:
                        <select name="rating" style="margin:8px 0;">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <option value="<%= i %>"><%= i %> sao</option>
                            <% } %>
                        </select>
                    </label>
                    <br/>
                    <label style="font-weight:bold;">Nội dung:<br/>
                        <textarea name="content" required style="width:100%;height:60px;margin:8px 0;"></textarea>
                    </label>
                    <br/>
                    <button type="submit" style="background:#ff6600;color:#fff;padding:8px 20px;border:none;border-radius:4px;">Gửi feedback</button>
                </form>
            <% } else { %>
                <form id="editFeedbackForm">
                    <input type="hidden" name="feedbackId" value="<%= myFeedback.getFeedbackID() %>" />
                    <input type="hidden" name="userId" value="<%= currentUserId %>" />
                    <label style="font-weight:bold;">Số sao:
                        <select name="rating" style="margin:8px 0;">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <option value="<%= i %>" <%= myFeedback.getRating() == i ? "selected" : "" %>><%= i %> sao</option>
                            <% } %>
                        </select>
                    </label>
                    <br/>
                    <label style="font-weight:bold;">Nội dung:<br/>
                        <textarea name="content" required style="width:100%;height:60px;margin:8px 0;"><%= myFeedback.getContent() %></textarea>
                    </label>
                    <br/>
                    <button type="button" onclick="updateFeedback()" style="background:#ff6600;color:#fff;padding:8px 20px;border:none;border-radius:4px;">Cập nhật</button>
                    <button type="button" onclick="deleteFeedback()" style="background:#ccc;color:#333;padding:8px 20px;border:none;border-radius:4px;margin-left:8px;">Xóa feedback</button>
                </form>
            <% } %>
        <% } else { %>
            <p style="color:#888;">Bạn cần hoàn thành khóa học để gửi feedback.</p>
        <% } %>
    </div>
    <div class="feedback-list" style="flex:2;">
        <h4 style="margin-bottom:16px;">Đánh giá của học viên</h4>
        <% if (feedbacks != null && !feedbacks.isEmpty()) { %>
            <% for (Feedback f : feedbacks) { %>
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
                        <div style="margin:8px 0 4px 0;white-space:pre-line;"> <%= f.getContent() %> </div>
                        <div style="display:flex;align-items:center;gap:12px;">
                            <button class="like-btn" onclick="voteFeedback(<%= f.getFeedbackID() %>, 1)" style="background:none;border:none;color:#ff6600;font-size:1.1em;cursor:pointer;">👍 <span class="like-count"><%= f.getTotalLikes() %></span></button>
                            <button class="dislike-btn" onclick="voteFeedback(<%= f.getFeedbackID() %>, -1)" style="background:none;border:none;color:#888;font-size:1.1em;cursor:pointer;">👎 <span class="dislike-count"><%= f.getTotalDislikes() %></span></button>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } else { %>
            <p style="color:#888;">Chưa có feedback nào cho khóa học này.</p>
        <% } %>
    </div>
</div>
<script src="<%= request.getContextPath() %>/js/feedback.js"></script> 