# Frontend/Dockerfile - VERSIÓN SIMPLIFICADA
FROM node:20-alpine as build

WORKDIR /app

# Copiar solo los archivos necesarios para instalar dependencias
COPY package*.json ./
RUN npm install

# Copiar el resto del código
COPY . .

# Construir la aplicación
RUN npm run build --prod

# Verificar que la carpeta existe
RUN ls -la dist/gym-management-frontend/

# Etapa de producción
FROM nginx:alpine

# Copiar archivos construidos
COPY --from=build /app/dist/gym-management-frontend /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80