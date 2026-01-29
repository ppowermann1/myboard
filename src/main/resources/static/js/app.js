// API Base URL
const API_BASE = '/api';

// Auth Functions
async function signup(username, password, nickname, email, phoneNumber) {
    try {
        const response = await fetch(`${API_BASE}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password, nickname, email, phoneNumber }),
        });
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Signup error:', error);
        return { success: false, message: '회원가입 중 오류가 발생했습니다' };
    }
}

async function login(username, password) {
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({ username, password }),
        });
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Login error:', error);
        return { success: false, message: '로그인 중 오류가 발생했습니다' };
    }
}

async function getCurrentUser() {
    try {
        const response = await fetch(`${API_BASE}/auth/me`, {
            credentials: 'include'
        });
        if (response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Get current user error:', error);
        return null;
    }
}

async function logout() {
    try {
        await fetch(`${API_BASE}/auth/logout`, {
            method: 'POST',
        });
        window.location.href = '/login.html';
    } catch (error) {
        console.error('Logout error:', error);
    }
}

// Post Functions
async function getAllPosts() {
    try {
        const response = await fetch(`${API_BASE}/posts`);
        return await response.json();
    } catch (error) {
        console.error('Get posts error:', error);
        return [];
    }
}

async function getPost(id) {
    try {
        const response = await fetch(`${API_BASE}/posts/${id}`);
        return await response.json();
    } catch (error) {
        console.error('Get post error:', error);
        return null;
    }
}

async function createPost(title, content, password = '', imageFile = null) {
    try {
        const formData = new FormData();
        const postRequest = {
            title: title,
            content: content,
            password: password
        };

        formData.append('post', new Blob([JSON.stringify(postRequest)], {
            type: 'application/json'
        }));

        if (imageFile) {
            formData.append('image', imageFile);
        }

        const response = await fetch(`${API_BASE}/posts`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('게시글 작성에 실패했습니다');
        }

        return await response.json();
    } catch (error) {
        console.error('Create post error:', error);
        throw error;
    }
}

async function updatePost(id, title, content, password = '') {
    try {
        const response = await fetch(`${API_BASE}/posts/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, content, password }),
        });
        return await response.json();
    } catch (error) {
        console.error('Update post error:', error);
        throw error;
    }
}

async function deletePost(id) {
    try {
        const response = await fetch(`${API_BASE}/posts/${id}`, {
            method: 'DELETE',
        });
        return response.ok;
    } catch (error) {
        console.error('Delete post error:', error);
        return false;
    }
}

// Comment Functions
async function getComments(postId, page = 0) {
    try {
        const response = await fetch(`${API_BASE}/posts/${postId}/comments?page=${page}`);
        return await response.json();
    } catch (error) {
        console.error('Get comments error:', error);
        return { comments: [], currentPage: 0, totalPages: 0, totalComments: 0, hasNext: false, hasPrevious: false };
    }
}

async function createComment(postId, content, password = '') {
    try {
        const response = await fetch(`${API_BASE}/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ content, password }),
        });
        return await response.json();
    } catch (error) {
        console.error('Create comment error:', error);
        throw error;
    }
}

async function deleteComment(id) {
    try {
        const response = await fetch(`${API_BASE}/comments/${id}`, {
            method: 'DELETE',
        });
        return response.ok;
    } catch (error) {
        console.error('Delete comment error:', error);
        return false;
    }
}

// Utility Functions
// Helper Functions
function formatTimeAgo(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return '방금 전';
    if (minutes < 60) return `${minutes}분 전`;
    if (hours < 24) return `${hours}시간 전`;
    if (days < 7) return `${days}일 전`;

    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

function parseContent(text) {
    if (!text) return '';

    // 1. Escape HTML first for security
    let escaped = escapeHtml(text);

    // 2. Format Line Breaks
    escaped = escaped.replace(/\n/g, '<br>');

    // 3. YouTube Embedding Logic
    // Handles watch?v=, youtu.be/, shorts/, embed/, v/, live/
    const youtubeRegex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/(?:watch\?v=|shorts\/|v\/|embed\/|live\/|watch\?.+&v=)|youtu\.be\/)([a-zA-Z0-9_-]{11})(?:\S+)?/g;

    return escaped.replace(youtubeRegex, (match, videoId) => {
        return `
            <div class="video-container">
                <iframe 
                    src="https://www.youtube.com/embed/${videoId}" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                    allowfullscreen>
                </iframe>
            </div>
        `;
    });
}

function isNewPost(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    // Consider new if within 24 hours
    return (now - date) < 86400000;
}

function formatNumber(num) {
    if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

function showError(message) {
    alert(message);
}

function showSuccess(message) {
    alert(message);
}

// Vote Functions
async function votePost(postId, type) {
    try {
        const response = await fetch(`${API_BASE}/votes/posts/${postId}?type=${type}`, {
            method: 'POST',
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error('투표 실패');
        }
        return await response.json();
    } catch (error) {
        console.error('Vote post error:', error);
        throw error;
    }
}

async function voteComment(commentId, type) {
    try {
        const response = await fetch(`${API_BASE}/votes/comments/${commentId}?type=${type}`, {
            method: 'POST',
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error('투표 실패');
        }
        return await response.json();
    } catch (error) {
        console.error('Vote comment error:', error);
        throw error;
    }
}
