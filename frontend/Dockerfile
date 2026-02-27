# Stage build 
FROM node:18 AS build
WORKDIR /app

# Copy package files first for caching
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

# Stage runtime nginx
FROM nginx:1.25
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
