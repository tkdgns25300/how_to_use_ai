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

    // 좋아요 버튼 이벤트
    if (likeButton) {
        likeButton.addEventListener("click", async function () {
            const cardId = this.getAttribute("data-card-id");

            try {
                const response = await fetch(`/api/cards/${cardId}/like`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ uuid: uuid }),
                });

                const result = await response.json();

                if (result.success) {
                    // 좋아요 성공 시 UI 업데이트
                    updateLikeUI(true);
                    showMessage("Tip liked successfully!", "success");
                } else {
                    showMessage(result.message || "Failed to like tip.", "error");
                }
            } catch (error) {
                console.error("Like error:", error);
                showMessage("Server error occurred. Please try again.", "error");
            }
        });
    }

    // 좋아요 UI 업데이트
    function updateLikeUI(liked) {
        const heartIcon = likeButton.querySelector(".heart-icon span");
        if (liked) {
            heartIcon.textContent = "♥";
            heartIcon.style.color = "#e74c3c";
        } else {
            heartIcon.textContent = "♡";
            heartIcon.style.color = "#666";
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

// 공유 기능
function shareTip() {
    const currentUrl = window.location.href;

    if (navigator.share) {
        navigator
            .share({
                title: document.title,
                url: currentUrl,
            })
            .then(() => {
                console.log("Shared successfully");
            })
            .catch((error) => {
                console.log("Error sharing:", error);
                copyToClipboard(currentUrl);
            });
    } else {
        copyToClipboard(currentUrl);
    }
}

function copyToClipboard(text) {
    navigator.clipboard
        .writeText(text)
        .then(() => {
            showShareMessage("Link copied to clipboard!");
        })
        .catch(() => {
            // Fallback for older browsers
            const textArea = document.createElement("textarea");
            textArea.value = text;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand("copy");
            document.body.removeChild(textArea);
            showShareMessage("Link copied to clipboard!");
        });
}

function showShareMessage(message) {
    const messageDiv = document.createElement("div");
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        background-color: #28a745;
        color: white;
        font-weight: 500;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;

    document.body.appendChild(messageDiv);

    setTimeout(() => {
        messageDiv.style.animation = "slideOut 0.3s ease";
        setTimeout(() => messageDiv.remove(), 300);
    }, 2000);
}

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
`;
document.head.appendChild(style);
