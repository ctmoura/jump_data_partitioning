#!/bin/bash

docker-compose down
docker-compose up -d
rm -rf console.log 
./mvnw spring-boot:run
