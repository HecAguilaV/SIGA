
// debug_cors.js
// Script to probe CORS allowed origins on the production backend

const BACKEND_URL = 'https://siga-backend-production.up.railway.app';

async function testOrigin(originName, originValue) {
    console.log(`\n--- Testing Origin: ${originName} (${originValue}) ---`);
    try {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (originValue) {
            headers['Origin'] = originValue;
        }

        const res = await fetch(`${BACKEND_URL}/api/auth/login`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({ email: 'admin@test.cl', password: 'password123' })
        });

        console.log(`Status: ${res.status} ${res.statusText}`);
        const text = await res.text();
        console.log(`Body: ${text.substring(0, 100)}...`); // Print first 100 chars

        if (res.status === 403 && text.includes("Invalid CORS")) {
            console.log("RESULT: ❌ REJECTED (CORS)");
        } else if (res.status === 401) {
            console.log("RESULT: ✅ ACCEPTED (CORS passed, Auth failed - Good!)");
        } else if (res.status === 200) {
            console.log("RESULT: ✅✅ SUCCESS");
        } else {
            console.log("RESULT: ❓ UNKNOWN");
        }
    } catch (e) {
        console.error('Request Failed:', e.message);
    }
}

async function run() {
    await testOrigin('No Origin', null);
    await testOrigin('Vercel App', 'https://siga-appweb.vercel.app');
    await testOrigin('Vercel App (Trailing Slash)', 'https://siga-appweb.vercel.app/');
    await testOrigin('Localhost 1573', 'http://localhost:1573');
    await testOrigin('Localhost 1574', 'http://localhost:1574');
    await testOrigin('Railway Domain', 'https://siga-backend-production.up.railway.app');
}

run();
