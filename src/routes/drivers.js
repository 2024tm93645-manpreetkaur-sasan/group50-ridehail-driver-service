import { Router } from 'express';
import * as ctrl from '../controllers/driverController.js';

const router = Router();

router.get('/', ctrl.getAll);
router.get('/search', ctrl.search);
router.get('/:id', ctrl.getById);
router.post('/', ctrl.create);
router.put('/:id', ctrl.update);
router.delete('/:id', ctrl.del);
router.patch('/:id/status', ctrl.status);

export default router;
