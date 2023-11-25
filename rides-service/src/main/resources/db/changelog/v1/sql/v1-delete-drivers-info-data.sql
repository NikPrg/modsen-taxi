DELETE FROM drivers_info
WHERE id IN (SELECT id FROM drivers_info ORDER BY id LIMIT 10);