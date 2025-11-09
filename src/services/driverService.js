import * as repo from '../models/driverModel.js';

// List drivers with pagination
export async function listDrivers({ limit, offset }) {
  return repo.findAll({ limit, offset });
}

// Get driver by ID
export async function getDriver(id) {
  return repo.findById(id);
}

// Create new driver
export async function createDriver(payload) {
  return repo.create(payload);
}

// Update driver
export async function updateDriver(id, payload) {
  return repo.update(id, payload);
}

// Delete driver
export async function deleteDriver(id) {
  return repo.remove(id);
}

// Update driver status
export async function updateStatus(id, isActive) {
  return repo.setStatus(id, isActive);
}

// Search driver (by name or phone)
export async function search(query) {
  return repo.search(query);
}
