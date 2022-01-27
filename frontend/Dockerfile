FROM node:14

WORKDIR /workspace/app
COPY package.json .
RUN npm install
COPY . .
CMD npm start