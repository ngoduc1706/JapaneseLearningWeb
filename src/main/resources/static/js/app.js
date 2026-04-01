/**
 * Shared Application Functions
 * Common utilities used across all pages
 */

/**
 * Logout function - handles user logout via Spring Security
 */
function logout() {
    // Spring Security 6 requires POST for /logout (GET is rejected)
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/logout';
    document.body.appendChild(form);
    form.submit();
}

/**
 * Check if user is authenticated
 */
function checkAuth() {
    return fetch('/api/auth/check', { 
        method: 'POST',
        credentials: 'include'
    })
    .then(res => res.json())
    .catch(err => {
        console.error('Error checking auth:', err);
        return { authenticated: false };
    });
}

/**
 * Show notification/toast message
 */
function showNotification(message, type = 'info', duration = 3000) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#26bba4' : type === 'error' ? '#ff6b6b' : '#3b82f6'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, duration);
}

/**
 * Add animation styles
 */
if (!document.querySelector('style[data-app-animations]')) {
    const style = document.createElement('style');
    style.setAttribute('data-app-animations', 'true');
    style.textContent = `
        @keyframes slideIn {
            from { 
                transform: translateX(100%); 
                opacity: 0;
            }
            to { 
                transform: translateX(0); 
                opacity: 1;
            }
        }
        @keyframes slideOut {
            from { 
                transform: translateX(0); 
                opacity: 1;
            }
            to { 
                transform: translateX(100%); 
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}
