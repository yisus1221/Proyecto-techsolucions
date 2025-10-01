package com.techsolutions.model;

/**
 * Clase que representa una tarea en el sistema de gestión de TechSolutions S.A. de C.V.
 * 
 * Una tarea es la unidad básica de trabajo en el sistema. Contiene información
 * esencial como identificador único, descripción, departamento responsable,
 * nivel de urgencia y estimación de tiempo requerido.
 * 
 * Esta clase se utiliza en diferentes estructuras de datos:
 * - Pila (Stack): Para tareas urgentes (LIFO - Last In, First Out)
 * - Cola (Queue): Para tareas programadas (FIFO - First In, First Out)  
 * - Lista (List): Para tareas departamentales (acceso indexado)
 * - Cola de Prioridad: Para tareas con priorización numérica
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class Tarea {
    
    // ===============================
    // ATRIBUTOS DE LA TAREA
    // ===============================
    
    /** Identificador único de la tarea (ej: T1, T2, T3...) */
    private String id;
    
    /** Descripción detallada de la tarea a realizar */
    private String descripcion;
    
    /** Departamento responsable de ejecutar la tarea */
    private String departamento;
    
    /** Nivel de urgencia: "Crítica", "Alta", "Media", "Baja" */
    private String urgencia;
    
    /** Tiempo estimado en horas para completar la tarea */
    private int horasEstimadas;
    
    /** ID del empleado asignado a esta tarea */
    private String empleadoAsignado;

    // ===============================
    // CONSTRUCTORES
    // ===============================

    /**
     * Constructor principal para crear una tarea completa.
     * 
     * @param id Identificador único de la tarea (no puede ser null)
     * @param descripcion Descripción detallada de la tarea (no puede ser null)
     * @param departamento Departamento responsable (ej: "Desarrollo", "Soporte Técnico")
     * @param urgencia Nivel de urgencia ("Crítica", "Alta", "Media", "Baja")
     * @param horasEstimadas Tiempo estimado en horas (debe ser > 0)
     */
    public Tarea(String id, String descripcion, String departamento, String urgencia, int horasEstimadas) {
        this.id = id;
        this.descripcion = descripcion;
        this.departamento = departamento;
        this.urgencia = urgencia;
        this.horasEstimadas = horasEstimadas;
    }

    /**
     * Constructor de compatibilidad para tareas sin especificar horas estimadas.
     * Asigna automáticamente 1 hora como valor por defecto.
     * 
     * @param id Identificador único de la tarea
     * @param descripcion Descripción detallada de la tarea
     * @param departamento Departamento responsable
     * @param urgencia Nivel de urgencia
     */
    public Tarea(String id, String descripcion, String departamento, String urgencia) {
        this(id, descripcion, departamento, urgencia, 1); // Valor por defecto: 1 hora
    }

    /**
     * Constructor completo que incluye empleado asignado.
     * 
     * @param id Identificador único de la tarea
     * @param descripcion Descripción detallada de la tarea
     * @param departamento Departamento responsable
     * @param urgencia Nivel de urgencia
     * @param horasEstimadas Tiempo estimado en horas
     * @param empleadoAsignado ID del empleado asignado
     */
    public Tarea(String id, String descripcion, String departamento, String urgencia, int horasEstimadas, String empleadoAsignado) {
        this.id = id;
        this.descripcion = descripcion;
        this.departamento = departamento;
        this.urgencia = urgencia;
        this.horasEstimadas = horasEstimadas;
        this.empleadoAsignado = empleadoAsignado;
    }

    // ===============================
    // MÉTODOS GETTER (ACCESO LECTURA)
    // ===============================

    /**
     * Obtiene el identificador único de la tarea.
     * 
     * @return ID de la tarea (ej: "T1", "T2", etc.)
     */
    public String getId() { 
        return id; 
    }
    
    /**
     * Obtiene la descripción detallada de la tarea.
     * 
     * @return Descripción de lo que debe realizarse
     */
    public String getDescripcion() { 
        return descripcion; 
    }
    
    /**
     * Obtiene el departamento responsable de la tarea.
     * 
     * @return Nombre del departamento (ej: "Desarrollo", "Marketing")
     */
    public String getDepartamento() { 
        return departamento; 
    }
    
    /**
     * Obtiene el nivel de urgencia de la tarea.
     * 
     * @return Urgencia ("Crítica", "Alta", "Media", "Baja")
     */
    public String getUrgencia() { 
        return urgencia; 
    }
    
    /**
     * Obtiene el tiempo estimado en horas para completar la tarea.
     * 
     * @return Número de horas estimadas (valor positivo)
     */
    public int getHorasEstimadas() { 
        return horasEstimadas; 
    }

    // ===============================
    // MÉTODOS SETTER (MODIFICACIÓN)
    // ===============================

    /**
     * Modifica el identificador de la tarea.¿
     * 
     * @param id Nuevo identificador único para la tarea
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Modifica la descripción de la tarea.
     * 
     * @param descripcion Nueva descripción detallada de la tarea
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Cambia el departamento responsable de la tarea.
     * 
     * @param departamento Nuevo departamento asignado
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Actualiza el nivel de urgencia de la tarea.
     * 
     * @param urgencia Nuevo nivel de urgencia ("Crítica", "Alta", "Media", "Baja")
     */
    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    /**
     * Modifica la estimación de tiempo para completar la tarea.
     * 
     * @param horasEstimadas Nuevo número de horas estimadas (debe ser > 0)
     */
    public void setHorasEstimadas(int horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    /**
     * Obtiene el ID del empleado asignado a esta tarea.
     * 
     * @return ID del empleado asignado o null si no se ha asignado
     */
    public String getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    /**
     * Establece el empleado asignado a esta tarea.
     * 
     * @param empleadoAsignado ID del empleado a asignar
     */
    public void setEmpleadoAsignado(String empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }

    // ===============================
    // MÉTODOS SOBRESCRITOS (OVERRIDE)
    // ===============================

    /**
     * Proporciona una representación textual de la tarea para depuración.
     * Incluye todos los atributos principales en formato legible.
     * 
     * @return Cadena de texto con la información completa de la tarea
     */
    @Override
    public String toString() {
        return "Tarea{" +
                "id='" + id + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", departamento='" + departamento + '\'' +
                ", urgencia='" + urgencia + '\'' +
                ", horasEstimadas=" + horasEstimadas +
                ", empleadoAsignado='" + empleadoAsignado + '\'' +
                '}';
    }

    /**
     * Compara dos tareas para determinar si son iguales.
     * La comparación se basa únicamente en el ID de la tarea.
     * Dos tareas son consideradas iguales si tienen el mismo ID.
     * 
     * @param obj Objeto a comparar con esta tarea
     * @return true si las tareas tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        // Verificar si es la misma referencia
        if (this == obj) return true;
        
        // Verificar si el objeto es null o de diferente clase
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Convertir a Tarea y comparar por ID
        Tarea tarea = (Tarea) obj;
        return id != null && id.equals(tarea.id);
    }

    /**
     * Genera un código hash para la tarea basado en su ID.
     * Necesario para usar la clase en colecciones como HashMap o HashSet.
     * 
     * @return Código hash basado en el ID de la tarea
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}