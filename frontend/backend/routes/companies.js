import express from 'express';
import { createCrudRoutes } from './crud.js';
import { authenticateToken, requireAdmin } from '../middleware/auth.js';

const router = express.Router();

// Las empresas las gestiona el administrador
router.use(authenticateToken, requireAdmin);
router.use('/', createCrudRoutes('companies'));

export default router;
