let visibleCountCourse = 2;
let allCourses = [];

function searchCourse() {
    const query = document.getElementById("searchInputCourse").value.trim();
    const resultContainer = document.getElementById("courseResults");
    const loadMoreButton = document.getElementById("loadMoreCourse");

    if (query === "") {
        resultContainer.innerHTML = "";
        if (loadMoreButton) loadMoreButton.style.display = "none";
        return;
    }

    fetch(`SearchCourse?query=${encodeURIComponent(query)}`)
        .then(res => {
            if (!res.ok) {
                throw new Error('Network response was not ok');
            }
            return res.json();
        })
        .then(data => {
            allCourses = data;
            visibleCountCourse = 2;
            resultContainer.innerHTML = "";

            if (allCourses.length === 0) {
                resultContainer.innerHTML = "<p class='no-results'>Không tìm thấy khóa học.</p>";
                if (loadMoreButton) loadMoreButton.style.display = "none";
            } else {
                displayCourses();
                if (loadMoreButton) {
                    loadMoreButton.style.display = allCourses.length > visibleCountCourse ? "block" : "none";
                }
            }
        })
        .catch(error => {
            console.error('Error searching courses:', error);
            resultContainer.innerHTML = "<p class='error-message'>Có lỗi xảy ra khi tìm kiếm khóa học.</p>";
            if (loadMoreButton) loadMoreButton.style.display = "none";
        });
}

function displayCourses() {
    const container = document.getElementById("courseResults");
    container.innerHTML = "";

    allCourses.slice(0, visibleCountCourse).forEach(course => {
        // Chỉ hiển thị khóa học không bị ẩn
        if (course.hidden === false) {
            const div = document.createElement("div");
            div.classList.add("search-item");
            
            const courseTitle = course.title || 'Không có tiêu đề';
            const courseDesc = course.description || 'Không có mô tả';
            const courseId = course.courseID || '';
            
            div.innerHTML = `
                <h4>${courseTitle}</h4>
                <p><b>Mô tả:</b> ${courseDesc}</p>
                <a href="course-detail.jsp?courseID=${encodeURIComponent(courseId)}" 
                   class="btn btn-primary btn-sm">
                   <i class="fas fa-eye"></i> Xem chi tiết
                </a>
            `;
            container.appendChild(div);
        }
    });
}

function loadMoreCourse() {
    visibleCountCourse += 2;
    displayCourses();
    const loadMoreButton = document.getElementById("loadMoreCourse");
    if (loadMoreButton && visibleCountCourse >= allCourses.length) {
        loadMoreButton.style.display = "none";
    }
}
