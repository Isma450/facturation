-- Script de fixtures pour la base de données facturation
-- Données de test complètes - Version corrigée avec IDs auto-générés

-- 1. UTILISATEURS (L'utilisateur ID=1 existe déjà)
-- Ajouter seulement un utilisateur admin supplémentaire
INSERT INTO users (username, password_hash, role, adresse) VALUES 
('admin', '$2a$10$xn3LI/AjqicFYZFruBbHgOhqpDpPx5uhNx4EEPZ4fKhCt5OsOA7HW', 'ADMIN', '456 Avenue de la Gestion, 69001 Lyon');

-- Réinitialiser la séquence pour les utilisateurs
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 2. ENTREPRISES CLIENTES
INSERT INTO entreprise (nom, adresse, email, siret) VALUES 
('TechCorp Solutions', '789 Boulevard de l''Innovation, 75012 Paris', 'contact@techcorp-solutions.fr', '12345678901234'),
('Design Studio Pro', '321 Rue Créative, 13001 Marseille', 'hello@designstudio-pro.com', '56789012345678'),
('Consulting Expert', '654 Avenue Business, 33000 Bordeaux', 'info@consulting-expert.fr', '98765432109876'),
('Formation Plus', '987 Place de l''Éducation, 59000 Lille', 'contact@formation-plus.fr', '11223344556677'),
('Digital Factory', '147 Rue du Numérique, 67000 Strasbourg', 'team@digital-factory.eu', '55443322110099');

-- 3. PRESTATIONS DIVERSES
INSERT INTO prestation (date, entreprise_id, user_id) VALUES 
-- Prestations pour TechCorp Solutions (ID: 1)
('2025-01-15', 1, 1),  -- Consultation
('2025-01-22', 1, 1),  -- Consultation
('2025-02-05', 1, 1),  -- Formation
-- Prestations pour Design Studio Pro (ID: 2)
('2025-01-18', 2, 1),  -- Consultation
('2025-01-25', 2, 1),  -- Consultation
('2025-02-08', 2, 1),  -- Formation
-- Prestations pour Consulting Expert (ID: 3)
('2025-01-20', 3, 1),  -- Consultation
('2025-02-03', 3, 1),  -- Consultation
-- Prestations pour Formation Plus (ID: 4)
('2025-01-12', 4, 1),  -- Formation
('2025-01-26', 4, 1),  -- Formation
('2025-02-09', 4, 1),  -- Formation
-- Prestations pour Digital Factory (ID: 5)
('2025-01-30', 5, 1),  -- Consultation
('2025-02-12', 5, 1);  -- Consultation

-- 4. CONSULTATIONS (TJM variés) - Utilisation d'une approche dynamique
-- D'abord, créons une table temporaire pour mapper les prestations
WITH prestation_mapping AS (
    SELECT p.id, p.entreprise_id, p.date,
           ROW_NUMBER() OVER (ORDER BY p.entreprise_id, p.date) as ordre
    FROM prestation p
    ORDER BY p.entreprise_id, p.date
)
INSERT INTO consultation (id, tjm) 
SELECT p.id,
       CASE 
           WHEN p.entreprise_id = 1 AND p.date = '2025-01-15' THEN 650.00  -- TechCorp - Consultation architecture
           WHEN p.entreprise_id = 1 AND p.date = '2025-01-22' THEN 750.00  -- TechCorp - Audit technique
           WHEN p.entreprise_id = 2 AND p.date = '2025-01-18' THEN 580.00  -- Design Studio - Consulting UX
           WHEN p.entreprise_id = 2 AND p.date = '2025-01-25' THEN 620.00  -- Design Studio - Stratégie digitale
           WHEN p.entreprise_id = 3 AND p.date = '2025-01-20' THEN 700.00  -- Consulting Expert - Audit organisationnel
           WHEN p.entreprise_id = 3 AND p.date = '2025-02-03' THEN 680.00  -- Consulting Expert - Conseil stratégique
           WHEN p.entreprise_id = 5 AND p.date = '2025-01-30' THEN 590.00  -- Digital Factory - Consultation DevOps
           WHEN p.entreprise_id = 5 AND p.date = '2025-02-12' THEN 610.00  -- Digital Factory - Audit sécurité
       END as tjm
FROM prestation p
WHERE (p.entreprise_id = 1 AND p.date IN ('2025-01-15', '2025-01-22'))
   OR (p.entreprise_id = 2 AND p.date IN ('2025-01-18', '2025-01-25'))
   OR (p.entreprise_id = 3 AND p.date IN ('2025-01-20', '2025-02-03'))
   OR (p.entreprise_id = 5 AND p.date IN ('2025-01-30', '2025-02-12'));

-- 5. FORMATIONS (Horaires et contenus variés)
INSERT INTO formation (id, heure_debut, heure_fin, classe, module) 
SELECT p.id,
       CASE 
           WHEN p.entreprise_id = 1 AND p.date = '2025-02-05' THEN '09:00'::time
           WHEN p.entreprise_id = 2 AND p.date = '2025-02-08' THEN '14:00'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-12' THEN '09:30'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-26' THEN '13:30'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-02-09' THEN '10:00'::time
       END as heure_debut,
       CASE 
           WHEN p.entreprise_id = 1 AND p.date = '2025-02-05' THEN '17:00'::time
           WHEN p.entreprise_id = 2 AND p.date = '2025-02-08' THEN '18:00'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-12' THEN '12:30'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-26' THEN '17:30'::time
           WHEN p.entreprise_id = 4 AND p.date = '2025-02-09' THEN '16:00'::time
       END as heure_fin,
       CASE 
           WHEN p.entreprise_id = 1 AND p.date = '2025-02-05' THEN 'Développeurs Senior'
           WHEN p.entreprise_id = 2 AND p.date = '2025-02-08' THEN 'Designers'
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-12' THEN 'Managers'
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-26' THEN 'Équipe Technique'
           WHEN p.entreprise_id = 4 AND p.date = '2025-02-09' THEN 'Développeurs Junior'
       END as classe,
       CASE 
           WHEN p.entreprise_id = 1 AND p.date = '2025-02-05' THEN 'Spring Boot Avancé'
           WHEN p.entreprise_id = 2 AND p.date = '2025-02-08' THEN 'Figma & Design System'
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-12' THEN 'Gestion de Projet Agile'
           WHEN p.entreprise_id = 4 AND p.date = '2025-01-26' THEN 'Architecture Microservices'
           WHEN p.entreprise_id = 4 AND p.date = '2025-02-09' THEN 'Initiation React.js'
       END as module
FROM prestation p
WHERE (p.entreprise_id = 1 AND p.date = '2025-02-05')
   OR (p.entreprise_id = 2 AND p.date = '2025-02-08')
   OR (p.entreprise_id = 4 AND p.date IN ('2025-01-12', '2025-01-26', '2025-02-09'));

-- Message de confirmation
SELECT 'Fixtures insérées avec succès!' as message,
       (SELECT COUNT(*) FROM users) as nb_users,
       (SELECT COUNT(*) FROM entreprise) as nb_entreprises,
       (SELECT COUNT(*) FROM prestation) as nb_prestations,
       (SELECT COUNT(*) FROM consultation) as nb_consultations,
       (SELECT COUNT(*) FROM formation) as nb_formations;
