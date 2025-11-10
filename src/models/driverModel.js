import pool from '../db.js';

// Get all drivers
export async function findAll({ limit = 100, offset = 0 } = {}) {
  const { rows } = await pool.query(
    'SELECT * FROM drivers ORDER BY driver_id LIMIT $1 OFFSET $2',
    [limit, offset]
  );
  return rows;
}

// Get one driver by ID
export async function findById(id) {
  const { rows } = await pool.query(
    'SELECT * FROM drivers WHERE driver_id = $1',
    [id]
  );
  return rows[0];
}

// Create a driver
export async function create(driver) {
  const { name, phone, vehicle_type, vehicle_plate, is_active } = driver;

  const { rows } = await pool.query(
    `INSERT INTO drivers (name, phone, vehicle_type, vehicle_plate, is_active)
     VALUES ($1, $2, $3, $4, COALESCE($5, true))
     RETURNING *`,
    [name, phone, vehicle_type, vehicle_plate, is_active]
  );

  return rows[0];
}

// Update a driver (partial updates allowed)
export async function update(id, driver) {
  const { name, phone, vehicle_type, vehicle_plate, is_active } = driver;

  const { rows } = await pool.query(
    `UPDATE drivers SET
       name = COALESCE($2, name),
       phone = COALESCE($3, phone),
       vehicle_type = COALESCE($4, vehicle_type),
       vehicle_plate = COALESCE($5, vehicle_plate),
       is_active = COALESCE($6, is_active),
       updated_at = NOW()
     WHERE driver_id = $1
     RETURNING *`,
    [id, name, phone, vehicle_type, vehicle_plate, is_active]
  );

  return rows[0];
}

// Delete driver
export async function remove(id) {
  const { rowCount } = await pool.query(
    'DELETE FROM drivers WHERE driver_id = $1',
    [id]
  );
  return rowCount > 0;
}

// Update active status
export async function setStatus(id, isActive) {
  const { rows } = await pool.query(
    `UPDATE drivers
     SET is_active = $2, updated_at = NOW()
     WHERE driver_id = $1
     RETURNING *`,
    [id, isActive]
  );
  return rows[0];
}

// Searching by name or phone
export async function search({ name, phone }) {
  if (phone) {
    const { rows } = await pool.query(
      'SELECT * FROM drivers WHERE phone = $1',
      [phone]
    );
    return rows;
  }

  if (name) {
    const { rows } = await pool.query(
      'SELECT * FROM drivers WHERE LOWER(name) LIKE LOWER($1)',
      ['%' + name + '%']
    );
    return rows;
  }

  return [];
}
