// 카드 상세 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const likeButton = document.querySelector('.like-button');
    
    // UUID 생성 (세션에 저장)
    let uuid = sessionStorage.getItem('userUuid');
    if (!uuid) {
        uuid = generateUUID();
        sessionStorage.setItem('userUuid', uuid);
    }
    
    // 좋아요 버튼 클릭 이벤트
    if (likeButton) {
        likeButton.addEventListener('click', async function() {
            const cardId = this.getAttribute('data-card-id');
            
            try {
                const response = await fetch(`/api/cards/${cardId}/like?uuid=${uuid}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                });
                
                const result = await response.json();
                
                if (result.success) {
                    // 좋아요 성공 시 UI 업데이트
                    updateLikeUI(true);
                    showMessage('Tip liked successfully!', 'success');
                } else {
                    showMessage(result.message || 'Failed to like tip.', 'error');
                }
                
            } catch (error) {
                console.error('Like error:', error);
                showMessage('Server error occurred. Please try again.', 'error');
            }
        });
    }
    
    // 좋아요 UI 업데이트
    function updateLikeUI(liked) {
        const heartIcon = likeButton.querySelector('.heart-icon span');
        const likeText = likeButton.querySelector('.like-text');
        const likeCount = document.querySelector('.like-count');
        
        if (liked) {
            heartIcon.textContent = '♥';
            heartIcon.style.color = '#e53e3e';
            likeButton.classList.add('liked');
            likeText.textContent = 'Liked';
            
            // 좋아요 수 증가
            const currentCount = parseInt(likeCount.textContent);
            likeCount.textContent = (currentCount + 1) + ' likes';
        }
    }
    
    // 메시지 표시 함수
    function showMessage(message, type) {
        // 기존 메시지 제거
        const existingMessage = document.querySelector('.message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        // 새 메시지 생성
        const messageDiv = document.createElement('div');
        messageDiv.className = `message message-${type}`;
        messageDiv.textContent = message;
        
        // 스타일 적용
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
            ${type === 'success' ? 'background-color: #28a745;' : 'background-color: #dc3545;'}
        `;
        
        document.body.appendChild(messageDiv);
        
        // 3초 후 자동 제거
        setTimeout(() => {
            messageDiv.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => messageDiv.remove(), 300);
        }, 3000);
    }
    
    // UUID 생성 함수
    function generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            const r = Math.random() * 16 | 0;
            const v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
});

// 공유 기능
function shareTip() {
    if (navigator.share) {
        navigator.share({
            title: document.title,
            url: window.location.href
        }).catch(console.error);
    } else {
        // 폴백: URL 복사
        navigator.clipboard.writeText(window.location.href).then(() => {
            showShareMessage('Link copied to clipboard!');
        }).catch(() => {
            showShareMessage('Please copy the URL manually: ' + window.location.href);
        });
    }
}

function showShareMessage(message) {
    const messageDiv = document.createElement('div');
    messageDiv.className = 'share-message';
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background-color: #17a2b8;
        color: white;
        border-radius: 8px;
        font-weight: 500;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => messageDiv.remove(), 300);
    }, 3000);
}

// 애니메이션 CSS 추가
const style = document.createElement('style');
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
        background-color: #e53e3e !important;
        border-color: #e53e3e !important;
        color: white !important;
    }
`;
document.head.appendChild(style); 