// 메인 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    console.log("Main page loaded");

    // UUID 생성 또는 가져오기 (LocalStorage 사용)
    let uuid = localStorage.getItem("userUuid");
    if (!uuid) {
        uuid = generateUUID();
        localStorage.setItem("userUuid", uuid);
    }
    console.log("User UUID:", uuid);

    // 좋아요 버튼 이벤트 리스너 추가
    initializeLikeButtons();

    // 카드 클릭 이벤트 리스너 추가
    initializeCardClickEvents();

    // 좋아요 버튼 초기화
    function initializeLikeButtons() {
        const likeButtons = document.querySelectorAll(".like-button");
        console.log("Found like buttons:", likeButtons.length);

        likeButtons.forEach((button, index) => {
            console.log(`Initializing like button ${index + 1}:`, {
                cardId: button.getAttribute("data-card-id"),
                userUuid: button.getAttribute("data-uuid"),
                likedUuids: button.getAttribute("data-liked-uuids"),
            });

            // 좋아요 상태 초기화
            initializeLikeState(button);

            // 클릭 이벤트 추가
            button.addEventListener("click", handleLikeClick);
            console.log(`Like button ${index + 1} event listener added`);
        });
    }

    // 좋아요 상태 초기화
    function initializeLikeState(button) {
        const cardId = button.getAttribute("data-card-id");
        const buttonUuid = button.getAttribute("data-uuid");
        const likedUserUuidsStr = button.getAttribute("data-liked-uuids") || "[]";
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
        updateLikeUI(button, isLiked);

        console.log("Like state initialized:", {
            cardId: cardId,
            buttonUuid: buttonUuid,
            userUuid: uuid,
            likedUserUuids: likedUserUuids,
            isLiked: isLiked,
        });
    }

    // 좋아요 클릭 핸들러
    async function handleLikeClick(event) {
        event.preventDefault();
        event.stopPropagation();

        const button = event.currentTarget;
        const cardId = button.getAttribute("data-card-id");
        const isLiked = button.classList.contains("liked");

        console.log("Like button clicked:", {
            cardId: cardId,
            isLiked: isLiked,
            userUuid: uuid,
        });

        try {
            const url = isLiked ? `/api/cards/${cardId}/like?uuid=${uuid}` : `/api/cards/${cardId}/like`;
            const method = isLiked ? "DELETE" : "POST";
            const body = isLiked ? null : JSON.stringify({ uuid: uuid });

            console.log("Making API request:", {
                url: url,
                method: method,
                body: body,
            });

            const response = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: body,
            });

            const result = await response.json();
            console.log("API response:", result);

            if (result.success) {
                // 좋아요 상태 토글
                toggleLikeState(button, !isLiked);
                showMessage(isLiked ? "Like removed!" : "Tip liked successfully!", "success");
            } else {
                showMessage(result.message || "Failed to update like.", "error");
            }
        } catch (error) {
            console.error("Like error:", error);
            showMessage("Server error occurred. Please try again.", "error");
        }
    }

    // 좋아요 상태 토글
    function toggleLikeState(button, isLiked) {
        updateLikeUI(button, isLiked);

        // 좋아요 수 업데이트
        const likeCountElement = button.querySelector(".like-count");
        if (likeCountElement) {
            const currentCount = parseInt(likeCountElement.textContent);
            const newCount = isLiked ? currentCount + 1 : currentCount - 1;
            likeCountElement.textContent = newCount;
        }

        console.log("Like state toggled:", {
            cardId: button.getAttribute("data-card-id"),
            isLiked: isLiked,
        });
    }

    // 좋아요 UI 업데이트
    function updateLikeUI(button, liked) {
        const heartIcon = button.querySelector(".heart-icon span");

        if (liked) {
            heartIcon.textContent = "♥";
            heartIcon.style.color = "#e74c3c";
            button.classList.add("liked");
        } else {
            heartIcon.textContent = "♡";
            heartIcon.style.color = "#666";
            button.classList.remove("liked");
        }

        console.log("Like UI updated:", {
            cardId: button.getAttribute("data-card-id"),
            liked: liked,
            heartText: heartIcon.textContent,
        });
    }

    // 메시지 표시 함수
    function showMessage(message, type) {
        // 기존 메시지 제거
        const existingMessage = document.querySelector(".message");
        if (existingMessage) {
            existingMessage.remove();
        }

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

    // 카드 클릭 이벤트 초기화
    function initializeCardClickEvents() {
        const cards = document.querySelectorAll(".clickable-card");
        console.log("Found clickable cards:", cards.length);

        cards.forEach((card, index) => {
            card.addEventListener("click", handleCardClick);
            console.log(`Card ${index + 1} click event listener added`);
        });
    }

    // 카드 클릭 핸들러
    function handleCardClick(event) {
        // 좋아요 버튼 클릭은 무시
        if (event.target.closest(".like-button")) {
            return;
        }

        const cardId = this.getAttribute("data-card-id");
        if (cardId) {
            console.log("Navigating to card detail:", cardId);
            window.location.href = `/card/${cardId}`;
        }
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
    
    .like-button.liked .heart-icon span {
        color: white !important;
    }
`;
document.head.appendChild(style);
