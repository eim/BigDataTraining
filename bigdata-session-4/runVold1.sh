#!/bin/bash
VOL_ID=$(docker ps|grep vold-1|awk '{print $1}')
docker exec -i -t ${VOL_ID} /bin/bash

