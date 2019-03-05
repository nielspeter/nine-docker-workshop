
Docker Advanced
---

### Docker i et cluster setup

#### Hvorfor: 

* High Availability / Failover
* Scaling
* Load Balancing
* Rolling Updates
* .. og meget mere

#### Udfordringer:

* Service discovery
* Load Balancing
* Health Checks
* Storage Management
* .. og meget mere

#### Vi har brug for et 'værktøj' som kan hjælpe os med dette!

### Container Orchestrator

Der er primært Docker Swarm og Kubernetes hvor kubernetes et klar den største.

* [Kubernetes] (https://kubernetes.io/)
* [Docker Swarm](https://docs.docker.com/engine/swarm/)

![alt text](https://www.docker.com/sites/default/files/d8/styles/large/public/2018-11/swarm%2Bkubernetes.png "Docker Swarm og Kubernetes")


## Docker Swarm - Oprette et cluster

### Online 
[https://labs.play-with-docker.com/](https://labs.play-with-docker.com/)


### Docker Machine 
[https://docs.docker.com/machine/install-machine/](https://docs.docker.com/machine/install-machine/)


### alias til Docker Machine - hvis man vil :-)

```
alias docm="docker-machine"
```

Gem

```
echo 'alias docm="docker-machine"' >> ~/.bash_profile
```

### Opret noder
```
docker-machine create -d virtualbox node1
docker-machine create -d virtualbox node2
```

### List noder
```
docker-machine ls
```

### Setup Docker CLI til at tale med node1
```
eval $(docker-machine env node1)
```

### Set node1 i Swarm mode
```
docker swarm init

Response:
Error response from daemon: could not choose an IP address to advertise since this system has multiple addresses on different interfaces...
```

```
docker swarm init --advertise-addr 192.168.99.100

Response:
docker swarm join --token <token> 192.168.99.100:2377
```

Kopiere token url

### Setup Docker CLI til at tale med node2
```
eval $(docker-machine env node2)
```

```
docker swarm join --token <token> 192.168.99.100:2377
```

### Setup Docker CLI til at tale med node1
```
eval $(docker-machine env node1)
```

### Se nodes 
```
docker node ls
```

###  Setup visualizer - hjælper med at forstå hvad der sker i clusteret
```
docker service create \
  --detach=true \
  --name=visualizer \
  --publish=8000:8080/tcp \
  --constraint=node.role==manager \
  --mount=type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
  dockersamples/visualizer
```

### find node1 ip-adresse
```
docker-machine ip node1
```

### Open visualizer i en browser
```
http://192.168.99.100:8000/
```

## Services
I en ny terminal

```
watch docker service ls
```

Opret ny service

```
docker service create --name hello nginxdemos/hello:0.1
```

Se services

```
watch docker service ls
```

Inspect service

```
docker service inspect --pretty hello
```

Update service med port

```
docker service update --publish-add published=8080,target=80 hello
```

Scale service

```
docker service scale hello=2
```

List services i swarm

```
docker service ps hello
```

Se services på noder

```
docker node ps node1
docker node ps node2
```

### Load Balancing 

```
curl http://192.168.99.100:8080/ | grep name
```

![alt text](https://docs.docker.com/engine/swarm/images/ingress-routing-mesh.png "Docker Swarm og Kubernetes")


## Docker Swarm Nodes

### promote worker node til manager node
```
docker node promote node2
```

### lukke node og se hvad der sker med service
```
docker-machine stop node2
```

### se node1
```
docker node ps node1
```

### genstart node2
```
docker-machine start node2
```

### se node1 + node2
```
docker node ps node1
docker node ps node2
```

### lave update 
```
docker service update hello
```

### se node1 + node2
```
docker service ps hello
docker node ps node1
docker node ps node2
```

### lave update force (rebalancing)
```
docker service update --force hello 
```

### se node1 + node2
```
docker service ps hello
docker node ps node1
docker node ps node2
```

### drain node
```
docker node update --availability drain node1
docker node ls
```

### activate node
```
docker node update --availability active node1
docker node ls
```


## Rolling update
```
docker service update --update-delay 10s --image nginxdemos/hello hello 
```

### Rollback
```
docker service rollback hello
```

### Healthcheck
```
docker run --help | grep health
```

## Constraint

### label constraint - prøv at starte redis med constraint (starter ikke)
```
docker service create --replicas 1 --name redis --constraint node.labels.datanode==true redis
```

### Giv node1 label 'datanode'
```
docker node update --label-add datanode=true node1
```

```
docker node inspect --pretty node1
```

### Nu er Redis startet
```
docker services ls
```

## Config

docker config create
docker config inspect
docker config ls
docker config rm

config from file:

```
docker config create proxy nginx.conf
```

config from stdin:

```
echo "This is a config" | docker config create my-config -
```

### opret config

```
echo "This is a config" | docker config create my-config -
```

### Lav en service som bruger config
```
docker service create --name busybox --config src=my-config,target=/somewhere/my-config.txt busybox top
```

### Se hvordan config er mountet i service

Find container navn

```
docker ps
```

### Kør 'cat' i container

```
docker exec -it busybox.1.03pvvevd3q9d3xi5i232j3s2k cat /somewhere/my-config.txt
```

### Config kan ikke slettes når den bruges

```
docker config rm my-config
```

### Fjern service og config

```
docker service rm busybox
docker config rm my-config
```

## Secrets

Opret secret

```
echo "This is a secret" | docker secret create my_secret_data -
```

### Opret service som bruger secret

```
docker service  create --name busybox --secret my_secret_data busybox top
```

### læse værdi

```
docker exec -it busybox.1.03pvvevd3q9d3xi5i232j3s2k cat /run/secrets/my_secret_data
```

### Se mount

```
docker exec -it busybox.1.x8ocfslgkjlgefhy3tb39n85z mount | grep secret
```

## logs

```
docker service logs redis
```

## Docker Stack

stack.yml 

```
version: "3.7"

services:

  hello-service:
    image: nginxdemos/hello:latest
    deploy:
      replicas: 2
    ports:
      - "8090:80"
    secrets:
      - my_secret_data
      
secrets:
  my_secret_data:
    external: true
```

docker stack ls

docker stack deploy --compose-file stack.yml hello-stack

docker stack ls

docker stack services hello-stack

docker stack rm hello-stack


## Kubernetes - Minikube

Start minikube cluster

```
minikube start
```

Start pod

```
kubectl run hello-minikube --image=gcr.io/google_containers/echoserver:1.4 --port=8080
```

Eksponere deployment

```
kubectl expose deployment hello-minikube  --type=NodePort
```

Hent pods

```
kubectl get pod
```

Hent url

```
curl $(minikube service hello-minikube --url)
```

Slet deployemt

```
kubectl delete deployment hello-minikube
```

Stop Minikube

```
minikube stop
```
