#!/bin/bash

DB_USER="goldentimer"
DB_PASS="goldentimer123"
DB_NAME="goldentimer"

echo "[i] Creando usuario $DB_USER..."
psql postgres -tc "SELECT 1 FROM pg_roles WHERE rolname = '$DB_USER'" | grep -q 1 || \
psql postgres -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASS';"

echo "[i] Creando base de datos $DB_NAME..."
psql postgres -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | grep -q 1 || \
psql postgres -c "CREATE DATABASE $DB_NAME OWNER $DB_USER;"

psql postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"

echo "[i] Creando tablas en $DB_NAME..."
psql -U $DB_USER -d $DB_NAME <<EOF
CREATE TABLE IF NOT EXISTS "user" (
  user_id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
  customer_id SERIAL PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS projects (
  project_id SERIAL PRIMARY KEY,
  project_name VARCHAR(100) NOT NULL,
  customer_id INTEGER REFERENCES customer(customer_id)
);

CREATE TABLE IF NOT EXISTS time_entry (
  time_entry_id SERIAL PRIMARY KEY,
  from_time TIMESTAMP NOT NULL,
  to_time TIMESTAMP,
  project_id INTEGER REFERENCES projects(project_id),
  user_id INTEGER REFERENCES "user"(user_id)
);
EOF

echo "[i] Base de datos y tablas creadas correctamente."
