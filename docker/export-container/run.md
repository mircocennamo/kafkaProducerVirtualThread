# EXPORT CONTAINER 
# docker container ls --all (view container ID)

# docker export f80555a6409b >  zipkin.tar
# docker export 8d391bb5de2a >  kibana.tar
# docker export 81dce157b3f1 >  elasticsearch.tar
# docker export d2c2c39f7710 >  kafka.tar
# docker export 90a140422a57 >  zookeeper.tar

# IMPORT CONTAINER
# docker import   zookeeper.tar zookeeper
# docker run zookeeper
# docker import   kafka.tar
# docker import   elasticsearch.tar
# docker import   zipkin.tar
# docker import   kibana.tar


docker import helloworld.tar my-imported-img:v1
