package com.techsolutions.db;

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
 * Gestor de base de datos MongoDB para el sistema de gestión de tareas de TechSolutions S.A. de C.V.
 * 
 * Esta clase implementa el patrón Singleton para garantizar una única instancia de conexión
 * a la base de datos MongoDB. Proporciona métodos para realizar operaciones CRUD sobre
 * las colecciones de tareas y empleados.
 * 
 * Funcionalidades principales:
 * - Gestión de conexión a MongoDB
 * - Operaciones CRUD para tareas (crear, leer, actualizar, eliminar)
 * - Operaciones CRUD para empleados
 * - Búsquedas y filtros especializados
 * - Manejo de dependencias entre tareas
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class MongoDBManager {
    
    // ===============================
    // ATRIBUTOS DE INSTANCIA
    // ===============================
    
    /** Instancia única del MongoDBManager (patrón Singleton) */
    private static MongoDBManager instance;
    
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
    // CONSTANTES DE CONFIGURACIÓN
    // ===============================
    
    /** URI de conexión a MongoDB (servidor local) */
    private static final String MONGO_URI = "mongodb://localhost:27017";
    
    /** Nombre de la base de datos */
    private static final String DATABASE_NAME = "techsolutions";
    
    /** Nombre de la colección de tareas */
    private static final String TAREAS_COLLECTION = "tareas";
    
    /** Nombre de la colección de empleados */
    private static final String EMPLEADOS_COLLECTION = "empleados";

    // ===============================
    // CONSTRUCTOR Y SINGLETON
    // ===============================

    /**
     * Constructor privado para implementar patrón Singleton.
     * Inicializa automáticamente la conexión a MongoDB.
     */
    private MongoDBManager() {
        this.conexionEstablecida = false;
        inicializarConexion();
    }

    /**
     * Obtiene la instancia única del MongoDBManager (patrón Singleton).
     * Si no existe una instancia, la crea automáticamente.
     * 
     * @return La instancia única de MongoDBManager
     */
    public static MongoDBManager getInstance() {
        if (instance == null) {
            instance = new MongoDBManager();
        }
        return instance;
    }

    // ===============================
    // MÉTODOS DE CONEXIÓN
    // ===============================

    /**
     * Inicializa la conexión con MongoDB y configura las colecciones.
     * Realiza una verificación de conectividad ejecutando una consulta de prueba.
     */
    private void inicializarConexion() {
        try {
            // Establecer conexión con MongoDB
            mongoClient = MongoClients.create(MONGO_URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            tareasCollection = database.getCollection(TAREAS_COLLECTION);
            empleadosCollection = database.getCollection(EMPLEADOS_COLLECTION);
            
            // Verificar la conexión haciendo una consulta simple
            tareasCollection.countDocuments();
            
            // Marcar conexión como exitosa
            conexionEstablecida = true;
            System.out.println("✓ Conexión a MongoDB establecida correctamente");
            System.out.println("✓ Base de datos: " + DATABASE_NAME);
            System.out.println("✓ URI: " + MONGO_URI);
            
        } catch (Exception e) {
            conexionEstablecida = false;
            System.err.println("✗ Error al conectar con MongoDB: " + e.getMessage());
            System.err.println("✗ Verifica que MongoDB esté ejecutándose en " + MONGO_URI);
            e.printStackTrace();
        }
    }

    /**
     * Verifica si la conexión a MongoDB está establecida y activa.
     * 
     * @return true si la conexión está activa, false en caso contrario
     */
    public boolean isConectado() {
        return conexionEstablecida;
    }

    /**
     * Cierra la conexión actual y establece una nueva conexión a MongoDB.
     * Útil para recuperarse de errores de conexión.
     * 
     * @return true si la reconexión fue exitosa, false en caso contrario
     */
    public boolean reconectar() {
        cerrarConexion();
        inicializarConexion();
        return conexionEstablecida;
    }

    /**
     * Cierra la conexión con MongoDB de forma segura.
     * Libera todos los recursos asociados al cliente MongoDB.
     */
    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            conexionEstablecida = false;
            System.out.println("✓ Conexión a MongoDB cerrada correctamente");
        }
    }

    /**
     * Prueba la conexión a MongoDB realizando una operación simple.
     * Si la conexión no está establecida, intenta reconectarse automáticamente.
     * 
     * @return true si la conexión está funcionando correctamente, false en caso contrario
     */
    public boolean probarConexion() {
        try {
            // Si no hay conexión, intentar reconectar
            if (!conexionEstablecida) {
                reconectar();
            }
            
            // Realizar una operación simple para verificar la conexión
            database.listCollectionNames().first();
            return true;
        } catch (Exception e) {
            System.err.println("✗ Error en prueba de conexión: " + e.getMessage());
            return false;
        }
    }

    // ===============================
    // OPERACIONES CRUD - TAREAS
    // ===============================

    /**
     * Guarda una nueva tarea en la base de datos MongoDB.
     * 
     * @param tarea La tarea a guardar (objeto Tarea con toda la información)
     * @param tipo El tipo de tarea: "urgente", "programada", o "departamento"
     * @return true si la tarea se guardó correctamente, false en caso de error
     */
    public boolean guardarTarea(Tarea tarea, String tipo) {
        // Verificar que existe conexión a la base de datos
        if (!conexionEstablecida) {
            System.err.println("✗ No hay conexión con MongoDB");
            return false;
        }

        try {
            // Crear documento MongoDB a partir de la tarea
            Document doc = createTareaDocument(tarea, tipo);
            
            // Insertar el documento en la colección de tareas
            tareasCollection.insertOne(doc);
            System.out.println("✓ Tarea guardada correctamente: " + tarea.getId());
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error al guardar tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda una tarea con información adicional de prioridad y fecha de entrega.
     * Especializado para tareas que requieren seguimiento temporal y priorización.
     * 
     * @param tarea La tarea base a guardar
     * @param tipo El tipo de tarea: "urgente", "programada", o "departamento"
     * @param prioridad Nivel de prioridad (1=más alta, 4=más baja)
     * @param fechaEntrega Fecha límite para completar la tarea (formato: YYYY-MM-DD)
     * @return true si la tarea se guardó correctamente, false en caso de error
     */
    public boolean guardarTareaPrioridad(Tarea tarea, String tipo, int prioridad, String fechaEntrega) {
        // Verificar conexión a la base de datos
        if (!conexionEstablecida) {
            System.err.println("✗ No hay conexión con MongoDB");
            return false;
        }

        try {
            // Crear documento base de la tarea
            Document doc = createTareaDocument(tarea, tipo);
            
            // Agregar campos específicos de prioridad y fecha
            doc.append("prioridad", prioridad)
               .append("fechaEntrega", fechaEntrega);

            // Insertar en la base de datos
            tareasCollection.insertOne(doc);
            System.out.println("✓ Tarea con prioridad guardada: " + tarea.getId() + 
                             " (Prioridad: " + prioridad + ", Fecha: " + fechaEntrega + ")");
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error al guardar tarea con prioridad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga todas las tareas almacenadas en la base de datos MongoDB.
     * Convierte los documentos MongoDB en objetos Tarea de Java.
     * 
     * @return Lista de todas las tareas encontradas, o lista vacía si hay errores
     */
    public List<Tarea> cargarTodasLasTareas() {
        List<Tarea> tareas = new ArrayList<>();
        
        // Verificar conexión antes de proceder
        if (!conexionEstablecida) {
            System.err.println("✗ No hay conexión con MongoDB");
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
            System.err.println("Error al cargar tareas: " + e.getMessage());
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
            System.err.println("Error al cargar tareas por tipo: " + e.getMessage());
        }

        return tareas;
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
            System.err.println("Error al eliminar tarea: " + e.getMessage());
            return false;
        }
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

    // ==================== OPERACIONES DE EMPLEADOS ====================

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
            System.err.println("Error al guardar empleado: " + e.getMessage());
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
            System.err.println("Error al cargar empleados: " + e.getMessage());
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
            System.err.println("Error al cargar empleados en árbol: " + e.getMessage());
        }

        return arbol;
    }

    // ==================== ESTADÍSTICAS ====================

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
     * Obtiene el siguiente ID disponible para tareas
     */
    public String generarIdTarea() {
        try {
            int max = 0;
            for (Document doc : tareasCollection.find()) {
                String id = doc.getString("id");
                if (id != null && id.matches("T\\d+")) {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                }
            }
            return "T" + (max + 1);
        } catch (Exception e) {
            System.err.println("Error al generar ID de tarea: " + e.getMessage());
            return "T1";
        }
    }

    /**
     * Obtiene el siguiente ID disponible para empleados
     */
    public String generarIdEmpleado() {
        try {
            int max = 0;
            for (Document doc : empleadosCollection.find()) {
                String id = doc.getString("id");
                if (id != null && id.matches("EMP\\d+")) {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > max) max = num;
                }
            }
            return "EMP" + (max + 1);
        } catch (Exception e) {
            System.err.println("Error al generar ID de empleado: " + e.getMessage());
            return "EMP1";
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Crea un documento MongoDB a partir de una tarea
     */
    private Document createTareaDocument(Tarea tarea, String tipo) {
        return new Document("id", tarea.getId())
                .append("descripcion", tarea.getDescripcion())
                .append("departamento", tarea.getDepartamento())
                .append("urgencia", tarea.getUrgencia())
                .append("horasEstimadas", tarea.getHorasEstimadas())
                .append("tipo", tipo);
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
}