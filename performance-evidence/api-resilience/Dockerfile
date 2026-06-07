FROM node:20-alpine
WORKDIR /app
COPY package.json .
RUN npm install --production
COPY resilience-server.js .
EXPOSE 3001
CMD ["node", "resilience-server.js"]
