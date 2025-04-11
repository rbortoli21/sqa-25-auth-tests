FROM node:18 AS builder

WORKDIR /app

COPY package.json package-lock.json ./

ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

RUN npm install

COPY . .

RUN npm run build

FROM node:18 AS production

WORKDIR /app

COPY --from=builder /app/package.json ./
COPY --from=builder /app/package-lock.json ./
COPY --from=builder /app/.next ./.next/
COPY --from=builder /app/public ./public/

RUN npm install --only=production

EXPOSE 3000

CMD ["npm", "start"]
