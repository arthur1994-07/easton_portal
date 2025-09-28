#!/bin/bash

cp -r ../backend/build/libs/easton-portal-0.0.1.jar ./easton-portal/backend/
cp -r ../frontend/dist/ ./easton-portal/frontend/
cp -r docker-compose.yaml ./easton-portal/

tar -czf easton-portal.tar.gz easton/
