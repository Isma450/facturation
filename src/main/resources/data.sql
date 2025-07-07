-- Script de fixtures pour la base de données facturation
-- Données de test complètes

-- 1. UTILISATEUR ADMIN
INSERT INTO users (username, password_hash, role, adresse) VALUES 
('admin', '$2a$10$xn3LI/AjqicFYZFruBbHgOhqpDpPx5uhNx4EEPZ4fKhCt5OsOA7HW', 'ADMIN', '456 Avenue de la Gestion, 69001 Lyon');

-- 2. ENTREPRISES CLIENTES
INSERT INTO entreprise (nom, adresse, email, siret) VALUES 
('TechCorp Solutions', '789 Boulevard de l''Innovation, 75012 Paris', 'contact@techcorp-solutions.fr', '12345678901234'),
('Design Studio Pro', '321 Rue Créative, 13001 Marseille', 'hello@designstudio-pro.com', '56789012345678'),
('Consulting Expert', '654 Avenue Business, 33000 Bordeaux', 'info@consulting-expert.fr', '98765432109876'),
('Formation Plus', '987 Place de l''Éducation, 59000 Lille', 'contact@formation-plus.fr', '11223344556677'),
('Digital Factory', '147 Rue du Numérique, 67000 Strasbourg', 'team@digital-factory.eu', '55443322110099');

-- 3. PRESTATIONS DIVERSES
-- 3.1 Consultations
INSERT INTO prestation (date, entreprise_id, user_id) VALUES 
('2024-01-15', 1, 1),
('2024-01-22', 2, 1),
('2024-02-05', 3, 1),
('2024-02-12', 1, 1),
('2024-02-18', 4, 1),
('2024-03-01', 2, 1),
('2024-03-08', 5, 1),
('2024-03-15', 3, 1),
('2024-03-22', 1, 1),
('2024-03-29', 4, 1);

INSERT INTO consultation (id, tjm) VALUES 
(1, 600.00),
(2, 650.00),
(3, 580.00),
(4, 620.00),
(5, 700.00),
(6, 675.00),
(7, 590.00),
(8, 610.00),
(9, 640.00),
(10, 720.00);

-- 3.2 Formations
INSERT INTO prestation (date, entreprise_id, user_id) VALUES 
('2024-04-10', 1, 1),
('2024-04-17', 2, 1),
('2024-04-24', 3, 1),
('2024-05-08', 4, 1),
('2024-05-15', 5, 1),
('2024-05-22', 1, 1),
('2024-05-29', 2, 1),
('2024-06-05', 3, 1),
('2024-06-12', 4, 1),
('2024-06-19', 5, 1);

INSERT INTO formation (id, module, classe, heure_debut, heure_fin) VALUES 
(11, 'Spring Boot Avancé', 'Développeurs Senior', '09:00:00', '17:00:00'),
(12, 'React.js Fundamentals', 'Développeurs Junior', '09:30:00', '16:30:00'),
(13, 'Architecture Microservices', 'Architectes', '10:00:00', '18:00:00'),
(14, 'Docker & Kubernetes', 'DevOps', '09:00:00', '17:30:00'),
(15, 'Test-Driven Development', 'Développeurs', '09:15:00', '16:45:00'),
(16, 'API REST Design', 'Développeurs Backend', '10:00:00', '17:00:00'),
(17, 'Vue.js Progressive', 'Frontend', '09:30:00', '17:30:00'),
(18, 'Database Performance', 'DBA', '09:00:00', '16:00:00'),
(19, 'Sécurité Web', 'Sécurité', '10:30:00', '18:30:00'),
(20, 'Agile & Scrum', 'Management', '09:00:00', '17:00:00');

-- 4. FACTURES
INSERT INTO facture (periode_debut, periode_fin, client_id, user_id, chemin_pdf) VALUES 
('2024-01-01', '2024-01-31', 1, 1, 'facture_janvier_2024_techcorp.pdf'),
('2024-02-01', '2024-02-28', 2, 1, 'facture_fevrier_2024_designstudio.pdf'),
('2024-03-01', '2024-03-31', 3, 1, 'facture_mars_2024_consulting.pdf'),
('2024-04-01', '2024-04-30', 4, 1, 'facture_avril_2024_formation.pdf'),
('2024-05-01', '2024-05-31', 5, 1, 'facture_mai_2024_digital.pdf'),
('2024-06-01', '2024-06-30', 1, 1, 'facture_juin_2024_techcorp.pdf');

-- 5. ASSOCIATIONS FACTURE-PRESTATIONS
-- Facture 1 (Janvier 2024) - TechCorp
INSERT INTO facture_prestations (facture_id, prestation_id) VALUES 
(1, 1),
(1, 4),
(1, 9);

-- Facture 2 (Février 2024) - Design Studio
INSERT INTO facture_prestations (facture_id, prestation_id) VALUES 
(2, 2),
(2, 6),
(2, 12),
(2, 17);

-- Facture 3 (Mars 2024) - Consulting Expert
INSERT INTO facture_prestations (facture_id, prestation_id) VALUES 
(3, 3),
(3, 8),
(3, 13),
(3, 18);

-- Facture 4 (Avril 2024) - Formation Plus
INSERT INTO facture_prestations (facture_id, prestation_id) VALUES 
(4, 5),
(4, 10),
(4, 14),
(4, 19);

-- Facture 5 (Mai 2024) - Digital Factory
INSERT INTO facture_prestations (facture_id, prestation_id) VALUES 
(5, 7),
(5, 15),
(5, 20);

