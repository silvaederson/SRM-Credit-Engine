#!/bin/bash

echo "Inicializando banco de dados..."

# Aguardar PostgreSQL
until pg_isready -h localhost -U admin -d srm; do
  echo "Aguardando PostgreSQL..."
  sleep 2
done

echo "PostgreSQL está pronto!"

# Executar migrações
psql -h localhost -U admin -d srm -f /docker-entrypoint-initdb.d/init.sql

echo "Banco de dados inicializado com sucesso!"