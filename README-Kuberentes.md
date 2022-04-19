# Notes on Docker/Kuberentes Information 

## Building Docker image 
Using the Redhat UBI 8 / Java 11 latest image. Take a look a the Dockerfile in the Kuberentes sub-dir. It's really simple just depositing the jar in the s2i run directory. 

To build the docker image run: `docker build -t springboot-kong:latest -f kuberentes/Dockerfile .`

Update the application.properties to not use the dot-env package for now. 

* Note: cannot use latest tag on microk8s hence using local as recommended per their documentation. 
To test the image via docker run: `docker run -p 8080:8080 --name spring-kong springboot-kong:local`

## Push the image to microk8s local image cache
https://microk8s.io/docs/registry-images