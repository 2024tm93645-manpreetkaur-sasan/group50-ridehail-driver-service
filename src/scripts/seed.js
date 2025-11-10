import pool from "../db.js";
import fs from "fs";
import path from "path";
import { parse } from "csv-parse";

export async function seedDatabase() {
  console.log("Starting database readiness and seeding check...");

  // Ensure drivers table exists
  await pool.query(`
    CREATE TABLE IF NOT EXISTS drivers (
      driver_id SERIAL PRIMARY KEY,
      name TEXT NOT NULL,
      phone TEXT,
      vehicle_type TEXT,
      vehicle_plate TEXT UNIQUE,
      is_active BOOLEAN DEFAULT TRUE,
      created_at TIMESTAMPTZ DEFAULT NOW(),
      updated_at TIMESTAMPTZ DEFAULT NOW()
    );
  `);

  console.log("Drivers table ensured.");

  // Path to CSV file mounted into container
  const csvPath = path.join(process.cwd(), "data", "rhfd_drivers.csv");

  if (!fs.existsSync(csvPath)) {
    console.log("CSV file not found at:", csvPath);
    return;
  }

  // Check if database is already seeded
  const result = await pool.query("SELECT COUNT(*) FROM drivers");
  const existingCount = parseInt(result.rows[0].count, 10);

  if (existingCount > 0) {
    console.log(`Skipping seed: ${existingCount} drivers already exist.`);
    return;
  }

  console.log("Seeding database using CSV file:", csvPath);

  const parser = fs
    .createReadStream(csvPath)
    .pipe(parse({ columns: true, trim: true }));

  for await (const row of parser) {
    console.log("Seeding row:", row);

    await pool.query(
      `INSERT INTO drivers (name, phone, vehicle_type, vehicle_plate, is_active)
       VALUES ($1, $2, $3, $4, $5)`,
      [
        row.name,
        row.phone,
        row.vehicle_type,
        row.vehicle_plate,
        row.is_active?.toLowerCase() === "true"
      ]
    );
  }

  console.log("Driver seeding completed.");
}
