# ./frontend/Dockerfile

# Build stage
FROM node:20-alpine as builder

WORKDIR /app

# Declare build arguments
ARG VITE_BACKEND_URL
ARG VITE_FRONTEND_URL

# Set environment variables para Vite (se usan en npm run build)
ENV VITE_BACKEND_URL=$VITE_BACKEND_URL
ENV VITE_FRONTEND_URL=$VITE_FRONTEND_URL

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
