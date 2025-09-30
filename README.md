# 🏢 TechSolutions S.A. de C.V. - Sistema de Gestión de Tareas

> **Universidad:** TecMilenio · **Materia:** Estructuras de Datos · **Profesora:** Blanca Aracely Aranda Machorro  
> **Ubicación:** Monterrey, Nuevo León · **Versión:** 1.0.0
><img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/dd8d3596-a7ab-4bb6-811e-7aa38c7346a5" /> <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/687abdfb-932b-401f-a688-3dd26ae39c68" />

Este repositorio contiene una implementación base y la documentación de un **sistema de gestión de tareas** para *TechSolutions S.A. de C.V.* construido en **Java 17**, **Swing**, **MongoDB** y **Maven**. Incluye estructuras de datos (Stack, Queue, PriorityQueue, HashMap, árbol binario y grafo de dependencias) y una organización por capas usando **MVC** + **DAO**.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-4.0+-green.svg)](https://www.mongodb.com/)
[![Swing](https://img.shields.io/badge/GUI-Swing-red.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)

Sistema empresarial de gestión de tareas implementado con **estructuras de datos avanzadas**, **algoritmos optimizados** y **arquitectura MVC** para TechSolutions S.A. de C.V.

---

## 📋 Tabla de Contenidos

1. [📊 Caso de Estudio](#-caso-de-estudio)
2. [⚠️ Problemática Identificada](#️-problemática-identificada)
3. [✅ Solución Implementada](#-solución-implementada)
4. [🛠️ Tecnologías](#️-tecnologías)
5. [👥 Sistema de Roles](#-sistema-de-roles)
6. [📊 Estructuras de Datos](#-estructuras-de-datos)
7. [🔧 Algoritmos Implementados](#-algoritmos-implementados)
8. [🏗️ Arquitectura](#️-arquitectura)
9. [📁 Estructura del Proyecto](#-estructura-del-proyecto)
10. [🚀 Instalación y Ejecución](#-instalación-y-ejecución)
11. [👤 Usuarios Predeterminados](#-usuarios-predeterminados)
12. [📈 Funcionalidades Principales](#-funcionalidades-principales)
13. [📊 Diagramas](#-diagramas)
14. [🧪 Pruebas](#-pruebas)
15. [📚 Bibliografía](#-bibliografía)
16. [👨‍💻 Autores](#-autores)
17. [📄 Licencia](#-licencia)

---

## 📊 Caso de Estudio

**TechSolutions S.A. de C.V.** es una empresa tecnológica con sede en Monterrey, Nuevo León, que ofrece soluciones tecnológicas para diversas áreas de negocio. La empresa cuenta con departamentos especializados:

- 💻 **Desarrollo** - Proyectos de software y aplicaciones
- 💼 **Ventas** - Gestión comercial y CRM
- 🛠️ **Soporte** - Atención técnica al cliente
- 📊 **Administración** - Gestión empresarial
- 📢 **Marketing** - Campañas y contenido
- 👥 **Recursos Humanos** - Gestión de personal

---

## ⚠️ Problemática Identificada

Durante un diagnóstico interno se identificaron las siguientes deficiencias:

### 🔴 **Falta de Priorización**
- No existía clasificación clara entre tareas urgentes y programadas
- Actividades críticas no se atendían a tiempo
- Ausencia de sistema de prioridades numéricas

### 🔴 **Dependencias Invisibles**
- Relaciones entre tareas no documentadas estructuradamente
- Dificultad para entender qué actividades dependían de otras
- Riesgo de bloqueos en la ejecución de proyectos

### 🔴 **Búsquedas Ineficientes**
- Información dispersa en diferentes documentos
- Encontrar una tarea específica era lento y poco práctico
- Ausencia de filtros dinámicos y búsquedas optimizadas

### 🔴 **Escasa Trazabilidad**
- Complicado conocer la carga de trabajo de cada empleado
- Progreso de actividades asignadas sin seguimiento
- Falta de métricas y estadísticas en tiempo real

---

## ✅ Solución Implementada

### 🎯 **Sistema Integral de Gestión**
Desarrollo de una aplicación empresarial que resuelve completamente las problemáticas identificadas mediante:

- **🏗️ Arquitectura MVC robusta** con separación de responsabilidades
- **📊 7 estructuras de datos especializadas** para diferentes tipos de tareas
- **🔍 Algoritmos optimizados** para búsquedas y ordenamiento
- **👥 Sistema de roles jerárquico** con permisos específicos
- **💾 Persistencia en MongoDB** con sincronización automática
- **🎨 Interfaz gráfica moderna** con Swing personalizado

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **☕ Java** | 17+ | Lenguaje principal y lógica de negocio |
| **📦 Maven** | 3.8+ | Gestión de dependencias y construcción |
| **🍃 MongoDB** | 4.0+ | Base de datos NoSQL para persistencia |
| **🎨 Swing** | Built-in | Interfaz gráfica de usuario moderna |
| **📊 Mermaid** | Latest | Diagramas y documentación visual |

### 📚 **Dependencias Principales**
```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.9.1</version>
</dependency>
```

---

## 👥 Sistema de Roles

### 🏛️ **Jerarquía Empresarial**

```
👑 CEO → 👨‍💼 JEFE → 🏢 JEFE_DEPARTAMENTO → 👤 EMPLEADO
```

| Rol | Descripción | Permisos | Interfaces |
|-----|-------------|----------|------------|
| **👑 CEO** | Director Ejecutivo | Control total + Panel ejecutivo | 8 pestañas + Dashboard |
| **👨‍💼 JEFE** | Jefe General | Gestión global de departamentos | 7 pestañas completas |
| **🏢 JEFE_DEPTO** | Jefe de Departamento | Solo su departamento y empleados | 5 pestañas filtradas |
| **👤 EMPLEADO** | Empleado Regular | Solo sus tareas asignadas | 3-4 pestañas básicas |

---

## 📊 Estructuras de Datos

### 🎯 **Implementación Especializada**

| Estructura | Uso Empresarial | Complejidad | Ubicación en Código |
|------------|-----------------|-------------|---------------------|
| **📚 Stack (Pila)** | Tareas urgentes LIFO | O(1) push/pop | `pilaTareasUrgentes` |
| **⏰ Queue (Cola)** | Tareas programadas FIFO | O(1) enqueue/dequeue | `colaTareasProgramadas` |
| **📋 List (Lista)** | Tareas departamentales | O(1) acceso indexado | `listaTareasDepartamento` |
| **⭐ PriorityQueue** | Ordenamiento automático | O(log n) inserción | `colaPrioridad` |
| **🌳 BST (Árbol)** | Empleados por ID | O(log n) búsqueda | `ArbolEmpleados.java` |
| **🔍 HashMap** | Acceso instantáneo | O(1) búsqueda | `hashTareas` |
| **🕸️ Graph (Grafo)** | Dependencias entre tareas | O(V+E) recorrido | `GrafoTareas.java` |

### 📈 **Ventajas de Rendimiento**

```java
// Ejemplos de operaciones optimizadas
Stack<Tarea> pilaTareasUrgentes = new Stack<>();               // O(1)
LinkedList<Tarea> colaTareasProgramadas = new LinkedList<>();  // O(1)
PriorityQueue<TareaPrioridad> colaPrioridad = new PriorityQueue<>(); // O(log n)
Map<String, Tarea> hashTareas = new HashMap<>();               // O(1)
```

---

## 🔧 Algoritmos Implementados

### 🔍 **Algoritmos de Búsqueda**
- **Búsqueda Binaria** `O(log n)` - Listas ordenadas
- **Búsqueda en BST** `O(log n)` - Empleados por ID
- **HashMap Lookup** `O(1)` - Acceso instantáneo por clave
- **Búsqueda Lineal** `O(n)` - Filtros específicos

### 📊 **Algoritmos de Ordenamiento**
- **QuickSort** - Ordenamiento por prioridad
- **MergeSort** - Ordenamiento por departamento
- **Ordenamiento Natural** - PriorityQueue automático

### 🔄 **Algoritmos Recursivos**
- **Cálculo de tiempo total** - Suma recursiva de horas estimadas
- **Recorrido de árbol** - Inorden, preorden, postorden
- **Divide y vencerás** - Distribución optimizada de tareas

### 🕸️ **Algoritmos de Grafos**
- **Detección de ciclos** - Prevención de dependencias circulares
- **Ordenamiento topológico** - Secuencia óptima de ejecución
- **Búsqueda DFS/BFS** - Exploración de dependencias

---

## 🏗️ Arquitectura

### 📐 **Patrón MVC + DAO**

```
🎨 PRESENTATION LAYER (GUI)
├── LoginFrame.java
├── SistemaGestionTareas.java
├── GestionUsuariosPanel.java
├── ArbolEmpleadosViewer.java
└── TableWithFilters.java

⚙️ BUSINESS LAYER (Services)
├── UsuarioService.java
├── MongoDBService.java
└── Utilidades.java

📊 MODEL LAYER (Entities)
├── Usuario.java
├── Empleado.java
├── Tarea.java
└── TareaPrioridad.java

💾 DATA LAYER (Persistence)
├── MongoDBManager.java
└── MongoDB Database
```

### 🔧 **Patrones de Diseño**
- **Singleton** - UsuarioService, MongoDBManager
- **Factory** - Creación de componentes GUI
- **Observer** - Actualizaciones de tablas
- **Strategy** - Diferentes algoritmos de ordenamiento

---

## 📁 Estructura del Proyecto

```
Proyecto/
├── 📄 pom.xml                          # Configuración Maven
├── 📖 README.md                        # Documentación principal
└── 📁 src/main/java/com/techsolutions/
    ├── 🚀 SistemaGestionTareas.java    # Clase principal con GUI
    ├── 📊 DatosPredeterminados.java    # Carga de datos de prueba
    ├── 📁 db/
    │   └── MongoDBManager.java         # Gestión de conexión MongoDB
    ├── 📁 gui/                         # Componentes de interfaz
    │   ├── LoginFrame.java             # Ventana de autenticación
    │   ├── GestionUsuariosPanel.java   # Panel admin de usuarios
    │   ├── ArbolEmpleadosViewer.java   # Visualizador de árbol
    │   ├── TableWithFilters.java       # Tablas con filtros
    │   └── PanelPila.java              # Panel específico de pila
    ├── 📁 model/                       # Modelos de datos
    │   ├── Usuario.java                # Modelo de usuario con roles
    │   ├── Empleado.java               # Entidad empleado
    │   ├── Tarea.java                  # Entidad base de tarea
    │   ├── TareaPrioridad.java         # Tarea con prioridad
    │   └── ArbolEmpleados.java         # BST de empleados
    ├── 📁 services/                    # Lógica de negocio
    │   ├── UsuarioService.java         # Gestión de usuarios
    │   └── MongoDBService.java         # Servicios de persistencia
    ├── 📁 util/                        # Utilidades y algoritmos
    │   ├── GrafoTareas.java            # Grafo de dependencias
    │   └── Utilidades.java             # Métodos auxiliares
    └── 📁 utils/                       # Herramientas de testing
        └── PruebaConexionMongoDB.java  # Test de conexión DB
```

---

## 🚀 Instalación y Ejecución

### 📋 **Requisitos Previos**

1. **☕ Java 17+** instalado y configurado en PATH
2. **📦 Maven 3.8+** para gestión de dependencias
3. **🍃 MongoDB** ejecutándose en `localhost:27017`

### ⚡ **Ejecución Rápida**

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd Proyecto

# 2. Compilar el proyecto
mvn clean compile

# 3. Cargar datos de prueba (opcional)
mvn exec:java -Dexec.mainClass="com.techsolutions.DatosPredeterminados"

# 4. Ejecutar la aplicación principal
mvn exec:java -Dexec.mainClass="com.techsolutions.SistemaGestionTareas"
```

### 📦 **Crear JAR Ejecutable**

```bash
# Generar JAR con todas las dependencias
mvn clean package

# Ejecutar JAR standalone
java -jar target/sistema-gestion-tareas-1.0.0-jar-with-dependencies.jar
```

### 🔧 **Variables de Entorno (Opcionales)**

```bash
export MONGODB_URI="mongodb://localhost:27017"
export MONGO_DB_NAME="techsolutions"
```

---

## 👤 Usuarios Predeterminados

### 🔐 **Credenciales de Acceso**

| Rol | Usuario | Contraseña | Descripción |
|-----|---------|------------|-------------|
| **👑 CEO** | `ceo` | `ceo123` | Control ejecutivo total |
| **👨‍💼 JEFE** | `jefe` | `jefe123` | Gestión general de departamentos |
| **🏢 JEFE_IT** | `jefe_it` | `it123` | Jefe del departamento IT |
| **👤 EMPLEADO** | `empleado` | `emp123` | Empleado básico |

### 🎯 **Generación Automática**
El sistema puede crear automáticamente cuentas para todos los empleados registrados en MongoDB usando el botón **"Crear Cuentas"** desde el panel de administración.

---

## 📈 Funcionalidades Principales

### 🎛️ **Panel CEO (Control Total)**
- 🏢 Dashboard ejecutivo con métricas avanzadas
- 📊 Análisis financiero y estratégico  
- 🔒 Auditoría completa del sistema
- ⚙️ Override para casos excepcionales
- 💰 Control de rendimiento global

### 👨‍💼 **Panel Jefe General**
- 📋 Gestión de todas las estructuras de datos
- 👥 Administración completa de usuarios
- 📊 Reportes globales y estadísticas
- 🔄 Funciones de reseteo del sistema
- 🆔 Creación masiva de cuentas

### 🏢 **Panel Jefe de Departamento**
- 🎯 Gestión filtrada por departamento
- 👥 Supervisión de empleados a cargo
- ✅ Revisión de tareas completadas
- ➕ Creación de tareas departamentales
- 📊 Estadísticas específicas del área

### 👤 **Panel Empleado**
- 📋 Vista de tareas asignadas únicamente
- ✅ Completar y marcar tareas
- 📊 Seguimiento de progreso personal
- 💬 Paneles especializados por departamento

### 🏢 **Paneles Especializados por Departamento**

#### 💻 **IT (Tecnología)**
- 🔧 Gestión de tickets de soporte
- ⚙️ Mantenimiento de sistemas
- 📚 Documentación técnica
- 💾 Administración de bases de datos

#### 💼 **Ventas (Comercial)**
- 🎯 CRM y seguimiento de leads
- 👥 Gestión de clientes
- 📊 Pipeline de ventas
- 💰 Reportes comerciales

#### 📢 **Marketing**
- 📱 Gestión de campañas
- 🎨 Creación de contenido
- 📊 Análisis de métricas
- 🌐 Marketing digital

#### 👥 **Recursos Humanos**
- 👤 Gestión de personal
- 📋 Evaluaciones de desempeño
- 🎓 Programas de capacitación
- 📊 Reportes de RRHH

---

## 📊 Diagramas

El proyecto incluye diagramas completos en formato Mermaid ubicados en la carpeta [`Diagramas/`](Diagramas/):

### 📋 **Diagramas Disponibles**
- **📊 Flujo Principal** - `diagrama-flujo-principal.mmd`
- **👥 Casos de Uso** - `diagrama-casos-uso.mmd`
- **🏗️ Clases** - `diagrama-clases.mmd`
- **📊 Estructuras de Datos** - `diagrama-estructuras-datos.mmd`
- **🔄 Secuencia** - `diagrama-secuencia.mmd`
- **🏛️ Arquitectura** - `diagrama-arquitectura.mmd`
- **🔄 Estados** - `diagrama-estados.mmd`

### 🛠️ **Cómo Visualizar**
1. **VS Code**: Instalar extensión "Mermaid Preview"
2. **Online**: [Mermaid Live Editor](https://mermaid.live)
3. **GitHub**: Renderizado automático en repositorios

---

## 🧪 Pruebas

### ✅ **Testing de Conexión**
```bash
# Probar conexión a MongoDB
mvn exec:java -Dexec.mainClass="com.techsolutions.utils.PruebaConexionMongoDB"
```

### 📊 **Carga de Datos de Prueba**
```bash
# Cargar empleados y tareas de ejemplo
mvn exec:java -Dexec.mainClass="com.techsolutions.DatosPredeterminados"
```

### 🔍 **Verificación de Funcionalidades**
- ✅ Autenticación por roles
- ✅ Operaciones CRUD en estructuras de datos
- ✅ Persistencia en MongoDB
- ✅ Filtros y búsquedas optimizadas
- ✅ Algoritmos de ordenamiento
- ✅ Detección de dependencias circulares
- ✅ Interfaz gráfica responsive

---

## 📚 Bibliografía

### 📖 **Referencias Técnicas**
- **Oracle Corporation**. *Java SE Documentation*. [docs.oracle.com/javase](https://docs.oracle.com/javase/)
- **MongoDB Inc**. *MongoDB Manual*. [docs.mongodb.com](https://docs.mongodb.com/)
- **Apache Software Foundation**. *Maven Documentation*. [maven.apache.org](https://maven.apache.org/)

### 📚 **Literatura Especializada**
- **Weiss, Mark A.** *Data Structures and Algorithm Analysis in Java*. 3rd Edition.
- **Gang of Four**. *Design Patterns: Elements of Reusable Object-Oriented Software*.
- **Fowler, Martin**. *Patterns of Enterprise Application Architecture*.
- **Deitel & Deitel**. *Java: How to Program*. 11th Edition.
- **Knuth, Donald E.** *The Art of Computer Programming, Volume 1: Fundamental Algorithms*.

### 🔬 **Papers y Recursos Académicos**
- **Cormen, et al.** *Introduction to Algorithms*. MIT Press.
- **Sedgewick, Robert**. *Algorithms in Java*. 4th Edition.
- **Gamma, et al.** *Design Patterns in Object-Oriented Software Development*.

---

## 👨‍💻 Autores

### 🎓 **Equipo de Desarrollo**

**Universidad TecMilenio - Campus Monterrey**  
**Materia:** Estructuras de Datos  
**Profesora:** Blanca Aracely Aranda Machorro  
**Semestre:** 2025

### 💼 **Perfil del Estudiante**
- **🎯 Especialización:** Desarrollo de Software Empresarial
- **💻 Tecnologías:** Java, Spring, MongoDB, React
- **🏆 Enfoque:** Algoritmos Optimizados y Arquitecturas Escalables
- **📊 Experiencia:** Sistemas de Gestión Empresarial

---

## 📄 Licencia

```
MIT License

Copyright (c) 2025 TechSolutions S.A. de C.V.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🚀 **Estado del Proyecto**

![Estado](https://img.shields.io/badge/Estado-Produccion-brightgreen)
![Versión](https://img.shields.io/badge/Version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-17+-orange)
![Build](https://img.shields.io/badge/Build-Passing-success)

### 🎯 **Próximas Mejoras**
- 🔄 API REST para integración externa
- 📱 Aplicación móvil complementaria
- 📊 Dashboard analítico avanzado
- 🤖 Inteligencia artificial para optimización de tareas
- ☁️ Despliegue en cloud (AWS/Azure)

---

<div align="center">

### 🏢 **TechSolutions S.A. de C.V.**
*Innovando en Gestión Empresarial con Tecnología Avanzada*

**Monterrey, Nuevo León - México 🇲🇽**

</div>
