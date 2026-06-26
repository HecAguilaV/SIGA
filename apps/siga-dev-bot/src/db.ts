import { Database } from "bun:sqlite";
import path from "path";

const DATA_DIR = process.env.DATA_DIR || path.join(import.meta.dir, "..", "data");
const DB_PATH = path.join(DATA_DIR, "notifications.db");

// Ensure data dir exists
try { require("fs").mkdirSync(DATA_DIR, { recursive: true }); } catch {}

const db = new Database(DB_PATH);
db.run("PRAGMA journal_mode = WAL");
db.run("PRAGMA foreign_keys = ON");

db.run(`CREATE TABLE IF NOT EXISTS notifications (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  source TEXT NOT NULL DEFAULT 'discord',
  channel TEXT,
  author TEXT,
  content TEXT NOT NULL,
  attachments TEXT,
  is_read INTEGER NOT NULL DEFAULT 0,
  is_idea INTEGER NOT NULL DEFAULT 0,
  tags TEXT,
  created_at TEXT NOT NULL DEFAULT (datetime('now'))
)`);

export interface Notification {
  id: number;
  source: string;
  channel?: string;
  author?: string;
  content: string;
  attachments?: string;
  is_read: number;
  is_idea: number;
  tags?: string;
  created_at: string;
}

export function insertNotification(
  source: string,
  content: string,
  opts?: {
    channel?: string;
    author?: string;
    attachments?: string;
    is_idea?: boolean;
    tags?: string;
  },
): Notification {
  const stmt = db.prepare(
    `INSERT INTO notifications (source, channel, author, content, attachments, is_idea, tags)
     VALUES (?, ?, ?, ?, ?, ?, ?)`,
  );
  const result = stmt.run(
    source,
    opts?.channel || null,
    opts?.author || null,
    content,
    opts?.attachments || null,
    opts?.is_idea ? 1 : 0,
    opts?.tags || null,
  );
  return getNotification(result.lastInsertRowid as number)!;
}

export function getNotification(id: number): Notification | undefined {
  return db.prepare("SELECT * FROM notifications WHERE id = ?").get(id) as Notification | undefined;
}

export function listNotifications(opts?: { unread?: boolean; source?: string; limit?: number }): Notification[] {
  let sql = "SELECT * FROM notifications WHERE 1=1";
  const params: any[] = [];

  if (opts?.unread) {
    sql += " AND is_read = 0";
  }
  if (opts?.source) {
    sql += " AND source = ?";
    params.push(opts.source);
  }

  sql += " ORDER BY created_at DESC LIMIT ?";
  params.push(opts?.limit ?? 50);

  return db.prepare(sql).all(...params) as Notification[];
}

export function markAsRead(id: number): boolean {
  const result = db.prepare("UPDATE notifications SET is_read = 1 WHERE id = ?").run(id);
  return result.changes > 0;
}

export function markAllAsRead(): number {
  const result = db.prepare("UPDATE notifications SET is_read = 1 WHERE is_read = 0").run();
  return result.changes;
}

export function countUnread(): number {
  const row = db.prepare("SELECT COUNT(*) as count FROM notifications WHERE is_read = 0").get() as any;
  return row?.count ?? 0;
}
