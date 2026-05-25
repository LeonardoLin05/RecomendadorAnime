## Requisitos:

- Maven
- Java 11
- Cuenta de Groq

## Instalación:
- ### Linux/Mac:
```
git clone https://github.com/LeonardoLin05/RecomendadorAnime.git
mvn install:install-file -Dfile=lib/jade.jar -DgroupId=jade -DartifactId=jade -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/commons-codec-1.3.jar -DgroupId=commons-codec -DartifactId=commons-codec -Dversion=1.3 -Dpackaging=jar
```
- ### Windows:
```
git clone https://github.com/LeonardoLin05/RecomendadorAnime.git
mvn install:install-file -Dfile=lib\jade.jar -DgroupId=jade -DartifactId=jade -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\commons-codec-1.3.jar -DgroupId=commons-codec -DartifactId=commons-codec -Dversion=1.3 -Dpackaging=jar
```
## Tras instalación
1. Es necesario generar una API key de [Groq](https://console.groq.com/keys), para lo cual es necesario tener una cuenta.
2. En el directorio donde se encuentra la carpeta src crear un archivo .env y pegar la API key dentro de la siguiente manera:
```
GROQ_API_KEY=tu_api_key
```
## Ejecución: 
Desde el directorio donde se encuentra situado la carpeta src
```
mvn clean javafx:run
```
## Ejemplos de prueba para ejecutar la práctica:
- Quiero ver animes recientes con episodios cortos de unos 20 minutos
- Quiero ver peliculas de romance con una protagonista femenina y que sean tristes
- Quiero ver animes de deportes que traten sobre boxeo con episodios cortos y que las series sean antiguas
- Quiero ver animes como naruto
- Quiero ver animes de acción que traten los temas de la caza de demonios, a poder ser recientes

## Diagrama de la arquitectura

<img width="1280" height="720" alt="Diagrama_arquitectura" src="https://github.com/user-attachments/assets/d27378cd-a1ae-4df6-aadb-3a63c86587d7" />

## Uso de IA
Se hizo uso de IA para entender como hacer uso del entorno IntelliJ y depuración de errores.
