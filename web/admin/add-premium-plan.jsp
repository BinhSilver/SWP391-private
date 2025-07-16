<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Premium Plan</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
</head>
<body>
    <jsp:include page="navofadmin.jsp" />
    <div class="container mt-5">
        <h2>Add New Premium Plan</h2>
        <form action="${pageContext.request.contextPath}/admin/premium-plans" method="post">
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label for="planName">Plan Name</label>
                <input type="text" class="form-control" id="planName" name="planName" required>
            </div>
            <div class="form-group">
                <label for="price">Price</label>
                <input type="number" step="0.01" class="form-control" id="price" name="price" required>
            </div>
            <div class="form-group">
                <label for="duration">Duration (Months)</label>
                <input type="number" class="form-control" id="duration" name="duration" required>
            </div>
            <div class="form-group">
                <label for="description">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3" required></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Save Plan</button>
            <a href="${pageContext.request.contextPath}/admin/premium-plans" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
</body>
</html> 