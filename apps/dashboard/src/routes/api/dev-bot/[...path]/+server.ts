import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const DEV_BOT_URL = 'http://siga-dev-bot:9510';
const AUTH_TOKEN = 'siga2026';

export const GET: RequestHandler = async ({ params, url }) => {
  const path = params.path || '';
  const query = url.search || '';
  const targetUrl = `${DEV_BOT_URL}/api/${path}${query}`;

  try {
    const res = await fetch(targetUrl, {
      headers: { 'Authorization': `Bearer ${AUTH_TOKEN}` }
    });
    const data = await res.json();
    return json(data, { status: res.status });
  } catch (e) {
    console.error('Dev-bot proxy error:', e);
    return json({ error: 'Failed to reach dev-bot' }, { status: 502 });
  }
};

export const POST: RequestHandler = async ({ params, request }) => {
  const path = params.path || '';
  const targetUrl = `${DEV_BOT_URL}/api/${path}`;

  try {
    const res = await fetch(targetUrl, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${AUTH_TOKEN}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(await request.json().catch(() => ({})))
    });
    const data = await res.json();
    return json(data, { status: res.status });
  } catch (e) {
    console.error('Dev-bot proxy error:', e);
    return json({ error: 'Failed to reach dev-bot' }, { status: 502 });
  }
};
