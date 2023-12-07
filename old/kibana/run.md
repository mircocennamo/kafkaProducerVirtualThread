# crea nodo kibana


docker network create networkKibana
docker run --name kib01 --net networkKibana -p 5601:5601 kibana:7.17.15


java -jar .\zipkin-server-3.0.0-rc0-exec.jar
http://localhost:9411/zipkin

