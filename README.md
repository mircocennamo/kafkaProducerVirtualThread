# kafkaProducerVirtualThread

# By default, Tomcat in Spring Boot has a thread pool consisting of a maximum of 200 threads.
It means that If your web application simultaneously receives more than 200 HTTP requests, then some of them will be put in a queue waiting for available threads. 