
Docker på Mac og Windows
---

Kører i en egen lille Linux VM da Mac er BSD og Windows er, øh Windows. Så når man kører Docker kommandoer så er det mod den lille VM. 

Man kan dog connect til denne linux VM direkte på følgende måde:

```
$ docker run --rm -it --privileged --pid=host ubuntu nsenter -t 1 -m -u -n -i sh
```

På Docker hosten er Docker root:

```
/var/lib/docker/
```
```
# ls /var/lib/docker/
builder  containerd  containers  image  network  overlay2  plugins  runtimes  swarm  tmp  trust  volumes
```

Docker CLI
---

Demo CLI Help

```
$ docker --help
```

Docker API / REST
---
[Docker API/REST](https://docs.docker.com/engine/api/v1.24/)

Docker info

```
$ curl --unix-socket /var/run/docker.sock http://localhost/info | jq
$ curl --unix-socket /var/run/docker.sock http://localhost/info | jq -r '.Containers'
```

List alle containers

```
$ curl --unix-socket /var/run/docker.sock http://localhost/containers/json | jq
```

Docker Python
---
[Docker Python](https://github.com/docker/docker-py)

Kør en container

```
$ python
>>> import docker
>>> client = docker.from_env()
>>> client.containers.run("hello-world")
```

Docker Web GUI
---
[portainer.io](https://portainer.io)

```
$ docker container run -d -p 9000:9000 --name portainer -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer
```


Grundlæggende Docker
---

Søg efter et image i Docker hub

```
$ docker search hello
```

Hent et image fra Docker Hub

```
$ docker pull nginxdemos/hello
```

List lokale images

```	
$ docker image ls
```

Inspekterer image

```
$ docker image inspect nginxdemos/hello
```

Lave en container fra image

```
$ docker create -p 80:80 --name hello nginxdemos/hello
```
 
Se den nye container (køre ikke)

```
$ docker ps -a
```

Start container

```
$ docker start hello
```

Inspektere container

```
$ docker inspect hello
```

Åben http://localhost i en browser eller 

```
$ curl -v http://localhost
```

Stoppe container

```
$ docker stop hello
```

Slette container

```
$ docker rm hello
```

Slette image fra lokal maskine

```
$ docker rmi nginxdemos/hello
```



Container uden bagrundsprocess
---

[Docker Run doc](https://docs.docker.com/engine/reference/run/)

Lave en container fra image og kør en kommando og slet container

```
$ docker run -it --rm ubuntu ls -la
$ docker run -it --rm ubuntu sh
```



Arbejde med en kørende container
---
[Docker Run doc](https://docs.docker.com/engine/reference/run/)

Start en container med bagrundsprocess

```
$ docker run -d --name hello -p 80:80 nginxdemos/hello
```

Se logfiler

```
$ docker logs -f hello
```

Se stats

```
$ docker stats hello
```

Køre endnu en process i containeren

```
$ docker exec -it hello sh
```

I container:

```
# top
Ctrl-d
```
Udenfor container

```
$ docker container top hello
```

Opret en file

```
$ docker exec -it hello touch a.txt
$ docker exec -it hello ls
```

Se diff imellem container filsystem og image filsystem

```
$ docker container diff hello
A /a.txt							<-- added
C /var/cache/nginx                  <-- changed
A /var/cache/nginx/client_temp
A /var/cache/nginx/fastcgi_temp
A /var/cache/nginx/proxy_temp
A /var/cache/nginx/scgi_temp
A /var/cache/nginx/uwsgi_temp
C /var/run							
A /var/run/nginx.pid
```

Kopiere en fil fra container til lokale maskine

```
$ docker container cp hello:a.txt .
```

Kopiere en fil fra lokale maskine til container

```
$ docker container cp a.txt hello:a.txt
```

Stop

```
$ docker stop hello
```

Slet container (data er væk)

```
$ docker rm hello
```


Bind mounts (mount lokal directory ind i container)
---
[Docker Bind mounts doc](https://docs.docker.com/storage/bind-mounts/)

Opret en file på lokale maskine (html/index.html)

```
$ mkdir html && echo "<html><title>min side</title><body><h2>Nine Docker Workshop</h2></body></html>" > html/index.html
```

Start container og peg på html mappen

```
$ docker run --name nginx -p 80:80 --rm -it -v "$(pwd)"/html:/usr/share/nginx/html:ro nginx
```

Lav ændring i index.html på din lokale maskine og gem og se at ændringen slår igennem med det samme


Volumes
---
[Docker Volume doc](https://docs.docker.com/storage/volumes/)

List alle volumes

```
$ docker volume ls
```

Opret en ny volume 

```
$ volume create html
```

Start container og peg på html volume

```
$ docker run --rm -p 80:80 -it -v html:/usr/share/nginx/html:ro nginx
Ctrl-d
```

Container er nu slettet, men volume findes stadigvæk

```
$ docker volume ls
```

Start en ny container

```
$ docker run --rm -p 80:80 -it -v html:/usr/share/nginx/html:ro nginx
Ctrl-d
```

Dele data med en volume (start 2 nginx containers)

```
$ docker run --name nginx1 -p 81:80 -it -d -v html:/usr/share/nginx/html:ro nginx
$ docker run --name nginx2 -p 82:80 -it -d -v html:/usr/share/nginx/html:ro nginx
```

Stop og slet containers

```
$ docker stop nginx1 nginx2 && docker rm nginx1 nginx2
```

Slet volume

```
$ docker volume rm html
```



Byg dit eget image
---

Lave en Dockerfile

```
$ mkdir nginx && cd nginx
```

Lave html

```
$ mkdir html && echo "<html><title>min side</title><body><h2>Nine Docker Workshop</h2></body></html>" > html/index.html
```

Lav dockerfile

```
$ vi Dockerfile
FROM nginx
COPY html /usr/share/nginx/html
```

Byg docker image fra Dockerfile

```
$ docker build -t nine/nginx:latest -t nine/nginx:1.0 .
```

Inspect det nye image (ligger kun lokalt)

```
$ docker image inspect nine/nginx
```

Se build history

```
$ docker image history nine/nginx
```

Start en container på bagrund af image

```
$ docker run -p 80:80 -d --rm --name nine-nginx nine/nginx
```

Se indehold af den kopiered fil inde i containeren

```
$ docker exec -it nine-nginx cat /usr/share/nginx/html/index.html
```


Push dig eget image til Docker Hub
---
	
1) registrer dig på docker hub (https://hub.docker.com)

(prøv selv!)


Container netværks kommunikation
------------------------

[Docker Network doc](https://docs.docker.com/network/)

Brug lokale 'host' netværk (uden -p denne gang)

```
$ docker run --rm --network=host nginxdemos/hello
```

Opret et netværk

```
$ docker network create redis_network
```

Start Redis og brug det nye netværk (uden -p denne gang)

```
$ docker run --name min-redis --rm --network=redis_network redis
```

Snak med 'min-redis' via Redis klient (navnet på container er 'host' navnet - docker dns)

```
$ docker run --name min-redis-cli -it --rm --network=redis_network redis redis-cli -h min-redis -p 6379
```

Inspect redis_network - kan se de 2 containers

```
$ docker network inspect redis_network
``` 
eller 

```
$ docker network inspect redis_network | jq -r '.[0].Containers'
```
```
"Containers": {
            "68f254d20749ada6133553d6035d541ee109e1ca8c637f6da7bf72390d990b08": {
                "Name": "min-redis-cli",
                "EndpointID": "080c4e9ed9bc7d2dc23c1218e89afc216324d0a09912f68d817394573e3b5fe0",
                "MacAddress": "02:42:ac:12:00:03",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            },
            "e93b41af7bd7c3b86629ed7f89e02caa1b4e331074c07361b77d527cb9d7a603": {
                "Name": "min-redis",
                "EndpointID": "b79907c785a25d1195e27c61f6bd114f06372ca5373eac475da8dd1d22926e04",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        }
 ....
}
```

Prøve at lave et DNS lookup på 'min-redis' fra 'min-redis-cli' (skal dog først installere dnsutils)

```
$ docker exec -it min-redis-cli /bin/bash
```

I container 'min-redis-cli'

```
# apt-get update && apt-get install dnsutils
# exit
```

Prøv at lave en lookup 

```
$ docker exec -it min-redis-cli nslookup min-redis
```

Prøv at se mount i 'min-redis-cli'

```
$ docker exec -it min-redis-cli mount
/dev/sda1 on /etc/resolv.conf type ext4 (rw,relatime,stripe=1024,data=ordered)
/dev/sda1 on /etc/hostname type ext4 (rw,relatime,stripe=1024,data=ordered)
/dev/sda1 on /etc/hosts type ext4 (rw,relatime,stripe=1024,data=ordered)
```
```
$ docker exec -it min-redis-cli cat /etc/resolv.conf
nameserver 127.0.0.11 <- docker dns
```

```
$ docker exec -it min-redis-cli cat /etc/hostname
66b4366f8161
```
```
$ docker exec -it min-redis-cli cat /etc/hosts
172.18.0.3	66b4366f8161
```

Du kan sætte dit hostname med '-h <navn>'

Slet network

```
$ docker network rm redis_network
```


Docker Events
---
[Docker Events doc](https://docs.docker.com/engine/reference/commandline/events/)

Lytte til Docker event 

```
$ curl --unix-socket /var/run/docker.sock http://localhost/events
```

```
$ docker run -p 80:80 -d --rm --name nine-nginx nine/nginx
$ docker stop nine-nginx
```


Docker Compose
---

Gå til 

```
$ cd docker-compose/nginx-1
```

Tjek om docker-compose file er valid


```
$ docker-compose config
```

Kør services

```
$ docker-compose up -d
Creating network "nginx1_default" with the default driver
Pulling nginx (nginx:latest)...
latest: Pulling from library/nginx
f2aa67a397c4: Pull complete
3c091c23e29d: Pull complete
4a99993b8636: Pull complete
Digest: sha256:0fb320e2a1b1620b4905facb3447e3d84ad36da0b2c8aa8fe3a5a81d1187b884
Status: Downloaded newer image for nginx:latest
Creating nginx1_nginx_1 ... 
Creating nginx1_nginx_1 ... done
```

List containers

```
$ docker-compose ps
```

Se logs 

```
$ docker-compose logs
```

Stop alle services

```
$ docker-compose stop
```

Stop alle services og slet alle networks, volumes, containers

```
$ docker-compose down
Stopping nginx1_nginx_1 ... done
Removing nginx1_nginx_1 ... done
Removing network nginx1_default
```

Docker Machine 
---

Docker Machine Drivers

* Amazon Web Services
* Microsoft Azure
* Digital Ocean
* Exoscale
* Google Compute Engine
* Generic
* Microsoft Hyper-V
* OpenStack
* Rackspace
* IBM Softlayer
* Oracle VirtualBox
* VMware vCloud Air
* VMware Fusion
* VMware vSphere
* VMware Workstation (unofficial plugin, not supported by Docker)
* Grid 5000 (unofficial plugin, not supported by Docker)

Opret en 'maskine' hos Digital Ocean

```
docker-machine create --driver digitalocean --digitalocean-access-token <token> --digitalocean-region=ams3 --digitalocean-size=4gb node1
```

Opret en 'maskine' (vm) lokalt via virtualbox 

[Virtualbox download](https://www.virtualbox.org/wiki/Downloads)

```
$ docker-machine create --driver virtualbox node1
Running pre-create checks...
Creating machine...
(node1) Copying /Users/nps/.docker/machine/cache/boot2docker.iso to /Users/nps/.docker/machine/machines/node1/boot2docker.iso...
(node1) Creating VirtualBox VM...
(node1) Creating SSH key...
(node1) Starting the VM...
(node1) Check network to re-create if needed...
(node1) Waiting for an IP...
Waiting for machine to be running, this may take a few minutes...
Detecting operating system of created instance...
Waiting for SSH to be available...
Detecting the provisioner...
Provisioning with boot2docker...
Copying certs to the local machine directory...
Copying certs to the remote machine...
Setting Docker configuration on the remote daemon...
Checking connection to Docker...
Docker is up and running!
To see how to connect your Docker Client to the Docker Engine running on this virtual machine, run: docker-machine env node1
```

Få komando til at forbinde til node1

```
$ docker-machine env node1
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.103:2376"
export DOCKER_CERT_PATH="/Users/nps/.docker/machine/machines/node1"
export DOCKER_MACHINE_NAME="node1"
# Run this command to configure your shell: 
# eval $(docker-machine env node1)
```
```
$ eval $(docker-machine env node1)
```

Nu kommunikere Docker CLI med 'node1'

Unset ENV for 'node1' så vi igen snakker med den lokale docker host

```
$ eval $(docker-machine env -u)
```

List maskiner

```
$ docker-machine ls
```

Slet en maskine

```
$ docker-machine rm node1
```
