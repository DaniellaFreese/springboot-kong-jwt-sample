FROM registry.access.redhat.com/ubi8/openjdk-11:latest

COPY target/*.jar /deployments/

COPY .okta.env /deployments/

CMD ["/usr/local/s2i/run"]