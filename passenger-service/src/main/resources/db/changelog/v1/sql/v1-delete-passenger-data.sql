DELETE FROM passengers
WHERE id IN (SELECT id FROM passengers ORDER BY id LIMIT 10);
