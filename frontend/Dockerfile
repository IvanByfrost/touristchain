FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG VITE_API_URL=http://localhost:8080/api/v1
ARG VITE_MOCK_API_URL=http://localhost:3001/api
ENV VITE_API_URL=$VITE_API_URL \
    VITE_MOCK_API_URL=$VITE_MOCK_API_URL
RUN npm run build

FROM node:22-alpine
WORKDIR /app
ENV NODE_ENV=production
COPY package*.json ./
RUN npm ci --omit=dev
COPY backend ./backend
COPY --from=build /app/dist ./dist
RUN mkdir -p backend/data && chown -R node:node backend/data
USER node
EXPOSE 3001
CMD ["node", "backend/server.js"]
