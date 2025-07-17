<!-- ads.jsp -->
<style>
    .ads-container {
        position: relative; /* ?? chi?m ch? trong flow c?a trang */
        width: 100%;
    }

    .wasabii-ad-left,
    .wasabii-ad-right {
        position: fixed;
        top: 150px; /* ?i?u ch?nh cho ?úng chi?u cao navbar (tùy vào nav.jsp) */
        width: 120px;
        height: calc(100% - 100px);
        z-index: 998;
        background-color: #ffffff;
        text-align: center;
        padding-top: 10px;
        box-shadow: 0 0 6px rgba(0, 0, 0, 0.1);
        height: 550px;
    }

    .wasabii-ad-left {
        left: 0;
    }

    .wasabii-ad-right {
        right: 0;
    }

    @media screen and (max-width: 992px) {
        .wasabii-ad-left, .wasabii-ad-right {
            display: none;
        }
    }
</style>

<div class="ads-container">
    <div class="wasabii-ad-left">
        <a href="https://go.isclix.com/deep_link/v6/6760849022404507866/5979386823886321997?sub4=oneatweb&url_enc=aHR0cHM6Ly9wcm9tby5oaWdobGFuZHNjb2ZmZWUuY29tLnZuL3V1ZGFpNA%3D%3D" target="_blank">
            <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1T4RyVHyOm0PlrdJI1nCFggbGoxlq_sFzbg&s"
                 alt="Highlands Ad"
                 width="120"
                 height="550"
                 style="object-fit: cover; border-radius: 4px;" />
        </a>

    </div>

    <div class="wasabii-ad-right">
        <a href="https://go.isclix.com/deep_link/v6/6760849022404507866/5979386823886321997?sub4=oneatweb&url_enc=aHR0cHM6Ly9wcm9tby5oaWdobGFuZHNjb2ZmZWUuY29tLnZuL3V1ZGFpNA%3D%3D" target="_blank">
            <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1T4RyVHyOm0PlrdJI1nCFggbGoxlq_sFzbg&s"
                 alt="Highlands Ad"
                 width="120"
                 height="550"
                 style="object-fit: cover; border-radius: 4px;" />
        </a>

    </div>
</div>
