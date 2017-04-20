# bitflow

Sample application using [pipeline](https://github.com/orctom/pipeline).

### How to Run
 1. Setup and run zookeeper
 2. Setup and run emqtt
 3. Config the address of zookeeper in `xxx.conf` in each module
 4. Config the address of emqtt in `producer.conf` (bitflow-producer) and `spring.conf` (bitflow-spring)
 5. Run the main class (xxxxApplication) in each module in any order

