// 카드 상세 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    const likeButton = document.querySelector(".like-button");
    const editButton = document.querySelector(".edit-btn");

    // UUID 생성 또는 가져오기 (LocalStorage 사용)
    let uuid = localStorage.getItem("userUuid");
    if (!uuid) {
        uuid = generateUUID();
        localStorage.setItem("userUuid", uuid);
    }

    // 권한 확인 및 Edit 버튼 표시/숨김
    if (editButton) {
        const cardUuid = editButton.getAttribute("data-card-uuid");
        console.log("Current user UUID:", uuid);
        console.log("Card author UUID:", cardUuid);

        if (uuid === cardUuid) {
            editButton.style.display = "inline-block";
            console.log("Edit button shown - user is author");
        } else {
            editButton.style.display = "none";
            console.log("Edit button hidden - user is not author");
        }
    }

    // 좋아요 상태 초기화
    initializeLikeState();

    // 좋아요 버튼 이벤트
    if (likeButton) {
        likeButton.addEventListener("click", async function () {
            const cardId = this.getAttribute("data-card-id");
            const isLiked = this.classList.contains("liked");

            try {
                const url = isLiked ? `/api/cards/${cardId}/like?uuid=${uuid}` : `/api/cards/${cardId}/like`;
                const method = isLiked ? "DELETE" : "POST";
                const body = isLiked ? null : JSON.stringify({ uuid: uuid });

                const response = await fetch(url, {
                    method: method,
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: body,
                });

                const result = await response.json();

                if (result.success) {
                    // 좋아요 상태 토글
                    toggleLikeState(!isLiked);
                    showMessage(isLiked ? "Like removed!" : "Tip liked successfully!", "success");
                } else {
                    showMessage(result.message || "Failed to update like.", "error");
                }
            } catch (error) {
                console.error("Like error:", error);
                showMessage("Server error occurred. Please try again.", "error");
            }
        });
    }

    // 좋아요 상태 초기화
    function initializeLikeState() {
        if (!likeButton) return;

        const cardId = likeButton.getAttribute("data-card-id");
        const likedUserUuidsStr = likeButton.getAttribute("data-liked-uuids") || "[]";
        let likedUserUuids = [];

        try {
            if (likedUserUuidsStr && likedUserUuidsStr.trim() !== "") {
                likedUserUuids = likedUserUuidsStr.split(",").filter((uuid) => uuid.trim() !== "");
            }
        } catch (error) {
            console.error("Error parsing liked UUIDs:", error);
            likedUserUuids = [];
        }

        // 현재 사용자가 좋아요를 눌렀는지 확인
        const isLiked = likedUserUuids.includes(uuid);
        updateLikeUI(isLiked);

        console.log("Like state initialized:", {
            cardId: cardId,
            userUuid: uuid,
            likedUserUuids: likedUserUuids,
            isLiked: isLiked,
        });
    }

    // 좋아요 상태 토글
    function toggleLikeState(isLiked) {
        updateLikeUI(isLiked);

        // 좋아요 수 업데이트
        const likeCountElement = document.querySelector(".like-count");
        if (likeCountElement) {
            const currentCount = parseInt(likeCountElement.textContent);
            const newCount = isLiked ? currentCount + 1 : currentCount - 1;
            likeCountElement.textContent = newCount + " likes";
        }
    }

    // 좋아요 UI 업데이트
    function updateLikeUI(liked) {
        const heartIcon = likeButton.querySelector(".heart-icon span");
        const likeText = likeButton.querySelector(".like-text");

        if (liked) {
            heartIcon.textContent = "♥";
            heartIcon.style.color = "#e74c3c";
            likeButton.classList.add("liked");
            if (likeText) likeText.textContent = "Liked";
        } else {
            heartIcon.textContent = "♡";
            heartIcon.style.color = "#666";
            likeButton.classList.remove("liked");
            if (likeText) likeText.textContent = "Like this tip";
        }
    }

    // 메시지 표시 함수
    function showMessage(message, type) {
        const messageDiv = document.createElement("div");
        messageDiv.className = `message message-${type}`;
        messageDiv.textContent = message;

        messageDiv.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 1000;
            animation: slideIn 0.3s ease;
            ${type === "success" ? "background-color: #28a745;" : "background-color: #dc3545;"}
        `;

        document.body.appendChild(messageDiv);

        setTimeout(() => {
            messageDiv.style.animation = "slideOut 0.3s ease";
            setTimeout(() => messageDiv.remove(), 300);
        }, 3000);
    }

    // UUID 생성 함수
    function generateUUID() {
        return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
            const r = (Math.random() * 16) | 0;
            const v = c == "x" ? r : (r & 0x3) | 0x8;
            return v.toString(16);
        });
    }
});

// 애니메이션 CSS 추가
const style = document.createElement("style");
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .like-button.liked {
        background-color: #e74c3c !important;
        border-color: #e74c3c !important;
        color: white !important;
    }
    
    .like-button.liked .like-text {
        color: white !important;
    }
`;
document.head.appendChild(style);
