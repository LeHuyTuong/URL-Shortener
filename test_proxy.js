const http = require('http');
const https = require('https');

const data = JSON.stringify({ longUrl: 'https://google.com' });

const options = {
  hostname: 'url-shortener-tuong.vercel.app',
  port: 443,
  path: '/api/urls/shorten',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
};

const req = https.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
  res.setEncoding('utf8');
  res.on('data', (chunk) => {
    console.log(`BODY: ${chunk}`);
  });
});

req.on('error', (e) => {
  console.error(`problem with request: ${e.message}`);
});

req.write(data);
req.end();
