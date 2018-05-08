# Nine Docker Workshop

For dig som gerne vil vide noget mere om Docker.

## Som en lille appetizer på hvad Docker kan hjælpe med, så er her en lille demo
 
Denne demo består af:
*	MySql
*	Elasticsearch
*	Kibana
*	Grails applikation (nine-nyt) 

Det eneste du skal gøre er: 
* Installer Docker https://store.docker.com/search?type=edition&offering=community
* OBS! Se til at Docker har min 4GB ram tildelt. (På Mac er det under Docker -> Preferences -> Advanced)
* Køre følgende kommandoer

### Uden GIT
Hent og udpak filen https://github.com/nielspeter/nine-docker-workshop/archive/master.zip
```
> cd nine-docker-workshop
> docker-compose up -d
```
### Med GIT
```
> git clone git@github.com:nielspeter/nine-docker-workshop.git 
> cd nine-docker-workshop
> docker-compose up -d
```
* Åben din browser og skriv URL http://localhost:8080 (obs! Det kan tage 10-20 sekunder før applikationen er klar)
