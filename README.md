# TechSolutions S.A. de C.V. — Sistema de Gestión de Tareas (Java + Swing + MongoDB)

> Universidad: TecMilenio · Materia: **Estructuras de Datos** · Profesora: **Blanca Aracely Aranda Machorro** · Ubicación: **Monterrey, Nuevo León**

Este repositorio contiene una implementación base y la documentación de un **sistema de gestión de tareas** para *TechSolutions S.A. de C.V.* construido en **Java 17**, **Swing**, **MongoDB** y **Maven**. Incluye estructuras de datos (Stack, Queue, PriorityQueue, HashMap, árbol binario y grafo de dependencias) y una organización por capas usando **MVC** + **DAO**.
## 📑 Índice

1. [Caso de Estudio](#caso-de-estudio)  
2. [Problemática identificada](#problemática-identificada)  
3. [Solución propuesta](#solución-propuesta)  
4. [Tecnologías utilizadas](#tecnologías)  
5. [Alcance del sistema](#alcance)  
6. [Roles del sistema](#roles-del-sistema)  
7. [Beneficios esperados](#beneficios)  
8. [Estructura del proyecto](#estructura-del-proyecto)  
9. [Estructuras de datos aplicadas](#estructuras-de-datos-aplicadas)  
10. [Requisitos Funcionales (RF01–RF09)](#requisitos-funcionales-rf)  
11. [Objetivos de aprendizaje alcanzados](#objetivos-de-aprendizaje-alcanzados)  
12. [Cómo ejecutar (local)](#cómo-ejecutar-local)  
    - [Requisitos](#1-requisitos)  
    - [Compilar y ejecutar](#2-compilar-y-ejecutar)  
    - [Variables de entorno opcionales](#3-variables-de-entorno-opcionales)  
13. [Cómo publicar en GitHub](#cómo-publicar-en-github-rápido)  
14. [Bibliografía](#bibliografía-selección)  
15. [Autores](#autores-del-entregable-original)  
16. [Licencia](#licencia)  





---

## Caso de Estudio

La empresa, ubicada en Monterrey, tiene varios departamentos (Desarrollo, Ventas, Soporte, Administración). Se detectaron problemas como la dispersión de tareas en hojas de cálculo, falta de priorización y poca visibilidad de dependencias, lo que dio lugar a retrasos y baja productividad.

---

## Problemática identificada

- Falta de priorización clara entre tareas urgentes y programadas.
- Dependencias poco visibles entre tareas.
- Retrasos por búsquedas lentas en listas generales.
- Escasa trazabilidad de empleados y su carga de trabajo.

---

## Solución propuesta

Una aplicación Java (consola/GUI) que gestiona tareas departamentales con **clientes VIP**, **inventario**, **ventas**, **reportes** y **roles**. Se integran estructuras de datos y persistencia en MongoDB bajo el patrón DAO.

---

## Tecnologías
- **Java 17+**
- **Maven**
- **Swing**
- **MongoDB**
- **Estructuras de Datos**: Stack, LinkedList, PriorityQueue, HashMap
- **Patrones**: MVC, DAO, Singleton
---
## Alcance
- Registro/gestión de tareas: ID, descripción, urgencia, fecha límite, departamento.
- Clasificación por estructuras: **Pila** (urgentes), **Cola** (programadas), **Lista** (por departamento), **Cola de Prioridad** (prioridad + fecha).
- Persistencia (CRUD) en MongoDB.
- Reportes básicos y estadísticas simples.
---
## Roles del Sistema
- **Empleado**: registra/consulta sus tareas.
- **Líder de Departamento**: consulta todas las tareas del área y ajusta prioridades.
- **Administrador**: CRUD global de tareas/empleados y configuración de BD.
- **Cliente VIP**: seguimiento prioritario (solo consumo).
---
## Beneficios
Priorización automática, visibilidad de dependencias/jerarquías, búsquedas rápidas, trazabilidad y persistencia de datos.

---

## Estructura del proyecto

```
techsolutions-presentacion-github/
├─ .gitignore
├─ LICENSE
├─ pom.xml
├─ README.md
└─ src/
   └─ main/java/com/techsolutions/
      ├─ Main.java
      ├─ controller/
      │  └─ SistemaGestionTareas.java
      ├─ dao/
      │  ├─ MongoConnection.java
      │  ├─ TareaDAO.java
      │  └─ EmpleadoDAO.java
      ├─ model/
      │  ├─ Tarea.java
      │  ├─ TareaPrioridad.java
      │  ├─ Empleado.java
      │  └─ DependenciasGrafo.java
      ├─ estructuras/
      │  ├─ ArbolEmpleados.java
      │  └─ EstructurasAuxiliares.java
      └─ gui/
         ├─ MainFrame.java
         ├─ TabUrgentes.java
         ├─ TabProgramadas.java
         ├─ TabDepartamento.java
         ├─ TabPrioridad.java
         └─ TabEmpleados.java
```

> El contenido base de este README se adaptó desde la presentación original del proyecto del curso. Consulta el documento fuente en la carpeta de la entrega para más detalles.

### Estructuras de Datos aplicadas
- **PriorityQueue**: `O(log n)` en inserción/poll, `O(1)` peek.
- **List**: recorrido `O(n)`; índice `O(1)`.
- **HashMap**: `O(1)` promedio en búsqueda/inserción.
- **Grafo**: recorrido `O(V+E)` (DFS para ciclos/dependencias).
- **Árbol Binario** para empleados.

### Requisitos Funcionales (RF)
RF01–RF09: registro y clasificación de tareas, búsqueda por ID, empleados en árbol binario, dependencias en grafo y persistencia MongoDB.

### Objetivos de Aprendizaje alcanzados
Entorno listo con Maven, conexión a `mongodb://localhost:27017` (BD `techsolutions`), patrones POO/DAO, UI Swing funcional, y verificación con Compass. Diagramas UML y flujo UI ↔ BD verificados.


## Cómo ejecutar (local)

1) **Requisitos**
- Java 17+ y Maven instalados en el PATH.
- MongoDB corriendo en `localhost:27017` (o variable `MONGODB_URI`).

2) **Compilar y ejecutar**

```bash
mvn -q -DskipTests package
java -jar target/gestion-tareas-0.1.0.jar
```

> Para lanzar la GUI directamente durante el desarrollo:
```bash
mvn -q exec:java -Dexec.mainClass="com.techsolutions.Main"
```

3) **Variables de entorno opcionales**
- `MONGODB_URI` (por defecto `mongodb://localhost:27017`)
- `MONGO_DB_NAME` (por defecto `techsolutions`)

## Cómo publicar en GitHub (rápido)

```bash
git init
git add .
git commit -m "Proyecto base: TechSolutions Gestion de Tareas"
git branch -M main
git remote add origin https://github.com/<tu-usuario>/<tu-repo>.git
git push -u origin main
```

> Si no tienes repo creado, primero crea uno vacío en GitHub con el mismo nombre que prefieras y luego ejecuta los comandos anteriores (reemplaza `<tu-usuario>/<tu-repo>`).

## Bibliografía (selección)
- Oracle. *Java SE Docs*.  
- MongoDB Inc. *MongoDB Manual*.  
- Weiss, M. A. *Data Structures and Algorithm Analysis in Java*.  
- GoF. *Design Patterns*.  
- Fowler. *PEAA*.  
- Deitel & Deitel. *Java: How to Program*.  
- Knuth. *TAOCP Vol.1*.

---

## Autores (del entregable original)
- Jesus David Marroquin Peña (AL07101520)  
- Mauricio Sanchez Figueroa (AL07098275)  
- Juan Porfirio Torres Rojas (AL07099471)  
- Ian Carlos Martinez Diaz (AL03003400)

## Licencia
MIT
