let visibleCountCourse = 2;
let allCourses = [];

function searchCourse() {
    const query = document.getElementById("searchInputCourse").value.trim();
    const resultContainer = document.getElementById("courseResults");
    const loadMoreButton = document.getElementById("loadMoreCourse");

    if (query === "") {
        resultContainer.innerHTML = "";
        loadMoreButton.style.display = "none";
        return;
    }

    fetch(`SearchCourse?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            allCourses = data;
            visibleCountCourse = 2;
            resultContainer.innerHTML = "";

            if (allCourses.length === 0) {
                resultContainer.innerHTML = "<p>Không tìm thấy khóa học.</p>";
                loadMoreButton.style.display = "none";
            } else {
                displayCourses();
                loadMoreButton.style.display = allCourses.length > visibleCountCourse ? "block" : "none";
            }
        });
}

function displayCourses() {
    const container = document.getElementById("courseResults");
    container.innerHTML = "";

    allCourses.slice(0, visibleCountCourse).forEach(course => {
        const div = document.createElement("div");
        div.classList.add("search-item");
        div.innerHTML = `
            <h4>${course.title}</h4>
            <p><b>Mô tả:</b> ${course.description}</p>
            <p><b>Level:</b> ${course.level}</p>
        `;
        container.appendChild(div);
    });
}

function loadMoreCourse() {
    visibleCountCourse += 2;
    displayCourses();
    if (visibleCountCourse >= allCourses.length) {
        document.getElementById("loadMoreCourse").style.display = "none";
    }
}
