FROM mysql:5.7.8

ENV MYSQL_ROOT_PASSWORD root_password
ENV MYSQL_DATABASE petclinic
ENV MYSQL_USER petclinic
ENV MYSQL_PASSWORD petclinic

ADD mysql/a_schema.sql /docker-entrypoint-initdb.d
ADD mysql/b_data.sql /docker-entrypoint-initdb.d
