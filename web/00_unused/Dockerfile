FROM nginx

MAINTAINER IDSNA

COPY dist /usr/share/nginx/html/

COPY docker/default.conf /etc/nginx/conf.d/

COPY docker/mimes.types /etc/nginx/mimes.types
