// 카드 등록 페이지 JavaScript
document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM loaded, initializing form..."); // 디버깅용 로그

    const form = document.getElementById("cardForm");

    if (!form) {
        console.error("Form not found");
        return;
    }

    console.log("Form found:", form); // 디버깅용 로그

    const submitBtn = form.querySelector('button[type="submit"]');

    if (!submitBtn) {
        console.error("Submit button not found");
        return;
    }

    console.log("Submit button found:", submitBtn); // 디버깅용 로그

    // UUID 생성 또는 가져오기 (LocalStorage 사용)
    let uuid = localStorage.getItem("userUuid");
    if (!uuid) {
        uuid = generateUUID();
        localStorage.setItem("userUuid", uuid);
    }

    console.log("User UUID:", uuid); // 디버깅용 로그

    // 폼 제출 이벤트
    form.addEventListener("submit", async function (e) {
        console.log("Form submit event triggered"); // 디버깅용 로그
        e.preventDefault(); // 기본 제출 방지
        console.log("Default form submission prevented"); // 디버깅용 로그

        if (!validateForm()) {
            console.log("Form validation failed"); // 디버깅용 로그
            return;
        }

        console.log("Form validation passed, proceeding with submission"); // 디버깅용 로그

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

            console.log("Submitting card data:", cardData); // 디버깅용 로그

            const response = await fetch("/api/cards", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Accept: "application/json",
                },
                body: JSON.stringify(cardData),
            });

            console.log("Response status:", response.status); // 디버깅용 로그

            if (response.ok) {
                const result = await response.json();
                console.log("Response result:", result); // 디버깅용 로그

                if (result.success) {
                    // 성공 알람 표시
                    notification.success("Tip Shared!", "Your AI usage tip has been successfully shared.", 3000);

                    // 잠시 후 홈으로 이동
                    setTimeout(() => {
                        window.location.href = "/";
                    }, 1500);
                } else {
                    // 실패 알람 표시
                    notification.error(
                        "Share Failed",
                        result.message || "Failed to share the tip. Please try again.",
                        5000
                    );
                }
            } else {
                let errorMessage = `Server error: ${response.status}`;
                try {
                    const errorResult = await response.json();
                    if (errorResult.message) {
                        errorMessage = errorResult.message;
                    } else if (errorResult.errorMessage) {
                        errorMessage = errorResult.errorMessage;
                    }
                } catch (parseError) {
                    const errorText = await response.text();
                    errorMessage = `Server error: ${response.status} - ${errorText}`;
                }
                // 에러 알람 표시
                notification.error("Server Error", errorMessage, 5000);
            }
        } catch (error) {
            console.error("Submit error:", error);
            // 에러 알람 표시
            notification.error("Network Error", "Network error occurred. Please try again.", 5000);
        } finally {
            setLoadingState(false);
        }
    });

    console.log("Form submit event listener added"); // 디버깅용 로그

    // 폼 유효성 검사
    function validateForm() {
        const title = form.querySelector("#title").value.trim();
        const categoryId = form.querySelector("#categoryId").value;
        const content = form.querySelector("#content").value.trim();
        const situation = form.querySelector("#situation").value.trim();
        const usageExamples = form.querySelector("#usageExamples").value.trim();

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

        if (!situation) {
            notification.error("Validation Error", "Please explain when to use this tip.", 4000);
            form.querySelector("#situation").focus();
            return false;
        }

        if (!usageExamples) {
            notification.error("Validation Error", "Please provide specific usage examples.", 4000);
            form.querySelector("#usageExamples").focus();
            return false;
        }

        return true;
    }

    // 로딩 상태 설정
    function setLoadingState(loading) {
        if (!submitBtn) return;

        if (loading) {
            form.classList.add("form-loading");
            submitBtn.textContent = "Sharing...";
            submitBtn.disabled = true;
        } else {
            form.classList.remove("form-loading");
            submitBtn.textContent = "Share Tip";
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
