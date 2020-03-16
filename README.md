## Fuse Trace Error

Este proyecto muestra un inconveniente de propagacion de informacion de diagnostico y traza sobre rutas Camel

####  Analisis ejecucion
  
A continuacion se muestra el output de la aplicacion durante la ejecucion del servicio fuse.    
Se observa que los UnitOfWork en el contexto de un *multicast* ejecutan toda su logica sobre el mismo thread, 
por lo tanto es imposible propagar información de un thread al otro.
Las instancias de UnitOfWork son numeradas mediante un proceso sincronico en `UnitOfWorkFactory` (solo se sincroniza 
el incremento de un entero para interferir al minimo el paralelismo)    
Logback esta configurado para mostrar el nombre del thread origen.    
Si se observan los logs de `unitOfWork:0`, `unitOfWork:1` y `unitOfWork:2` estan asociados cada uno a un thread, 
de manera estricta. La construccion, y los metodos `beforeProcess`, `done`, `afterProcess` suceden sobre el mismo 
thread.     
Tambien puede obsservarse que los metodos `done` y `afterProcess` se ejecutan con el argumento `doneSync` 
con valor `true` cuando en realidad la tarea se lleva a cabo en *threads* diferentes y un notable *interliving* 
entre algunos de ellos

```ini
2020-03-15 21:04:59.679  INFO 37560 --- [           main] a.c.b.fuse.trace.error.Application       : fuse tracing error application is now running.....
2020-03-15 21:05:07.798  INFO 37560 --- [  XNIO-1 task-1] CustomUnitOfWorkFactory                  : unitOfWork:0 createUnitOfWork
2020-03-15 21:05:07.803  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 constructor collect MDC keys MDC_TEST_KEY
2020-03-15 21:05:07.803  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 constructor MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.816  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.817  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.818  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 done sync
2020-03-15 21:05:07.819  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 afterProcess sync
2020-03-15 21:05:07.819  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 beforeProcess found MDC key MDC_TEST_KEY:MDC_TEST_VALUE
2020-03-15 21:05:07.827  INFO 37560 --- [ #1 - Multicast] CustomUnitOfWorkFactory                  : unitOfWork:1 createUnitOfWork
2020-03-15 21:05:07.827  INFO 37560 --- [ #2 - Multicast] CustomUnitOfWorkFactory                  : unitOfWork:2 createUnitOfWork
2020-03-15 21:05:07.828  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 constructor collect MDC keys MDC_TEST_KEY
2020-03-15 21:05:07.828  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 constructor collect MDC keys MDC_TEST_KEY
2020-03-15 21:05:07.828  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 constructor MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.828  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 constructor MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.828  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.828  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.828  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.828  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.830  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.830  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.830  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.830  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 beforeProcess MDC key MDC_TEST_KEY not  found
2020-03-15 21:05:07.831  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : multicast item 2 MDC: null
2020-03-15 21:05:07.831  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : multicast item 1 MDC: null
2020-03-15 21:05:07.831  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 done sync
2020-03-15 21:05:07.831  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 done sync
2020-03-15 21:05:07.831  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 done sync
2020-03-15 21:05:07.831  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 done sync
2020-03-15 21:05:07.832  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 done sync
2020-03-15 21:05:07.832  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 done sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 done sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 done sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #1 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:1 afterProcess sync
2020-03-15 21:05:07.833  INFO 37560 --- [ #2 - Multicast] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:2 afterProcess sync
2020-03-15 21:05:07.835  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 done sync
2020-03-15 21:05:07.835  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 afterProcess sync
2020-03-15 21:05:07.836  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 done sync
2020-03-15 21:05:07.853  INFO 37560 --- [  XNIO-1 task-1] a.c.b.fuse.trace.error.route.RootRoute   : unitOfWork:0 afterProcess sync
```    


La aplicacion puede ser iniciada con el siguiente comando 

```bash
mvn spring-boot:run
```

Los sigueintes comandos invocan los servicios disponibles

```bash
curl 'http://localhost:8080/fuse/tracing'
```
```bash
curl 'http://localhost:8080/spring-web/tracing'
```

####  Casos de prueba

El proyecto implementa varios test:

Sobre los endpoints implementados con spring-web, se proveen 3 test.    
La aplicacion responde con status code 200 cuando se invoca un path existente.    
La aplciacion responde **NOT_FOUND**  cuando se invoca un path inexistente.    
La aplicacion tiene acceso a los valores de traza cuando se ejecuta en un contexto asincronico.    
En este ultimo, se muestra como un conjunto de procesos asincornicos tienen acceso a valores de MDC 
determinados en el hilo general de la aplicacion. Esto se logra con un wrapper implentado en el mismo proyecto
estos wrappers colectan valores mdc y el span jaeger del hilo donde son ejecutados para configurarlos
en el contexto en el que la instancia de **Callable** o **Runnable** es ejecutada    

Sobre los endpoints implementados con fuse, no fue posible comprobar los paths registrados. La documentacion
recomienda extraer el comportamiento de una definicion REST a un nuevo endpoint de tipo "direct:abc" para poder
hacer comprobaciones sobre este ultimo.    
Es deseable configurar un entorno de pruebas en el que se pueda hacer comprobaciones de endpoints HTTP.    
La unica prueba Camel verifica que el endopoint `restFuseTracing` asigne una lista conteniendo los valores MDC como 
cuerpo del *message* `in`, actualmente esta prueba falla y es lo que deseamos solucionar

El comando `mvn test` mostrará el siguiente output

```bash
[INFO]                                                                            
[INFO] Results:                                                                   
[INFO]                                                                            
[ERROR] Failures:                                                                 
[ERROR]   FuseTraceErrorRouteTest.whenMDCIsPropagatedThenCallableCanAccessMDC:43  
Expected: every item is is "MDC_TEST_VALUE"                                       
     but: an item was null                                                        
[INFO]                                                                            
[ERROR] Tests run: 6, Failures: 1, Errors: 0, Skipped: 0                          
[INFO]                                                                            
[INFO] ------------------------------------------------------------------------   
[INFO] BUILD FAILURE                                                              
[INFO] ------------------------------------------------------------------------   
```