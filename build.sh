#!/bin/bash

echo "=== Construyendo Aplicacion Gym Management ==="

# Limpiar todo completamente
echo "Limpiando contenedores previos..."
docker-compose down

# Eliminar imagenes y volúmenes no utilizados (opcional)
docker system prune -f

# Construir Backend
echo "Construyendo Backend..."
cd backend
./mvnw clean package -Dmaven.test.skip=true -DskipTests
cd ..

# Construir Frontend LOCALMENTE para asegurar
echo "Construyendo Frontend localmente..."
cd frontend
rm -rf dist/
npm run build --prod

# Verificar que todo está correcto
if [ ! -d "dist/gym-management-frontend" ]; then
    echo "ERROR: dist/gym-management-frontend no se generó"
    ls -la dist/
    exit 1
fi

echo "Frontend construido localmente"
cd ..

# Construir con Docker
echo "Construyendo contenedores Docker..."
docker-compose build --no-cache

echo "Iniciando contenedores..."
docker-compose up -d

if [ $? -eq 0 ]; then
    echo ""
    echo "Aplicacion desplegada exitosamente"
    echo ""
    echo "URLs de la aplicacion:"
    echo "   - Frontend: http://localhost"
    echo "   - Backend API: http://localhost:8080/api"
    echo "   - MySQL: localhost:3307"
    echo ""
    echo "Comandos utiles:"
    echo "   Ver logs: docker-compose logs -f"
    echo "   Detener: docker-compose down"
    echo "   Estado: docker-compose ps"
else
    echo "Error al levantar los contenedores"
    exit 1
fi