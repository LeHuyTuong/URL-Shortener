import { useState, useEffect } from 'react';

function Dashboard() {
    // State = "Bộ nhớ" của component
    // Khi state thay đổi → React tự động vẽ lại UI

    const [analytics, setAnalytics] = useState([]); // data từ API
    const [loading, setLoading] = useState(true);
    //Vì ban đầu component chưa có data → đang loading → nên true, không phải null.
    const [error, setError] = useState(null); // ban đầu chưa có lỗi
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '';

    // useEffect để gọi fetchAnalytics khi component mount
    useEffect(() => {
        fetchAnalytics();
    }, []);

    const fetchAnalytics = async () => {
        // 1. Set loading = true
        setLoading(true);
        // 2. Gọi fetch(`${apiBaseUrl}/api/analytics`)
        try {
            const apiUrl = apiBaseUrl ? `${apiBaseUrl}/api/analytics` : '/api/analytics';
            const response = await fetch(apiUrl);
            // 3. Kiểm tra response.ok
            if (!response.ok) {
                throw new Error(`HTTP error! status : ${response.status}`);
            }
            // 4. Parse JSON và setAnalytics(data)
            const data = await response.json();
            setAnalytics(data);
        } catch (error) {
            // 5. Catch error và setError
            setError(error);
            console.error('Error', error);
            alert('Error' + error.message);
        } finally {
            setLoading(false);
            // 6. Finally set loading = false
        }
    };

    if (loading) return <p>Loading analytics...</p>
    if (error) return <p style={{ color: 'red' }}>Error : {error.message}</p>
    return (
        <div className="card" style={{ marginTop: '40px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2 style={{ margin: 0 }}>Analytics Dashboard</h2>
                <button type='button' onClick={fetchAnalytics} style={{ padding: '8px 16px', fontSize: '0.9rem' }}>Refresh</button>
            </div>

            {analytics.length === 0 ? (
                <p>No data</p>) : (
                <table>
                    <thead>
                        <tr>
                            <th>Original URL</th>
                            <th>Short URL</th>
                            <th>Click Count</th>
                        </tr>
                    </thead>
                    <tbody>
                        {analytics.map((items) => (
                            <tr key={items.shortCode}>
                                <td><a href={items.originalUrl}>{items.originalUrl}</a></td>
                                <td><a href={items.shortUrl}>{items.shortUrl}</a></td>
                                <td>{items.clickCount}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default Dashboard;
