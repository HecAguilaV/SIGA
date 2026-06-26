FROM oven/bun:1-slim

WORKDIR /app

COPY package.json ./
RUN bun install --frozen-lockfile

COPY src/ src/

VOLUME /app/data

EXPOSE 9510

CMD ["bun", "run", "src/index.ts"]
