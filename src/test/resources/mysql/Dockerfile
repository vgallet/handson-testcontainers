FROM mysql:5.7.8

ENV MYSQL_ROOT_PASSWORD root_password
ENV MYSQL_DATABASE petclinic
ENV MYSQL_USER petclinic
ENV MYSQL_PASSWORD petclinic

ADD a_schema.sql /docker-entrypoint-initdb.d
ADD b_data.sql /docker-entrypoint-initdb.d
