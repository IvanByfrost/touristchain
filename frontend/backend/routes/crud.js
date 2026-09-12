import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';

export function createCrudRoutes(collectionName, options = {}) {
  const router = express.Router();
  const { filterByUser = false, adminOnly = false, omit = [] } = options;

  const clean = (item) => {
    if (!item || omit.length === 0) return item;
    const copy = { ...item };
    omit.forEach(k => delete copy[k]);
    return copy;
  };

  router.get('/', (req, res) => {
    try {
      const db = readDb();
      let data = db[collectionName] || [];

      if (filterByUser && req.user.role !== 'admin') {
        data = data.filter(item => item.userId === req.user.id);
      }

      res.json(data.map(clean));
    } catch (error) {
      console.error(`Error al obtener ${collectionName}:`, error);
      res.status(500).json({ error: 'Error interno del servidor' });
    }
  });

  router.get('/:id', (req, res) => {
    try {
      const db = readDb();
      const data = db[collectionName] || [];
      const item = data.find(i => i.id === parseInt(req.params.id));

      if (!item) {
        return res.status(404).json({ error: 'Recurso no encontrado' });
      }

      if (filterByUser && req.user.role !== 'admin' && item.userId !== req.user.id) {
        return res.status(403).json({ error: 'Acceso denegado' });
      }

      res.json(clean(item));
    } catch (error) {
      console.error(`Error al obtener ${collectionName}:`, error);
      res.status(500).json({ error: 'Error interno del servidor' });
    }
  });

  router.post('/', (req, res) => {
    try {
      const db = readDb();
      const data = db[collectionName] || [];

      const newItem = {
        id: generateId(data),
        ...req.body,
        createdAt: new Date().toISOString().split('T')[0]
      };

      if (filterByUser) {
        newItem.userId = req.user.id;
      }

      data.push(newItem);
      db[collectionName] = data;
      writeDb(db);

      res.status(201).json(clean(newItem));
    } catch (error) {
      console.error(`Error al crear ${collectionName}:`, error);
      res.status(500).json({ error: 'Error interno del servidor' });
    }
  });

  router.put('/:id', (req, res) => {
    try {
      const db = readDb();
      const data = db[collectionName] || [];
      const index = data.findIndex(i => i.id === parseInt(req.params.id));

      if (index === -1) {
        return res.status(404).json({ error: 'Recurso no encontrado' });
      }

      if (filterByUser && req.user.role !== 'admin' && data[index].userId !== req.user.id) {
        return res.status(403).json({ error: 'Acceso denegado' });
      }

      data[index] = { ...data[index], ...req.body, id: data[index].id };
      db[collectionName] = data;
      writeDb(db);

      res.json(clean(data[index]));
    } catch (error) {
      console.error(`Error al actualizar ${collectionName}:`, error);
      res.status(500).json({ error: 'Error interno del servidor' });
    }
  });

  router.delete('/:id', (req, res) => {
    try {
      const db = readDb();
      const data = db[collectionName] || [];
      const index = data.findIndex(i => i.id === parseInt(req.params.id));

      if (index === -1) {
        return res.status(404).json({ error: 'Recurso no encontrado' });
      }

      if (filterByUser && req.user.role !== 'admin' && data[index].userId !== req.user.id) {
        return res.status(403).json({ error: 'Acceso denegado' });
      }

      const deleted = data.splice(index, 1)[0];
      db[collectionName] = data;
      writeDb(db);

      res.json(clean(deleted));
    } catch (error) {
      console.error(`Error al eliminar ${collectionName}:`, error);
      res.status(500).json({ error: 'Error interno del servidor' });
    }
  });

  return router;
}
