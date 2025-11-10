import pool from "../db.js";

export async function waitForDB(retries = 20, delayMs = 2000) {
  console.log("⏳ Waiting for Postgres to become reachable...");

  for (let i = 0; i < retries; i++) {
    try {
      await pool.query("SELECT 1");
      console.log("Postgres is ready!");
      return;
    } catch (err) {
      console.log(`⏳ Retry ${i + 1}/${retries}... DB not ready yet.`);
      await new Promise(resolve => setTimeout(resolve, delayMs));
    }
  }

  throw new Error("Postgres did not become ready in time.");
}
