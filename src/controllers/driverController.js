import * as service from '../services/driverService.js';

export async function getAll(req, res, next) {
  try {
    const limit = parseInt(req.query.limit || '100', 10);
    const offset = parseInt(req.query.offset || '0', 10);
    const data = await service.listDrivers({ limit, offset });
    res.json(data);
  } catch (err) {
    next(err);
  }
}

export async function getById(req, res, next) {
  try {
    const driver = await service.getDriver(req.params.id);
    if (!driver) {
      return res.status(404).json({ error: { message: 'Driver not found' } });
    }
    res.json(driver);
  } catch (err) {
    next(err);
  }
}

export async function search(req, res, next) {
  try {
    const { name, phone } = req.query;

    if (!name && !phone) {
      return res.status(400).json({
        error: { message: 'Provide name or phone to search' }
      });
    }

    const results = await service.search({ name, phone });
    if (!results.length) {
      return res.status(404).json({ error: { message: 'No matching drivers' } });
    }

    res.json(results);
  } catch (err) {
    next(err);
  }
}

export async function create(req, res, next) {
  try {
    const created = await service.createDriver(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
}

export async function update(req, res, next) {
  try {
    const updated = await service.updateDriver(req.params.id, req.body);
    if (!updated) {
      return res.status(404).json({ error: { message: 'Driver not found' } });
    }
    res.json(updated);
  } catch (err) {
    next(err);
  }
}

export async function del(req, res, next) {
  try {
    const ok = await service.deleteDriver(req.params.id);
    if (!ok) {
      return res.status(404).json({ error: { message: 'Driver not found' } });
    }
    res.status(204).end();
  } catch (err) {
    next(err);
  }
}

export async function status(req, res, next) {
  try {
    const isActive = req.body?.is_active;

    if (typeof isActive !== 'boolean') {
      return res.status(400).json({
        error: { message: 'is_active (boolean) is required' }
      });
    }

    const updated = await service.updateStatus(req.params.id, isActive);
    if (!updated) {
      return res.status(404).json({ error: { message: 'Driver not found' } });
    }

    res.json(updated);
  } catch (err) {
    next(err);
  }
}
