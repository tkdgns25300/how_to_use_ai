/**
 * Notification Toast System
 * Provides consistent toast notifications across the application
 */

class NotificationManager {
    constructor() {
        this.container = null;
        this.init();
    }

    init() {
        // Create notification container if it doesn't exist
        if (!document.querySelector(".notification-container")) {
            this.container = document.createElement("div");
            this.container.className = "notification-container";
            document.body.appendChild(this.container);
        } else {
            this.container = document.querySelector(".notification-container");
        }
    }

    /**
     * Show a success notification
     * @param {string} title - Notification title
     * @param {string} message - Notification message
     * @param {number} duration - Auto-hide duration in milliseconds (default: 5000)
     */
    success(title, message, duration = 5000) {
        this.show("success", "✅", title, message, duration);
    }

    /**
     * Show an error notification
     * @param {string} title - Notification title
     * @param {string} message - Notification message
     * @param {number} duration - Auto-hide duration in milliseconds (default: 7000)
     */
    error(title, message, duration = 7000) {
        this.show("error", "❌", title, message, duration);
    }

    /**
     * Show an info notification
     * @param {string} title - Notification title
     * @param {string} message - Notification message
     * @param {number} duration - Auto-hide duration in milliseconds (default: 5000)
     */
    info(title, message, duration = 5000) {
        this.show("info", "ℹ️", title, message, duration);
    }

    /**
     * Show a notification toast
     * @param {string} type - Notification type (success, error, info)
     * @param {string} icon - Icon emoji
     * @param {string} title - Notification title
     * @param {string} message - Notification message
     * @param {number} duration - Auto-hide duration
     */
    show(type, icon, title, message, duration) {
        const toast = document.createElement("div");
        toast.className = `notification-toast ${type}`;

        toast.innerHTML = `
            <div class="notification-icon">${icon}</div>
            <div class="notification-content">
                <div class="notification-title">${title}</div>
                <div class="notification-message">${message}</div>
            </div>
            <button class="notification-close" onclick="this.parentElement.remove()">×</button>
        `;

        this.container.appendChild(toast);

        // Trigger animation
        setTimeout(() => {
            toast.classList.add("show");
        }, 100);

        // Auto-hide after duration
        if (duration > 0) {
            setTimeout(() => {
                this.hide(toast);
            }, duration);
        }
    }

    /**
     * Hide a specific notification
     * @param {HTMLElement} toast - Toast element to hide
     */
    hide(toast) {
        toast.classList.remove("show");
        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 300);
    }

    /**
     * Hide all notifications
     */
    hideAll() {
        const toasts = this.container.querySelectorAll(".notification-toast");
        toasts.forEach((toast) => this.hide(toast));
    }
}

// Global notification manager instance
const notification = new NotificationManager();

// Export for use in other modules
window.notification = notification;
