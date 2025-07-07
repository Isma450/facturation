-- Script de nettoyage et recréation de la base de données
-- Pour la facturation Spring Boot/Hibernate

-- Désactiver les contraintes de clés étrangères temporairement
SET session_replication_role = replica;

-- Supprimer toutes les données des tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS facture_prestations CASCADE;
DROP TABLE IF EXISTS facture CASCADE;
DROP TABLE IF EXISTS prestation CASCADE;
DROP TABLE IF EXISTS consultation CASCADE;
DROP TABLE IF EXISTS formation CASCADE;
DROP TABLE IF EXISTS entreprise CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Réactiver les contraintes de clés étrangères
SET session_replication_role = DEFAULT;

-- Supprimer les séquences si elles existent
DROP SEQUENCE IF EXISTS facture_seq CASCADE;
DROP SEQUENCE IF EXISTS prestation_seq CASCADE;
DROP SEQUENCE IF EXISTS consultation_seq CASCADE;
DROP SEQUENCE IF EXISTS formation_seq CASCADE;
DROP SEQUENCE IF EXISTS entreprise_seq CASCADE;
DROP SEQUENCE IF EXISTS users_seq CASCADE;

-- Message de confirmation
SELECT 'Base de donnees nettoyee avec succes. Redemarrez application pour recreer les tables.' as message;
