# ./frontend/Dockerfile

# Build stage
FROM node:24-alpine AS builder

WORKDIR /app

# Enable corepack so the pnpm version pinned in package.json is used
RUN corepack enable

COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile

COPY . .

RUN pnpm run build

# Production stage
FROM nginx:alpine

# Copy static files
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy env.js for variables
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
