// 카드 수정 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("cardForm");
    const submitBtn = form.querySelector('button[type="submit"]');
    const cardId = document.getElementById("cardId").value;

    // UUID 생성 또는 가져오기 (LocalStorage 사용)
    let uuid = localStorage.getItem("userUuid");
    if (!uuid) {
        uuid = generateUUID();
        localStorage.setItem("userUuid", uuid);
    }

    // 폼 제출 이벤트 (수정)
    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        // 로딩 상태 설정
        setLoadingState(true);

        try {
            const formData = new FormData(form);
            const cardData = {
                title: formData.get("title"),
                categoryId: parseInt(formData.get("categoryId")),
                content: formData.get("content"),
                tags: formData.get("tags") || "",
                situation: formData.get("situation") || "",
                usageExamples: formData.get("usageExamples") || "",
                uuid: uuid,
            };

            const response = await fetch(`/api/cards/${cardId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(cardData),
            });

            const result = await response.json();

            if (result.success) {
                // 성공 알람 표시
                notification.success("Tip Updated!", "Your AI usage tip has been successfully updated.", 3000);

                // 잠시 후 상세 페이지로 이동
                setTimeout(() => {
                    window.location.href = `/card/${cardId}`;
                }, 1500);
            } else {
                // 실패 알람 표시
                notification.error(
                    "Update Failed",
                    result.message || "Failed to update the tip. Please try again.",
                    5000
                );
            }
        } catch (error) {
            console.error("Update error:", error);
            // 에러 알람 표시
            notification.error(
                "Network Error",
                "An error occurred while updating the tip. Please check your connection.",
                5000
            );
        } finally {
            setLoadingState(false);
        }
    });

    // 폼 유효성 검사
    function validateForm() {
        const title = form.querySelector("#title").value.trim();
        const categoryId = form.querySelector("#categoryId").value;
        const content = form.querySelector("#content").value.trim();

        if (!title) {
            notification.error("Validation Error", "Please enter a usage tip title.", 4000);
            form.querySelector("#title").focus();
            return false;
        }

        if (!categoryId) {
            notification.error("Validation Error", "Please select an AI tool category.", 4000);
            form.querySelector("#categoryId").focus();
            return false;
        }

        if (!content) {
            notification.error("Validation Error", "Please enter a usage tip description.", 4000);
            form.querySelector("#content").focus();
            return false;
        }

        return true;
    }

    // 로딩 상태 설정
    function setLoadingState(loading) {
        if (loading) {
            form.classList.add("form-loading");
            submitBtn.textContent = "Updating...";
            submitBtn.disabled = true;
        } else {
            form.classList.remove("form-loading");
            submitBtn.textContent = "Update Tip";
            submitBtn.disabled = false;
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

// 카드 삭제 함수
async function deleteCard() {
    const cardId = document.getElementById("cardId").value;

    if (!confirm("Are you sure you want to delete this tip? This action cannot be undone.")) {
        return;
    }

    try {
        // UUID 가져오기 (LocalStorage 사용)
        let uuid = localStorage.getItem("userUuid");
        if (!uuid) {
            uuid = generateUUID();
            localStorage.setItem("userUuid", uuid);
        }

        const response = await fetch(`/api/cards/${cardId}?uuid=${uuid}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        });

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
            notification.error("Delete Failed", result.message || "Failed to delete the tip. Please try again.", 5000);
        }
    } catch (error) {
        console.error("Delete error:", error);
        // 에러 알람 표시
        notification.error(
            "Network Error",
            "An error occurred while deleting the tip. Please check your connection.",
            5000
        );
    }
}

// 애니메이션 CSS 추가
const style = document.createElement("style");
style.textContent = `
    .btn-danger {
        background-color: #dc3545;
        color: white;
        border: 2px solid #dc3545;
    }
    
    .btn-danger:hover {
        background-color: #c82333;
        border-color: #c82333;
    }
`;
document.head.appendChild(style);
