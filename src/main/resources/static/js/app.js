// Random Emoji Generator
const EMOJIS = [
    '😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂', '🙂', '🙃',
    '😉', '😊', '😇', '🥰', '😍', '🤩', '😘', '😗', '😚', '😙',
    '😋', '😛', '😜', '🤪', '😝', '🤑', '🤗', '🤭', '🤫', '🤔',
    '🤐', '🤨', '😐', '😑', '😶', '😏', '😒', '🙄', '😬', '🤥',
    '😌', '😔', '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢', '🤮',
    '🤧', '🥵', '🥶', '🥴', '😵', '🤯', '🤠', '🥳', '😎', '🤓',
    '🧐', '😕', '😟', '🙁', '☹️', '😮', '😯', '😲', '😳', '🥺',
    '😦', '😧', '😨', '😰', '😥', '😢', '😭', '😱', '😖', '😣',
    '😞', '😓', '😩', '😫', '🥱', '😤', '😡', '😠', '🤬', '😈',
    '👿', '💀', '☠️', '💩', '🤡', '👹', '👺', '👻', '👽', '👾'
];

function getRandomEmoji() {
    return EMOJIS[Math.floor(Math.random() * EMOJIS.length)];
}

// API Base URL
const API_BASE = '/api';

// Auth Functions
async function signup(username, password, nickname) {
    try {
        const response = await fetch(`${API_BASE}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password, nickname }),
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
async function getComments(postId) {
    try {
        const response = await fetch(`${API_BASE}/posts/${postId}/comments`);
        return await response.json();
    } catch (error) {
        console.error('Get comments error:', error);
        return [];
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
function formatDate(dateString) {
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

    return date.toLocaleDateString('ko-KR');
}

function showError(message) {
    alert(message);
}

function showSuccess(message) {
    alert(message);
}
