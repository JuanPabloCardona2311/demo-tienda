# Demo-Tienda 🛒

**Demo-Tienda** es una aplicación ejemplo de tienda online desarrollada en Java Spring Boot. Incluye gestión de clientes, productos, ventas, y componentes de analítica que usan MongoDB y Redis para demostraciones. Este README explica cómo arrancar, probar y entender el proyecto rápidamente.

---

## Características

- Interfaz web para catálogo y proceso de compra.
- API REST para operaciones de compras.
- Persistencia principal con Spring Data JPA (H2/MySQL según configuración).
- Integración con MongoDB para analítica histórica.
- Caché / sesión simple en Redis para pruebas.

## Stack tecnológico

- Java 17+
- Spring Boot
- Maven (incluye `mvnw` / `mvnw.cmd`)
- MongoDB (opcional, para analytics)
- Redis (opcional, ejemplo de cache)

## Requisitos

- JDK 17 o superior instalado
- Git (repo ya clonado)
- (Opcional) Docker para levantar MongoDB/Redis localmente

## Instalación rápida (Windows)

1. Clona el repositorio (ya debería estar clonado):

```powershell
git clone <repo-url>
cd "Demo-Tienda"
```

2. Compilar con Maven Wrapper:

```powershell
.\mvnw.cmd -DskipTests package
```

3. Ejecutar la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
# o ejecutar el JAR:
java -jar target\demo-*.jar
```

La aplicación arranca por defecto en http://localhost:8080

## Ejecutar tests

```powershell
.\mvnw.cmd test
```

## Configuración para desarrollo (opcional)

- MongoDB: configura `spring.data.mongodb.uri` en `src/main/resources/application.properties` o usa Docker:

```powershell
docker run -d -p 27017:27017 --name mongodb mongo:6
```

- Redis:

```powershell
docker run -d -p 6379:6379 --name redis redis:7
```

## Rutas importantes y estructura del proyecto

Estructura principal (resumida):

```
src/main/java/com/example/demo/
  ├─ DemoApplication.java
  ├─ config/ (Seguridad, DataLoader)
  ├─ domain/ (entidades: Cliente, Producto, Venta...)
  ├─ repository/ (Spring Data Repositories)
  ├─ service/ (lógica de negocio)
  └─ web/ (controladores web y REST)
```

Ficheros clave:

- `src/main/java/com/example/demo/DemoApplication.java` — Punto de entrada.
- `src/main/java/com/example/demo/web/CompraRestController.java` — API REST de compra.
- `src/main/java/com/example/demo/service/ProductoService.java` — Lógica de productos.
- `pom.xml` — Dependencias y configuración Maven.

Si quieres saltar a un archivo, ábrelo en el editor del proyecto.

## Cómo probar flujo básico

1. Arranca la aplicación.
2. Abre http://localhost:8080 en tu navegador.
3. Regístrate o entra como usuario desde la pantalla de `login`.
4. Navega a `nueva_compra` para simular una compra.
5. Revisa panel de `dashboard` para ver analytics básicos (requiere Mongo para datos históricos).

## Contribución

1. Crea un fork y una rama con nombre descriptivo.
2. Añade tests y documentación para cambios importantes.
3. Abre un Pull Request describiendo los cambios brevemente.

## Licencia

Este proyecto es un demo. Añade una licencia según prefieras (MIT, Apache-2.0, etc.).

## Contacto

Si necesitas ayuda o quieres que prepare un despliegue Docker/Heroku, dime y lo preparo.

---

Gracias por revisar Demo-Tienda. ¡Listo para probar y mejorar! 🚀
