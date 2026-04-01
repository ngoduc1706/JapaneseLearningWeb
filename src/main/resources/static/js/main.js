/* ========== Main JavaScript Utilities ========== */

/**
 * API Helper Functions
 */
const API = {
    /**
     * Make a GET request
     */
    get: async function(url) {
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                }
            });
            return await response.json();
        } catch (error) {
            console.error('GET Error:', error);
            return { success: false, message: error.message };
        }
    },

    /**
     * Make a POST request
     */
    post: async function(url, data = {}) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                },
                body: JSON.stringify(data)
            });
            return await response.json();
        } catch (error) {
            console.error('POST Error:', error);
            return { success: false, message: error.message };
        }
    },

    /**
     * Make a PUT request
     */
    put: async function(url, data = {}) {
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                },
                body: JSON.stringify(data)
            });
            return await response.json();
        } catch (error) {
            console.error('PUT Error:', error);
            return { success: false, message: error.message };
        }
    },

    /**
     * Make a DELETE request
     */
    delete: async function(url) {
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.getAuthToken()
                }
            });
            return await response.json();
        } catch (error) {
            console.error('DELETE Error:', error);
            return { success: false, message: error.message };
        }
    },

    /**
     * Get Authorization Token from localStorage
     */
    getAuthToken: function() {
        const token = localStorage.getItem('authToken');
        return token ? `Bearer ${token}` : '';
    }
};

/**
 * DOM Manipulation Helpers
 */
const DOM = {
    /**
     * Get element by ID
     */
    getElementById: function(id) {
        return document.getElementById(id);
    },

    /**
     * Get elements by class
     */
    getElementsByClass: function(className) {
        return document.getElementsByClassName(className);
    },

    /**
     * Show element
     */
    show: function(element) {
        if (typeof element === 'string') {
            element = this.getElementById(element);
        }
        if (element) element.style.display = 'block';
    },

    /**
     * Hide element
     */
    hide: function(element) {
        if (typeof element === 'string') {
            element = this.getElementById(element);
        }
        if (element) element.style.display = 'none';
    },

    /**
     * Add class to element
     */
    addClass: function(element, className) {
        if (typeof element === 'string') {
            element = this.getElementById(element);
        }
        if (element) element.classList.add(className);
    },

    /**
     * Remove class from element
     */
    removeClass: function(element, className) {
        if (typeof element === 'string') {
            element = this.getElementById(element);
        }
        if (element) element.classList.remove(className);
    }
};

/**
 * Notification/Alert Helpers
 */
const Notification = {
    /**
     * Show success message
     */
    success: function(message) {
        this.show('Success', message, 'success');
    },

    /**
     * Show error message
     */
    error: function(message) {
        this.show('Error', message, 'danger');
    },

    /**
     * Show info message
     */
    info: function(message) {
        this.show('Info', message, 'info');
    },

    /**
     * Show warning message
     */
    warning: function(message) {
        this.show('Warning', message, 'warning');
    },

    /**
     * Show toast notification
     */
    show: function(title, message, type = 'info') {
        const toastHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
                <strong>${title}</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', toastHtml);

        // Auto remove after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            if (alerts.length > 0) {
                alerts[alerts.length - 1].remove();
            }
        }, 5000);
    }
};

/**
 * Validation Helpers
 */
const Validator = {
    /**
     * Check if email is valid
     */
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    /**
     * Check if string is empty
     */
    isEmpty: function(str) {
        return !str || str.trim().length === 0;
    },

    /**
     * Check if number is valid
     */
    isValidNumber: function(num) {
        return !isNaN(parseFloat(num)) && isFinite(num);
    }
};

/**
 * Storage Helpers
 */
const Storage = {
    /**
     * Set item in localStorage
     */
    set: function(key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    },

    /**
     * Get item from localStorage
     */
    get: function(key) {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : null;
    },

    /**
     * Remove item from localStorage
     */
    remove: function(key) {
        localStorage.removeItem(key);
    },

    /**
     * Clear all localStorage
     */
    clear: function() {
        localStorage.clear();
    }
};

/**
 * Date Helpers
 */
const DateUtil = {
    /**
     * Format date to readable string
     */
    format: function(date, format = 'YYYY-MM-DD') {
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },

    /**
     * Get time ago string
     */
    timeAgo: function(date) {
        const seconds = Math.floor((new Date() - new Date(date)) / 1000);
        let interval = seconds / 31536000;

        if (interval > 1) return Math.floor(interval) + ' years ago';
        interval = seconds / 2592000;
        if (interval > 1) return Math.floor(interval) + ' months ago';
        interval = seconds / 86400;
        if (interval > 1) return Math.floor(interval) + ' days ago';
        interval = seconds / 3600;
        if (interval > 1) return Math.floor(interval) + ' hours ago';
        interval = seconds / 60;
        if (interval > 1) return Math.floor(interval) + ' minutes ago';
        return Math.floor(seconds) + ' seconds ago';
    }
};

/**
 * Initialize page
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('JavaScript loaded successfully');
});
