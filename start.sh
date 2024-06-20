#!/bin/sh
su - root -c 'service postgresql start'
sleep 10
su - postgres -c 'psql -c "CREATE DATABASE prac2;"'
su - postgres -c 'psql -d prac2 < /app/prac2.sql'
exec java -jar /app/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar