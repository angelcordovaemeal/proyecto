# Bootcamp Microservices Project

Este proyecto forma parte del Bootcamp 2026 y está compuesto por varios microservicios construidos con **Spring Boot**, **RxJava** y **MongoDB**.

---

## Arquitectura

El sistema está dividido en los siguientes microservicios:

- **customer-service**
- **account-service**
- **credit-service**
- **movement-service**

Cada microservicio está organizado por capas siguiendo el patron de arquitectura limpia:

- `infrastructure` → controllers, clients, repositories
- `application` → services y lógica de negocio
- `domain` → modelos

---

## Tecnologías utilizadas

- Maven
- Java 17
- Spring Boot 3.3.4
- RxJava
- MongoDB
- OkHttp (clients HTTP)
- JUnit 5
- Mockito
- JaCoCo (coverage)

---

## ✅ Testing y calidad

Todos los microservicios incluyen:

- ✅ Tests de **repositories**
- ✅ Tests de **application services** (reglas de negocio)
- ✅ Tests de **clients HTTP**
- ✅ Tests de **controllers**
- ✅ Cobertura de código superior al **80 %**

---

## ▶️ Ejecución del proyecto

Cada microservicio se puede ejecutar de forma independiente:

```bash
mvn spring-boot:run
```

---

## Ejecución con docker

Clonar el repositorio, abrir consola y levantar el proyecto usando la siguiente lista de comandos: 

- Levantar servicios:
```bash
docker-compose up --build
```
- Apagar y eliminar containers desplegado:
```bash
docker-compose down --remove-orphans
```
- Eliminar imagenes, dependencias y cache:
```bash
 docker system prune -af
```
- Revisar logs de un determinado servicio (ejemplo: customer service):
```bash
 docker logs customer-service
```

## Rutas disponibles al levantar los servicios

```txt
- **CONFIG SERVER**
http://localhost:8888/customer-service/default
http://localhost:8888/account-service/default
http://localhost:8888/credit-service/default
http://localhost:8888/movement-service/default

- **DISCOVER**
http://localhost:8761

- **GATEWAY**
http://localhost:8080/actuator/health

CUSTOMER (8081)
http://localhost:8081/swagger-ui.html

ACCOUNT (8082)
http://localhost:8082/swagger-ui.html

CREDIT (8083)
http://localhost:8083/swagger-ui.html

MOVEMENT (8084)
http://localhost:8084/swagger-ui.html

http://localhost:8080/customer-service/api/v1/customers
http://localhost:8080/account-service/api/v1/accounts
http://localhost:8080/credit-service/api/v1/credits
http://localhost:8080/movement-service/api/v1/movements
```

## Probar servicios con postman

- Se pueden ejecutar los requests configurados en la coleccion postman ubicada en: \documentacion\Bootcamp Proyecto.postman_collection.json
