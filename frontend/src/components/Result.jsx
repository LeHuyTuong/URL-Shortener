function Result({ data }) {
    // data sẽ có dạng: { shortUrl: "...", shortCode: "..." }
    const link = data.shortUrl;
    const qrCode = data.shortCode;
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

    return (
        <div style={{ marginTop: '20px', border: '1px solid #ccc', padding: '10px' }}>
            <a href={link} target="_blank"> {link}</a>
            <img src={`${apiBaseUrl}/api/urls/${qrCode}/qr`} alt="QR Code" />

            <p>Kết quả sẽ hiện ở đây...</p>
        </div >
    );
}

export default Result;
