// 카드 수정 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('cardForm');
    const submitBtn = form.querySelector('button[type="submit"]');
    const cardId = document.getElementById('cardId').value;
    
    // UUID 생성 (세션에 저장)
    let uuid = sessionStorage.getItem('userUuid');
    if (!uuid) {
        uuid = generateUUID();
        sessionStorage.setItem('userUuid', uuid);
    }
    
    // 폼 제출 이벤트 (수정)
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        // 로딩 상태 설정
        setLoadingState(true);
        
        try {
            const formData = new FormData(form);
            const cardData = {
                title: formData.get('title'),
                categoryId: parseInt(formData.get('categoryId')),
                description: formData.get('description'),
                tags: formData.get('tags') || '',
                situation: formData.get('situation') || '',
                usageExamples: formData.get('usageExamples') || '',
                content: formData.get('content') || '',
                uuid: uuid
            };
            
            const response = await fetch(`/api/cards/${cardId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(cardData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                showSuccessMessage('Tip updated successfully!');
                setTimeout(() => {
                    window.location.href = `/card/${cardId}`;
                }, 2000);
            } else {
                showErrorMessage(result.message || 'Failed to update tip.');
            }
            
        } catch (error) {
            console.error('Update error:', error);
            showErrorMessage('Server error occurred. Please try again.');
        } finally {
            setLoadingState(false);
        }
    });
    
    // 폼 유효성 검사
    function validateForm() {
        const title = form.querySelector('#title').value.trim();
        const categoryId = form.querySelector('#categoryId').value;
        const description = form.querySelector('#description').value.trim();
        
        if (!title) {
            showErrorMessage('Please enter a usage tip title.');
            form.querySelector('#title').focus();
            return false;
        }
        
        if (!categoryId) {
            showErrorMessage('Please select an AI tool category.');
            form.querySelector('#categoryId').focus();
            return false;
        }
        
        if (!description) {
            showErrorMessage('Please enter a brief description.');
            form.querySelector('#description').focus();
            return false;
        }
        
        return true;
    }
    
    // 로딩 상태 설정
    function setLoadingState(loading) {
        if (loading) {
            form.classList.add('form-loading');
            submitBtn.textContent = 'Updating...';
            submitBtn.disabled = true;
        } else {
            form.classList.remove('form-loading');
            submitBtn.textContent = 'Update Tip';
            submitBtn.disabled = false;
        }
    }
    
    // 성공 메시지 표시
    function showSuccessMessage(message) {
        showMessage(message, 'success');
    }
    
    // 에러 메시지 표시
    function showErrorMessage(message) {
        showMessage(message, 'error');
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

// 카드 삭제 함수
async function deleteCard() {
    const cardId = document.getElementById('cardId').value;
    
    if (!confirm('Are you sure you want to delete this tip? This action cannot be undone.')) {
        return;
    }
    
    try {
        // UUID 가져오기
        let uuid = sessionStorage.getItem('userUuid');
        if (!uuid) {
            uuid = generateUUID();
            sessionStorage.setItem('userUuid', uuid);
        }
        
        const response = await fetch(`/api/cards/${cardId}?uuid=${uuid}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        const result = await response.json();
        
        if (result.success) {
            showDeleteMessage('Tip deleted successfully!');
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
        } else {
            showDeleteMessage(result.message || 'Failed to delete tip.', 'error');
        }
        
    } catch (error) {
        console.error('Delete error:', error);
        showDeleteMessage('Server error occurred. Please try again.', 'error');
    }
}

function showDeleteMessage(message, type = 'success') {
    const messageDiv = document.createElement('div');
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
        ${type === 'success' ? 'background-color: #28a745;' : 'background-color: #dc3545;'}
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