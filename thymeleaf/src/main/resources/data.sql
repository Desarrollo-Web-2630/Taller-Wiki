-- Root entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (1, 'Inicio', '/wiki/inicio', NULL, 1, false, 'wiki/inicio.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (2, 'Arquitectura', '/wiki/arquitectura', NULL, 2, false, 'wiki/arquitectura.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (3, 'Historias de Usuario', '/wiki/historias-usuario', NULL, 3, false, 'wiki/historias-usuario.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (4, 'Entregas', '/wiki/entregas', NULL, 4, false, 'wiki/entregas.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (5, 'Contacto', '/contacto', NULL, 5, false, 'wiki/contacto.html');

-- Historias de Usuario - children
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (6,  'Autenticacion', '/wiki/historias-usuario/autenticacion', 3, 1, false, 'wiki/autenticacion.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (7,  'Procesos', '/wiki/historias-usuario/procesos', 3, 2, false, 'wiki/procesos.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (8,  'Actividades', '/wiki/historias-usuario/actividades', 3, 3, false, 'wiki/actividades.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (9,  'Arcos', '/wiki/historias-usuario/arcos', 3, 4, false, 'wiki/arcos.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (10, 'Gateways', '/wiki/historias-usuario/gateways', 3, 5, false, 'wiki/gateways.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (11, 'Roles', '/wiki/historias-usuario/roles', 3, 6, false, 'wiki/roles.html');

-- Autenticacion sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (12, 'HU-01 Registro empresa', '/wiki/historias-usuario/hu-01', 6, 1, false, 'wiki/hu-01.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (13, 'HU-02 Registro usuarios', '/wiki/historias-usuario/hu-02', 6, 2, false, 'wiki/hu-02.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (14, 'HU-03 Login', '/wiki/historias-usuario/hu-03', 6, 3, false, 'wiki/hu-03.html');

-- Procesos sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (15, 'HU-04 Crear proceso', '/wiki/historias-usuario/hu-04', 7, 1, false, 'wiki/hu-04.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (16, 'HU-05 Editar proceso', '/wiki/historias-usuario/hu-05', 7, 2, false, 'wiki/hu-05.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (17, 'HU-06 Eliminar proceso', '/wiki/historias-usuario/hu-06', 7, 3, false, 'wiki/hu-06.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (18, 'HU-07 Consultar proceso', '/wiki/historias-usuario/hu-07', 7, 4, false, 'wiki/hu-07.html');

-- Actividades sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (19, 'HU-08 Crear actividad', '/wiki/historias-usuario/hu-08', 8, 1, false, 'wiki/hu-08.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (20, 'HU-09 Editar actividad', '/wiki/historias-usuario/hu-09', 8, 2, false, 'wiki/hu-09.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (21, 'HU-10 Eliminar actividad', '/wiki/historias-usuario/hu-10', 8, 3, false, 'wiki/hu-10.html');

-- Arcos sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (22, 'HU-11 Crear arco', '/wiki/historias-usuario/hu-11', 9, 1, false, 'wiki/hu-11.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (23, 'HU-12 Editar arco', '/wiki/historias-usuario/hu-12', 9, 2, false, 'wiki/hu-12.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (24, 'HU-13 Eliminar arco', '/wiki/historias-usuario/hu-13', 9, 3, false, 'wiki/hu-13.html');

-- Gateways sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (25, 'HU-14 Crear gateway', '/wiki/historias-usuario/hu-14', 10, 1, false, 'wiki/hu-14.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (26, 'HU-15 Editar gateway', '/wiki/historias-usuario/hu-15', 10, 2, false, 'wiki/hu-15.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (27, 'HU-16 Eliminar gateway', '/wiki/historias-usuario/hu-16', 10, 3, false, 'wiki/hu-16.html');

-- Roles sub-entries
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (28, 'HU-17 Crear rol', '/wiki/historias-usuario/hu-17', 11, 1, false, 'wiki/hu-17.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (29, 'HU-18 Editar rol', '/wiki/historias-usuario/hu-18', 11, 2, false, 'wiki/hu-18.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (30, 'HU-19 Eliminar rol', '/wiki/historias-usuario/hu-19', 11, 3, false, 'wiki/hu-19.html');
INSERT INTO wiki_entry (id, title, url, parent_id, order_index, active, content_path) VALUES (31, 'HU-20 Consultar rol', '/wiki/historias-usuario/hu-20', 11, 4, false, 'wiki/hu-20.html');
