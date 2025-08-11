// 카드 상세 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    initializeLikeButtons();
    initializeEditDeleteButtons();
});

// 좋아요 버튼 초기화
function initializeLikeButtons() {
    const likeButtons = document.querySelectorAll(".btn-like");
    likeButtons.forEach((button) => {
        const cardId = button.getAttribute("data-card-id");
        const uuid = button.getAttribute("data-uuid");
        const likedUuids = button.getAttribute("data-liked-uuids");

        if (uuid && likedUuids) {
            const uuidList = likedUuids.split(",").filter((u) => u.trim() !== "");
            if (uuidList.includes(uuid)) {
                button.classList.add("liked");
                button.querySelector(".like-text").textContent = "Liked";
            }
        }

        button.addEventListener("click", handleLikeClick);
    });
}

// 좋아요 버튼 클릭 처리
async function handleLikeClick(e) {
    const likeButton = e.currentTarget;
    const cardId = likeButton.getAttribute("data-card-id");
    const likeCountSpan = document.getElementById(`like-count-${cardId}`);

    // 로딩 상태 (애니메이션 등)
    likeButton.disabled = true;

    try {
        const response = await fetch(`/api/cards/${cardId}/like`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ uuid: getUuid() }),
        });

        const result = await response.json();

        if (response.ok && result.success) {
            const likeData = result.data;
            // UI 업데이트
            toggleLikeState(likeButton, likeData.liked);
            updateLikeCount(likeCountSpan, likeData.likesCount);

            notification.info(
                likeData.liked ? "Liked!" : "Unliked",
                likeData.liked ? "You now like this tip." : "You no longer like this tip.",
                2000
            );
        } else {
            notification.error("Error", result.message || "Failed to update like status.", 4000);
        }
    } catch (error) {
        console.error("Like Error:", error);
        notification.error("Network Error", "Could not connect to the server.", 4000);
    } finally {
        likeButton.disabled = false;
    }
}

// 좋아요 버튼 상태 변경
function toggleLikeState(button, isLiked) {
    if (isLiked) {
        button.classList.add("liked");
        button.innerHTML = "<span>❤️ Liked</span>";
    } else {
        button.classList.remove("liked");
        button.innerHTML = "<span>🤍 Like</span>";
    }
}

// 좋아요 수 업데이트
function updateLikeCount(span, count) {
    if (span) {
        span.textContent = count;
    }
}

// 수정/삭제 버튼 초기화
function initializeEditDeleteButtons() {
    const editButton = document.querySelector(".btn-edit");
    const deleteButton = document.querySelector(".btn-delete");

    if (editButton && deleteButton) {
        const cardUuid = editButton.getAttribute("data-card-uuid");
        const userUuid = getUuid();

        if (userUuid && cardUuid === userUuid) {
            editButton.style.display = "inline-block";
            deleteButton.style.display = "inline-block";

            // Delete 버튼에 이벤트 리스너 추가
            deleteButton.addEventListener("click", handleDeleteClick);
        }
    }
}

// Delete 버튼 클릭 처리
function handleDeleteClick() {
    const deleteButton = document.querySelector(".btn-delete");
    const cardId = deleteButton.getAttribute("data-card-id");
    if (confirm("Are you sure you want to delete this tip?")) {
        deleteCard(cardId);
    }
}

// 카드 삭제 기능
async function deleteCard(cardId) {
    try {
        // 로딩 상태 표시
        const deleteButton = document.querySelector(".btn-delete");
        const originalText = deleteButton.textContent;
        deleteButton.disabled = true;
        deleteButton.textContent = "Deleting...";

        const response = await fetch(`/api/cards/${cardId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ uuid: getUuid() }),
        });

        if (response.ok) {
            const result = await response.json();
            if (result.success) {
                // 성공 알람 표시
                notification.success("Tip Deleted!", "Your AI usage tip has been successfully deleted.", 3000);

                // 잠시 후 홈으로 이동
                setTimeout(() => {
                    window.location.href = "/";
                }, 1500);
            } else {
                // 실패 알람 표시
                notification.error(
                    "Delete Failed",
                    result.message || "Failed to delete the tip. Please try again.",
                    5000
                );
            }
        } else {
            const errorData = await response.json();
            // 에러 알람 표시
            notification.error("Delete Failed", errorData.message || "Server error occurred. Please try again.", 5000);
        }
    } catch (error) {
        console.error("Error:", error);
        // 에러 알람 표시
        notification.error(
            "Network Error",
            "An error occurred while deleting the tip. Please check your connection.",
            5000
        );
    } finally {
        // 버튼 상태 복원
        const deleteButton = document.querySelector(".btn-delete");
        deleteButton.disabled = false;
        deleteButton.textContent = "Delete Tip";
    }
}

// UUID 가져오기
function getUuid() {
    let uuid = localStorage.getItem("userUuid");
    if (!uuid) {
        uuid = "uuid-" + Date.now() + "-" + Math.random().toString(36).substr(2, 9);
        localStorage.setItem("userUuid", uuid);
    }
    return uuid;
}

// URL 복사 기능
function copyUrl() {
    const currentUrl = window.location.href;

    if (navigator.clipboard && window.isSecureContext) {
        // Modern clipboard API 사용
        navigator.clipboard
            .writeText(currentUrl)
            .then(() => {
                showCopyMessage("URL copied to clipboard!");
            })
            .catch(() => {
                fallbackCopyTextToClipboard(currentUrl);
            });
    } else {
        // Fallback for older browsers
        fallbackCopyTextToClipboard(currentUrl);
    }
}

// Fallback 복사 방법
function fallbackCopyTextToClipboard(text) {
    const textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position = "fixed";
    textArea.style.left = "-999999px";
    textArea.style.top = "-999999px";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        document.execCommand("copy");
        showCopyMessage("URL copied to clipboard!");
    } catch (err) {
        console.error("Fallback: Oops, unable to copy", err);
        showCopyMessage("Failed to copy URL");
    }

    document.body.removeChild(textArea);
}

// 복사 메시지 표시
function showCopyMessage(message) {
    const messageDiv = document.createElement("div");
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #333;
        color: white;
        padding: 12px 20px;
        border-radius: 5px;
        z-index: 1000;
        font-size: 14px;
    `;

    document.body.appendChild(messageDiv);

    setTimeout(() => {
        messageDiv.remove();
    }, 2000);
}
