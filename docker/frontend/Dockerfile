FROM nginx:stable-alpine AS production-stage
COPY /dist/ /opt/easton-portal
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 7070

CMD ["nginx", "-g", "daemon off;"]