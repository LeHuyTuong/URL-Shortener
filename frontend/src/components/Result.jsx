function Result({ data }) {
    // data sẽ có dạng: { shortUrl: "...", shortCode: "..." }
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '';
    const shortCode = data.shortCode;
    const link = apiBaseUrl ? `${apiBaseUrl}/${shortCode}` : `${window.location.origin}/${shortCode}`;
    const qrCode = shortCode;
    const qrUrl = apiBaseUrl ? `${apiBaseUrl}/api/urls/${qrCode}/qr` : `/api/urls/${qrCode}/qr`;

    const downloadQr = async () => {
        try {
            // 1. Fetch ảnh từ backend (binary)
            const response = await fetch(qrUrl);
            const blob = await response.blob();

            // 2. Kiểm tra trình duyệt có hỗ trợ showSaveFilePicker không
            if ('showSaveFilePicker' in window) {
                // ✅ Modern Browsers (Chrome, Edge)
                const handle = await window.showSaveFilePicker({
                    suggestedName: `QR-${qrCode}.png`,
                    types: [{
                        description: 'PNG Image',
                        accept: { 'image/png': ['.png'] },
                    }],
                });

                // 3. Ghi file vào chỗ user chọn
                const writable = await handle.createWritable();
                await writable.write(blob);
                await writable.close();

            } else {
                // ⚠️ Fallback cho trình duyệt cũ (Firefox, Safari)
                // Vẫn dùng cách tạo thẻ <a>
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `QR-${qrCode}.png`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            }
        } catch (error) {
            if (error.name !== 'AbortError') { // User bấm Cancel thì ko báo lỗi
                console.error('Download failed:', error);
                alert('Failed to save QR Code');
            }
        }
    };

    return (
        <div className="result-card">
            <h3>Ready! Here is your short link:</h3>
            <div style={{ marginBottom: '1rem' }}>
                <a href={link} target="_blank" rel="noopener noreferrer" style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>
                    {link}
                </a>
            </div>

            <img src={qrUrl} alt="QR Code" className="qr-code" width="200" height="200" />

            <div style={{ marginTop: '1rem' }}>
                <button type="button" onClick={downloadQr} style={{ backgroundColor: '#0066cc', marginTop: '10px' }}>
                    Download QR Code
                </button>
            </div>
        </div >
    );
}

export default Result;
