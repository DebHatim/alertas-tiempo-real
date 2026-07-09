# 1 - Construir estaticos
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Compilar el proyecto
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# 2 - Servir con Nginx
FROM nginx:alpine

# Copiar los archivos compilados a la carpeta que sirve Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar una configuración basica si fuera necesario, o usamos la de por defecto
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]