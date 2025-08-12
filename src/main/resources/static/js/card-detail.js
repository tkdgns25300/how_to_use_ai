// 카드 상세 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    // UUID가 없으면 생성
    if (!localStorage.getItem("userUuid")) {
        const newUuid = "uuid-" + Date.now() + "-" + Math.random().toString(36).substr(2, 9);
        localStorage.setItem("userUuid", newUuid);
        console.log("New UUID created:", newUuid);
    }

    initializeLikeButtons();
    initializeEditDeleteButtons();
});

// 좋아요 버튼 초기화
function initializeLikeButtons() {
    const likeButtons = document.querySelectorAll(".btn-like");
    likeButtons.forEach((button) => {
        const cardId = button.getAttribute("data-card-id");
        const currentUuid = getUuid(); // 현재 사용자의 UUID
        const likedUuids = button.getAttribute("data-liked-uuids");

        // 현재 사용자가 이 카드를 좋아요했는지 확인
        let isLikedByCurrentUser = false;
        if (likedUuids) {
            const uuidList = likedUuids.split(",").filter((u) => u.trim() !== "");
            isLikedByCurrentUser = uuidList.includes(currentUuid);
            console.log(
                `Card ${cardId} - Current user UUID: ${currentUuid}, Liked UUIDs: ${uuidList}, Is liked by current user: ${isLikedByCurrentUser}`
            );
        }

        // 좋아요 상태 설정
        if (isLikedByCurrentUser) {
            button.classList.add("liked");
            const likeText = button.querySelector(".like-text");
            const likeIcon = button.querySelector(".like-icon");
            if (likeText) likeText.textContent = "Liked";
            if (likeIcon) likeIcon.textContent = "❤️";
            console.log(`Button set to liked state for card ${cardId}`);
        } else {
            button.classList.remove("liked");
            const likeText = button.querySelector(".like-text");
            const likeIcon = button.querySelector(".like-icon");
            if (likeText) likeText.textContent = "Like";
            if (likeIcon) likeIcon.textContent = "🤍";
            console.log(`Button set to unliked state for card ${cardId}`);
        }

        // 디버깅을 위한 로그 추가
        console.log(`Card ${cardId} like state:`, {
            buttonClasses: button.className,
            isLiked: button.classList.contains("liked"),
            currentUserUuid: currentUuid,
            likedUuids: likedUuids,
            isLikedByCurrentUser: isLikedByCurrentUser,
        });

        button.addEventListener("click", handleLikeClickWithUuid);
    });
}

// UUID를 URL에 추가하는 함수 (현재는 사용하지 않음)
function addUuidToUrl() {
    // URL 변경으로 인한 문제를 방지하기 위해 임시로 비활성화
    console.log("UUID URL 추가 기능은 현재 비활성화됨");
}

// 좋아요 버튼 클릭 처리 (UUID URL 추가 포함)
async function handleLikeClickWithUuid(e) {
    const button = e.currentTarget;

    // 이미 처리 중인 경우 중복 클릭 방지
    if (button.disabled) {
        console.log("Like request already in progress, ignoring click");
        return;
    }

    // 버튼 비활성화 (중복 클릭 방지)
    button.disabled = true;
    button.style.opacity = "0.6";

    try {
        // UUID를 URL에 추가
        addUuidToUrl();

        // 기존 좋아요 처리 로직 호출
        await handleLikeClick(e);
    } finally {
        // 요청 완료 후 버튼 활성화
        button.disabled = false;
        button.style.opacity = "1";
    }
}

// 좋아요 버튼 클릭 처리
async function handleLikeClick(e) {
    const likeButton = e.currentTarget;
    const cardId = likeButton.getAttribute("data-card-id");
    const likeCountSpan = likeButton.querySelector(".like-count");

    // 로딩 상태는 이미 handleLikeClickWithUuid에서 처리됨

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
    }
}

// 좋아요 버튼 상태 변경
function toggleLikeState(button, isLiked) {
    if (isLiked) {
        button.classList.add("liked");
        const likeText = button.querySelector(".like-text");
        const likeIcon = button.querySelector(".like-icon");
        if (likeText) likeText.textContent = "Liked";
        if (likeIcon) likeIcon.textContent = "❤️";
    } else {
        button.classList.remove("liked");
        const likeText = button.querySelector(".like-text");
        const likeIcon = button.querySelector(".like-icon");
        if (likeText) likeText.textContent = "Like";
        if (likeIcon) likeIcon.textContent = "🤍";
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
    // localStorage에서 UUID 확인 (우선순위 1)
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
