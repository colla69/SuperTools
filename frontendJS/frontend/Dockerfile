# base image
FROM node:12.2.0-alpine as build

WORKDIR /app

# install and cache app dependencies
COPY package.json /app/package.json
RUN npm install
RUN npm install @vue/cli@3.7.0 -g
COPY . .
#CMD ["npm", "run", "serve"]
RUN npm run build

FROM nginx
COPY  --from=build /dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]