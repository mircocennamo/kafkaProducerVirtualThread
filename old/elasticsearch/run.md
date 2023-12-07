# crea nodo elasticsearch


docker network create networkKibana



docker run --name es01 --net networkKibana -p 127.0.0.1:9200:9200 -p 127.0.0.1:9300:9300 -it -m 1GB -e "discovery.type=single-node" --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:7.17.15

docker pull docker.elastic.co/elasticsearch/elasticsearch:8.11.1-amd64

docker run --name es01 --net networkKibana -p 127.0.0.1:9200:9200 -p 127.0.0.1:9300:9300 -it -m 1GB -e "discovery.type=single-node" --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:8.11.1-amd64

docker run --name kib01 --net networkKibana -p 5601:5601 kibana:7.17.15




----------------------------------------
docker run --name es01 --net networkKibana -p 127.0.0.1:9200:9200 -p 127.0.0.1:9300:9300 -it -m 1GB -e "discovery.type=single-node" --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:8.11.1-amd64


docker run --name kib01 --net networkKibana -p 5601:5601 kibana:8.11.1

# test
http://localhost:9200/
