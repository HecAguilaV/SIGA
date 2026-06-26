import { Hono } from "hono";
import { serve } from "bun";
import { Client, Events, GatewayIntentBits } from "discord.js";
import {
  insertNotification,
  listNotifications,
  markAsRead,
  markAllAsRead,
  countUnread,
} from "./db";

// ── Config ──
const PORT = parseInt(process.env.PORT || "9510");
const DISCORD_TOKEN = process.env.DISCORD_TOKEN || "";
const DISCORD_CHANNEL_ID = process.env.DISCORD_CHANNEL_ID || "";
const BOT_USERNAME = "SIGA Dev Bot";

// ── Discord Bot ──
let botReady = false;
let lastError: string | null = null;

if (DISCORD_TOKEN && DISCORD_CHANNEL_ID) {
  const client = new Client({
    intents: [
      GatewayIntentBits.Guilds,
      GatewayIntentBits.GuildMessages,
      GatewayIntentBits.MessageContent,
    ],
  });

  client.once(Events.ClientReady, (c) => {
    console.log(`✅ Discord bot connected as ${c.user.tag}`);
    botReady = true;
  });

  client.on(Events.MessageCreate, async (msg) => {
    // Ignore bot's own messages
    if (msg.author.bot) return;
    // Only listen to the configured channel
    if (msg.channelId !== DISCORD_CHANNEL_ID) return;

    const isIdea = msg.content.toLowerCase().startsWith("idea:") ||
                   msg.content.toLowerCase().startsWith("💡");

    const tags = isIdea ? "idea" : "discord";

    insertNotification("discord", msg.content, {
      channel: msg.channelId,
      author: msg.author.globalName || msg.author.username,
      attachments: msg.attachments.size > 0
        ? msg.attachments.map((a) => a.url).join("\n")
        : undefined,
      is_idea: isIdea,
      tags,
    });

    console.log(`📥 Discord message from ${msg.author.username}: ${msg.content.slice(0, 80)}`);
  });

  client.on(Events.Error, (err) => {
    console.error("❌ Discord error:", err.message);
    lastError = err.message;
  });

  client.login(DISCORD_TOKEN).catch((err) => {
    console.error("❌ Discord login failed:", err.message);
    lastError = err.message;
  });
} else {
  console.log("⚠️ Discord not configured — API mode only");
}

// ── HTTP API ──
const app = new Hono();

// Auth middleware (optional, same pattern as ContainerFlow)
app.use("*", async (c, next) => {
  const token = process.env.AUTH_TOKEN;
  if (token) {
    const auth = c.req.header("Authorization");
    if (!auth || auth !== `Bearer ${token}`) {
      return c.json({ error: "Unauthorized" }, 401);
    }
  }
  await next();
});

// CORS for dashboard
app.use("*", async (c, next) => {
  c.header("Access-Control-Allow-Origin", "*");
  c.header("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
  c.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
  if (c.req.method === "OPTIONS") return c.body(null, 204);
  await next();
});

// Health check
app.get("/api/health", (c) => {
  return c.json({
    status: "ok",
    bot: botReady ? "connected" : lastError ? `error: ${lastError}` : "starting",
    uptime: process.uptime(),
  });
});

// List notifications
app.get("/api/notifications", (c) => {
  const unread = c.req.query("unread") === "true";
  const source = c.req.query("source");
  const limit = parseInt(c.req.query("limit") || "50");
  const notes = listNotifications({ unread, source: source || undefined, limit });
  return c.json({
    notifications: notes,
    unread_count: countUnread(),
  });
});

// Count unread
app.get("/api/notifications/count", (c) => {
  return c.json({ unread_count: countUnread() });
});

// Mark one as read
app.post("/api/notifications/:id/read", (c) => {
  const id = parseInt(c.req.param("id"));
  if (!id) return c.json({ error: "Invalid id" }, 400);
  const ok = markAsRead(id);
  return c.json({ ok });
});

// Mark all as read
app.post("/api/notifications/read-all", (c) => {
  const count = markAllAsRead();
  return c.json({ ok: true, count });
});

// Add a manual notification (for future web/Telegram/etc inputs)
app.post("/api/notifications", async (c) => {
  const body = await c.req.json();
  const { content, source, author, tags, is_idea } = body;
  if (!content) return c.json({ error: "content is required" }, 400);

  const note = insertNotification(source || "manual", content, {
    author: author || "unknown",
    is_idea: !!is_idea,
    tags: tags || "manual",
  });
  return c.json(note, 201);
});

// Bot status
app.get("/api/status", (c) => {
  return c.json({
    bot: botReady ? "connected" : "disconnected",
    last_error: lastError,
    channel: DISCORD_CHANNEL_ID ? "configured" : "not set",
  });
});

// ── Serve ──
const server = serve({
  port: PORT,
  fetch: app.fetch,
});

console.log(`🚀 SIGA Dev Bot API running on :${PORT}`);
console.log(`   GET  /api/notifications`);
console.log(`   GET  /api/notifications/count`);
console.log(`   POST /api/notifications/:id/read`);
console.log(`   POST /api/notifications/read-all`);
console.log(`   POST /api/notifications`);
console.log(`   GET  /api/health`);
console.log(`   GET  /api/status`);
