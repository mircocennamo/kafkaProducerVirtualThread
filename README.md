# kafkaProducerVirtualThread

# By default, Tomcat in Spring Boot has a thread pool consisting of a maximum of 200 threads.
It means that If your web application simultaneously receives more than 200 HTTP requests, 
then some of them will be put in a queue waiting for available threads. 


# MISURATORE RISORSE MACCHINE 
# SPRING actuator ( performace)
# http://localhost:8081/actuator



# STEP PROPEDEUTICI 
# creare nodo kafka/zookeeper
# cd docker/docker-kafka
# docker-compose up -d

## creare nodo  zipkin / elasticsearch / kibana 
cd docker/zipkin-elastic-kibana
$ docker-compose up -d

# check status elasticsearch 
 # visualizza indici presenti in elasicsearch http://localhost:9200/_cat/indices?v&pretty 

# ZIPKIN monitora le richieste HTTP
 # console web http://localhost:9411

# KIBANA 
# console http://localhost:5601/

# GUIDA ALL'INTEGRAZIONE ZIPKIN / ELASTICKSEARCH / KIBANA  (https://logz.io/blog/zipkin-elk/)
 
# STEP 
# inviare una richiesta al producer  
# curl --location 'localhost:10001/nsis/scntt/publish/new' \
--header 'Content-Type: application/json' \
--data ' {

	"licensePlate": "EFGH",
	"latitude": "999",
	"longitude": "-45454"
}

# APRIRE ZIPKIN dovrebbe essere presente il trace della richiesta 

# curl -XGET 'localhost:9200/_cat/indices?v&pretty'
# su elastick search dovrebbe esserci l'indice zipkin 
# ES indice : yellow open   zipkin:span-2023-12-07          r-_GAXmuQLyvKsWu6knI-Q   5   1      34988            0     16.9mb         16.9mb
# su kibana creare un index pattern zipkin* come campo timestamp , usare  ‘timestamp_millis’ 
# dopo aver creato index pattern su kibana, aprire Discover tab e dovrebbero esserci le informazioni collezionate da Zipkin.

# creare le metriche
 # Average service duration
 # Using an average aggregation of the ‘duration’ field, you can create some metric visualizations that give you the average time for your services. Use a query to build the visualization # on a specific service

 #  Average duration per service over time
 # Creare una dashboard con le metriche create



## Link to view Swagger-ui
- http://localhost:10001/swagger-ui/index.html
