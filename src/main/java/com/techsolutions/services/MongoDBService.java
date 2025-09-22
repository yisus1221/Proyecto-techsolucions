package com.techsolutions.services;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.techsolutions.model.ArbolEmpleados;
import com.techsolutions.model.Empleado;
import com.techsolutions.model.Tarea;
import com.techsolutions.model.TareaPrioridad;

/**
 * Servicio especializado para manejar todas las operaciones de MongoDB
 * en el sistema de gestión de tareas de TechSolutions S.A. de C.V.
 * 
 * Esta clase actúa como capa de servicio entre la interfaz de usuario
 * y la base de datos MongoDB. Proporciona métodos específicos para:
 * 
 * Operaciones de Tareas:
 * - Guardar tareas simples y con prioridad
 * - Cargar tareas filtradas por tipo, departamento, urgencia
 * - Búsqueda de tareas por diversos criterios
 * - Actualización y eliminación de tareas
 * 
 * Operaciones de Empleados:
 * - Cargar empleados para estructura de árbol binario
 * - Búsqueda de empleados por ID y departamento
 * - Gestión de la jerarquía organizacional
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class MongoDBService {
    
    // ===============================
    // ATRIBUTOS DE CONEXIÓN
    // ===============================
    
    /** Cliente de conexión a MongoDB */
    private MongoClient mongoClient;
    
    /** Base de datos MongoDB */
    private MongoDatabase database;
    
    /** Colección de tareas en MongoDB */
    private MongoCollection<Document> tareasCollection;
    
    /** Colección de empleados en MongoDB */
    private MongoCollection<Document> empleadosCollection;
    
    /** Estado de la conexión a la base de datos */
    private boolean conexionEstablecida;

    // ===============================
    // CONSTRUCTOR E INICIALIZACIÓN
    // ===============================

    /**
     * Constructor que inicializa automáticamente la conexión a MongoDB.
     * Establece la conexión y configura las colecciones necesarias.
     */
    public MongoDBService() {
        this.conexionEstablecida = false;
        inicializar();
    }

    /**
     * Método privado para inicializar la conexión a MongoDB.
     * Separado del constructor para evitar llamadas a métodos sobrescribibles.
     * Realiza verificación de conectividad y configura las colecciones.
     */
    private void inicializar() {
        try {
            // Establecer conexión con MongoDB
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("techsolutions");
            tareasCollection = database.getCollection("tareas");
            empleadosCollection = database.getCollection("empleados");
            
            // Verificar la conexión haciendo una consulta simple
            tareasCollection.countDocuments();
            
            conexionEstablecida = true;
            System.out.println("Conexión a MongoDB establecida correctamente");
        } catch (Exception e) {
            conexionEstablecida = false;
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
        }
    }

    /**
     * Establece conexión con MongoDB
     */
    public boolean conectar() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("techsolutions");
            tareasCollection = database.getCollection("tareas");
            empleadosCollection = database.getCollection("empleados");
            
            // Verificar la conexión haciendo una consulta simple
            tareasCollection.countDocuments();
            
            conexionEstablecida = true;
            System.out.println("Conexión a MongoDB establecida correctamente");
            return true;
        } catch (Exception e) {
            conexionEstablecida = false;
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si la conexión está establecida
     */
    public boolean isConexionEstablecida() {
        return conexionEstablecida;
    }

    /**
     * Cierra la conexión con MongoDB
     */
    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            conexionEstablecida = false;
            System.out.println("Conexión a MongoDB cerrada");
        }
    }

    /**
     * Guarda una tarea en MongoDB
     */
    public boolean guardarTarea(Tarea tarea, String tipo) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return false;
        }

        try {
            Document doc = new Document("id", tarea.getId())
                    .append("descripcion", tarea.getDescripcion())
                    .append("departamento", tarea.getDepartamento())
                    .append("urgencia", tarea.getUrgencia())
                    .append("horasEstimadas", tarea.getHorasEstimadas())
                    .append("tipo", tipo);

            // Si es una TareaPrioridad, agregar campos adicionales
            if (tarea instanceof TareaPrioridad) {
                TareaPrioridad tp = (TareaPrioridad) tarea;
                doc.append("prioridad", tp.getPrioridad())
                   .append("fechaEntrega", tp.getFechaEntrega());
            }

            tareasCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar tarea en MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda una tarea con prioridad y fecha de entrega
     */
    public boolean guardarTareaPrioridad(Tarea tarea, String tipo, int prioridad, String fechaEntrega) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return false;
        }

        try {
            Document doc = new Document("id", tarea.getId())
                    .append("descripcion", tarea.getDescripcion())
                    .append("departamento", tarea.getDepartamento())
                    .append("urgencia", tarea.getUrgencia())
                    .append("horasEstimadas", tarea.getHorasEstimadas())
                    .append("tipo", tipo)
                    .append("prioridad", prioridad)
                    .append("fechaEntrega", fechaEntrega);

            tareasCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar tarea con prioridad en MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga todas las tareas desde MongoDB
     */
    public List<Tarea> cargarTodasLasTareas() {
        List<Tarea> tareas = new ArrayList<>();
        
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return tareas;
        }

        try {
            for (Document doc : tareasCollection.find()) {
                Tarea tarea = documentToTarea(doc);
                if (tarea != null) {
                    tareas.add(tarea);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar tareas desde MongoDB: " + e.getMessage());
        }

        return tareas;
    }

    /**
     * Carga tareas por tipo específico
     */
    public List<Tarea> cargarTareasPorTipo(String tipo) {
        List<Tarea> tareas = new ArrayList<>();
        
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return tareas;
        }

        try {
            for (Document doc : tareasCollection.find(Filters.eq("tipo", tipo))) {
                Tarea tarea = documentToTarea(doc);
                if (tarea != null) {
                    tareas.add(tarea);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar tareas por tipo desde MongoDB: " + e.getMessage());
        }

        return tareas;
    }

    /**
     * Elimina una tarea por ID
     */
    public boolean eliminarTarea(String id) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return false;
        }

        try {
            return tareasCollection.deleteOne(Filters.eq("id", id)).getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar tarea desde MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca una tarea por ID
     */
    public Tarea buscarTareaPorId(String id) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return null;
        }

        try {
            Document doc = tareasCollection.find(Filters.eq("id", id)).first();
            return doc != null ? documentToTarea(doc) : null;
        } catch (Exception e) {
            System.err.println("Error al buscar tarea por ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda un empleado en MongoDB
     */
    public boolean guardarEmpleado(Empleado empleado) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return false;
        }

        try {
            Document doc = new Document("id", empleado.getId())
                    .append("nombre", empleado.getNombre())
                    .append("departamento", empleado.getDepartamento());

            empleadosCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar empleado en MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga todos los empleados desde MongoDB
     */
    public List<Empleado> cargarTodosLosEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return empleados;
        }

        try {
            for (Document doc : empleadosCollection.find()) {
                Empleado empleado = documentToEmpleado(doc);
                if (empleado != null) {
                    empleados.add(empleado);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar empleados desde MongoDB: " + e.getMessage());
        }

        return empleados;
    }

    /**
     * Carga empleados en un árbol desde MongoDB
     */
    public ArbolEmpleados cargarEmpleadosEnArbol() {
        ArbolEmpleados arbol = new ArbolEmpleados();
        
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return arbol;
        }

        try {
            for (Document doc : empleadosCollection.find()) {
                Empleado empleado = documentToEmpleado(doc);
                if (empleado != null) {
                    arbol.insertar(empleado);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar empleados en árbol desde MongoDB: " + e.getMessage());
        }

        return arbol;
    }

    /**
     * Actualiza una tarea existente
     */
    public boolean actualizarTarea(String id, Tarea tareaActualizada) {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return false;
        }

        try {
            Bson filter = Filters.eq("id", id);
            Bson updates = Updates.combine(
                Updates.set("descripcion", tareaActualizada.getDescripcion()),
                Updates.set("departamento", tareaActualizada.getDepartamento()),
                Updates.set("urgencia", tareaActualizada.getUrgencia()),
                Updates.set("horasEstimadas", tareaActualizada.getHorasEstimadas())
            );

            return tareasCollection.updateOne(filter, updates).getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene estadísticas de las tareas
     */
    public Document obtenerEstadisticasTareas() {
        if (!conexionEstablecida) {
            System.err.println("No hay conexión con MongoDB");
            return new Document();
        }

        try {
            long totalTareas = tareasCollection.countDocuments();
            long tareasUrgentes = tareasCollection.countDocuments(Filters.eq("tipo", "urgente"));
            long tareasProgramadas = tareasCollection.countDocuments(Filters.eq("tipo", "programada"));
            long tareasDepartamento = tareasCollection.countDocuments(Filters.eq("tipo", "departamento"));

            return new Document("total", totalTareas)
                    .append("urgentes", tareasUrgentes)
                    .append("programadas", tareasProgramadas)
                    .append("departamento", tareasDepartamento);
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            return new Document();
        }
    }

    /**
     * Convierte un Document de MongoDB a objeto Tarea
     */
    private Tarea documentToTarea(Document doc) {
        try {
            String id = doc.getString("id");
            String descripcion = doc.getString("descripcion");
            String departamento = doc.getString("departamento");
            String urgencia = doc.getString("urgencia");
            int horasEstimadas = doc.getInteger("horasEstimadas", 1);

            // Verificar si tiene campos de prioridad
            Integer prioridad = doc.getInteger("prioridad");
            String fechaEntrega = doc.getString("fechaEntrega");

            if (prioridad != null && fechaEntrega != null) {
                return new TareaPrioridad(id, descripcion, departamento, urgencia, horasEstimadas, prioridad, fechaEntrega);
            } else {
                return new Tarea(id, descripcion, departamento, urgencia, horasEstimadas);
            }
        } catch (Exception e) {
            System.err.println("Error al convertir documento a tarea: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convierte un Document de MongoDB a objeto Empleado
     */
    private Empleado documentToEmpleado(Document doc) {
        try {
            String id = doc.getString("id");
            String nombre = doc.getString("nombre");
            String departamento = doc.getString("departamento");

            return new Empleado(id, nombre, departamento);
        } catch (Exception e) {
            System.err.println("Error al convertir documento a empleado: " + e.getMessage());
            return null;
        }
    }

    /**
     * Prueba la conexión con MongoDB
     */
    public boolean probarConexion() {
        try {
            if (mongoClient == null) {
                conectar();
            }
            
            // Realizar una operación simple para verificar la conexión
            database.listCollectionNames().first();
            return true;
        } catch (Exception e) {
            System.err.println("Error en prueba de conexión: " + e.getMessage());
            return false;
        }
    }
}