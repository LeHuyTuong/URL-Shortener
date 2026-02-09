import { useState } from 'react';

function UrlForm({ onSuccess }) {
    const [longUrl, setLongUrl] = useState('');
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(apiBaseUrl + `/api/urls/shorten`, {
                method: 'POST', // method
                headers: {
                    'Content-Type': 'application/json', // báo server là client gửi json
                },
                body: JSON.stringify({ longUrl }), // biến object thành chuỗi json
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json(); // biến chuỗi json từ server trả cho client thành object
            console.log("Success:", data);
            onSuccess(data);
        } catch (error) {
            console.error('Error:', error);// lỗi console
            alert('Lỗi:' + error.message);// popup
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input
                type="url"
                value={longUrl}
                onChange={(e) => setLongUrl(e.target.value)}
                placeholder="Dán link vào đây anh ơi..."
            />
            <button type="submit">Rút gọn ngay</button>
        </form>
    );
}

export default UrlForm;
