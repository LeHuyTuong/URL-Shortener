const http = require('http');

const BACKEND_URL = 'http://163.61.111.120:8081';

module.exports = async (req, res) => {
    const targetUrl = `${BACKEND_URL}${req.url}`;

    // Collect request body
    const bodyChunks = [];
    for await (const chunk of req) {
        bodyChunks.push(chunk);
    }
    const body = Buffer.concat(bodyChunks);

    // Forward headers, remove host
    const headers = { ...req.headers };
    delete headers.host;
    delete headers['x-forwarded-host'];
    delete headers['x-vercel-id'];
    delete headers['x-vercel-deployment-url'];
    delete headers['x-vercel-forwarded-for'];

    return new Promise((resolve, reject) => {
        const parsedUrl = new URL(targetUrl);
        const options = {
            hostname: parsedUrl.hostname,
            port: parsedUrl.port,
            path: parsedUrl.pathname + parsedUrl.search,
            method: req.method,
            headers: {
                ...headers,
                'Content-Length': body.length,
            },
        };

        const proxyReq = http.request(options, (proxyRes) => {
            // Forward status and headers
            res.status(proxyRes.statusCode);
            Object.entries(proxyRes.headers).forEach(([key, value]) => {
                // Skip transfer-encoding as Vercel handles it
                if (key.toLowerCase() !== 'transfer-encoding') {
                    res.setHeader(key, value);
                }
            });

            // Stream response body
            const chunks = [];
            proxyRes.on('data', (chunk) => chunks.push(chunk));
            proxyRes.on('end', () => {
                const responseBody = Buffer.concat(chunks);
                res.end(responseBody);
                resolve();
            });
        });

        proxyReq.on('error', (err) => {
            console.error('Proxy error:', err);
            res.status(502).json({ error: 'Bad Gateway', message: err.message });
            resolve();
        });

        // Send body
        if (body.length > 0) {
            proxyReq.write(body);
        }
        proxyReq.end();
    });
};
