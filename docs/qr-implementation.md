# QR Code Implementation & Challenges

## Overview

The QR Code feature allows users to download a QR code for their shortened link. This implementation leverages:
- **ZXing** for backend generation (Matrix to Byte Array)
- **Blob** & **File System Access API** for frontend download

## 1. The Challenge: Cross-Origin Binary Downloads

### Problem
When frontend (`localhost:5173`) tries to download images from backend (`localhost:8080`), browsers block the `<a download>` attribute due to CORS security policies (Cross-Origin Resource Sharing).

### Initial Attempt (Failed)
```html
<!--Opens image in new tab instead of downloading -->
<a href="http://localhost:8080/api/urls/abc/qr" download="qr.png">Download</a>
```

## 2. The Solution: Fetch + Blob

Instead of relying on the browser's default download behavior, we use JavaScript to fetch the binary data manually.

```javascript
/* Result.jsx */
const downloadQr = async () => {
    // 1. Fetch binary data from backend
    const response = await fetch(qrUrl);
    const blob = await response.blob();
    
    // 2. Modern browsers: Let user choose save location
    if ('showSaveFilePicker' in window) {
        const handle = await window.showSaveFilePicker({
            suggestedName: `QR-${qrCode}.png`,
            types: [{ accept: { 'image/png': ['.png'] } }]
        });
        const writable = await handle.createWritable();
        await writable.write(blob);
        await writable.close();
    } else {
        // Fallback: Auto-download to default folder
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `QR-${qrCode}.png`;
        a.click();
        URL.revokeObjectURL(url);
    }
};
```

## 3. Key Takeaways

1. **CORS Configuration:** Spring Boot must explicitly allow the frontend origin (`CorsConfig.java`) for binary endpoints too.
2. **Content-Type:** Backend must return `Content-Type: image/png` correctly.
3. **UX Improvement:** The `showSaveFilePicker` API provides a superior experience compared to automatic downloads, especially for enterprise users managing multiple files.
