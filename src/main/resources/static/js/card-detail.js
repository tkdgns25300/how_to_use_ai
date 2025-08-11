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

        button.addEventListener("click", () => handleLikeClick(button, cardId));
    });
}

// 좋아요 클릭 처리
async function handleLikeClick(button, cardId) {
    const uuid = getUuid();
    if (!uuid) {
        alert("Please refresh the page to like this tip.");
        return;
    }

    try {
        const response = await fetch(`/api/cards/${cardId}/like`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ uuid: uuid }),
        });

        if (response.ok) {
            const result = await response.json();
            if (result.success) {
                toggleLikeState(button);
                updateLikeCount(button, result.data.liked);
            }
        } else {
            console.error("Failed to like/unlike");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

// 좋아요 상태 토글
function toggleLikeState(button) {
    const isLiked = button.classList.contains("liked");
    if (isLiked) {
        button.classList.remove("liked");
        button.querySelector(".like-text").textContent = "Like";
    } else {
        button.classList.add("liked");
        button.querySelector(".like-text").textContent = "Liked";
    }
}

// 좋아요 수 업데이트
function updateLikeCount(button, liked) {
    // 좋아요 수는 서버에서 업데이트되므로 페이지 새로고침이 필요할 수 있습니다
    // 실제 구현에서는 서버 응답에서 좋아요 수를 받아와서 업데이트
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
                alert("Tip deleted successfully!");
                window.location.href = "/";
            } else {
                alert("Failed to delete tip: " + (result.message || "Unknown error"));
            }
        } else {
            const errorData = await response.json();
            alert("Failed to delete tip: " + (errorData.message || "Server error"));
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred while deleting the tip.");
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
