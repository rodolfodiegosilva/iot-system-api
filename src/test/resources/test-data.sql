-- Limpando as tabelas
DELETE FROM parameters;
DELETE FROM command_descriptions;
DELETE FROM commands;
DELETE FROM devices;
DELETE FROM users;

-- Resetando os valores AUTO_INCREMENT
ALTER TABLE parameters ALTER COLUMN id RESTART WITH 1;
ALTER TABLE command_descriptions ALTER COLUMN id RESTART WITH 1;
ALTER TABLE commands ALTER COLUMN id RESTART WITH 1;
ALTER TABLE devices ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

-- Inserindo dados na tabela users
INSERT INTO users (`name`, `email`, `password`, `role`, `username`) VALUES
('User Test 1','usertest1@example.com','$2a$10$krGayxtrxtnnUDjSMXkaEurwAu/6PTFX3ABx37cugn.LJasquG8nC','USER','usertest1'),
('Admin User', 'admin@example.com', '$2a$10$krGayxtrxtnnUDjSMXkaEurwAu/6PTFX3ABx37cugn.LJasquG8nC', 'ADMIN', 'admin'),
('Unauthorized User', 'unauthorized@example.com', '$2a$10$krGayxtrxtnnUDjSMXkaEurwAu/6PTFX3ABx37cugn.LJasquG8nC', 'USER', 'unauthorized');

-- Inserindo dados na tabela devices
INSERT INTO devices (device_code, device_name, description, device_status, industry_type, manufacturer, url, user_id, created_at) VALUES
('DVC00001', 'Moisture Sensor', 'Soil moisture sensor for precision irrigation', 0, 'Agriculture', 'AgriTech Co.', 'http://localhost:8080/devices/command/DVC00001', 1, NOW()),
('DVC00002', 'Predefined Device', 'Predefined Description', 0, 'Test Industry', 'Test Manufacturer', 'http://localhost:8080/devices/command/DVC00002', 1, NOW()),
('DVC00003', 'Tractor Controller', 'Automated tractor controller', 2, 'Agriculture', 'TractorTech', 'http://localhost:8080/devices/command/DVC00003', 2, NOW());

-- Inserindo dados na tabela commands
INSERT INTO commands (command) VALUES
('Activate Moisture Sensor'), 
('Deactivate Moisture Sensor'),
('Activate Climate Sensor'), 
('Deactivate Climate Sensor'),
('Activate Tractor Controller'), 
('Deactivate Tractor Controller');

-- Inserindo dados na tabela command_descriptions
INSERT INTO command_descriptions (operation, description, result, format, device_id, command_id) VALUES
('Activate', 'Turn on the moisture sensor', 'Sensor activated', 'JSON', 1, 1),
('Deactivate', 'Turn off the moisture sensor', 'Sensor deactivated', 'JSON', 1, 2),
('Activate', 'Turn on the climate sensor', 'Climate sensor activated', 'JSON', 2, 3),
('Deactivate', 'Turn off the climate sensor', 'Climate sensor deactivated', 'JSON', 2, 4);

-- Inserindo dados na tabela parameters
INSERT INTO parameters (name, description, command_id) VALUES
('sensor_id', 'The unique identifier of the moisture sensor', 1),
('sensor_id', 'The unique identifier of the moisture sensor', 2),
('sensor_id', 'The unique identifier of the climate sensor', 3),
('sensor_id', 'The unique identifier of the climate sensor', 4);
