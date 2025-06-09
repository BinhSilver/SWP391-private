window.onload = function () {
    fetch('/SWP_HUY/UserStatsServlet')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalUsers').innerText = data.totalUsers.toLocaleString();
            document.getElementById('userGrowthRate').innerText = `${data.userGrowthRate.toFixed(1)}% from last month`;
            document.getElementById('currentMonthPremium').innerText = data.currentMonthPremium.toLocaleString();
            document.getElementById('premiumGrowthRate').innerText = `${data.premiumGrowthRate.toFixed(1)}% from last month`;
        })
        .catch(error => {
            console.error('Lỗi khi lấy dữ liệu:', error);
        });
};
