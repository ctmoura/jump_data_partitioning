#!/bin/bash

docker-compose down
docker-compose up -d 
rm -rf jump_dp.log
./mvnw spring-boot:run 
