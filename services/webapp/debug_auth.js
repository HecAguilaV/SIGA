
async function testAuth() {
    console.log('--- Testing Login ---');
    try {
        const loginRes = await fetch('http://localhost:1574/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: 'admin@test.cl', password: 'password123' })
        });

        console.log(`Login Status: ${loginRes.status}`);
        const loginText = await loginRes.text();
        console.log('Login Body:', loginText);
    } catch (e) {
        console.error('Login Failed:', e.message);
    }

    console.log('\n--- Testing Register ---');
    try {
        // Try to register a new user to check if DB writes work
        const regRes = await fetch('http://localhost:1574/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: `debug_${Date.now()}@test.cl`,
                password: 'Password123!',
                nombre: 'DebugUser',
                rol: 'OPERADOR'
            })
        });

        console.log(`Register Status: ${regRes.status}`);
        const regText = await regRes.text();
        console.log('Register Body:', regText);
    } catch (e) {
        console.error('Register Failed:', e.message);
    }
}

testAuth();
