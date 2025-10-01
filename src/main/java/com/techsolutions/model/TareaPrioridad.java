package com.techsolutions.model;

/**
 * Clase especializada que extiende Tarea para manejar tareas con prioridad y fechas de entrega.
 * 
 * Esta clase se utiliza específicamente en la Cola de Prioridad (PriorityQueue) del sistema,
 * donde las tareas se ordenan automáticamente según su nivel de prioridad numérica.
 * 
 * Sistema de Prioridades:
 * - 1 = Prioridad Alta
 * - 2 = Prioridad Media  
 * - 3 = Prioridad Baja (menor urgencia)
 * 
 * Implementa Comparable para permitir ordenamiento automático en estructuras
 * de datos que requieren comparación (como PriorityQueue).
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class TareaPrioridad extends Tarea implements Comparable<TareaPrioridad> {
    
    // ===============================
    // ATRIBUTOS ESPECÍFICOS
    // ===============================
    
    /** Nivel de prioridad numérica (1=Crítica, 2=Alta, 3=Media, 4=Baja) */
    private int prioridad;
    
    /** Fecha límite de entrega en formato yyyy-MM-dd (ej: 2025-12-31) */
    private String fechaEntrega;

    // ===============================
    // CONSTRUCTORES
    // ===============================

    /**
     * Constructor básico para crear una tarea con prioridad y fecha de entrega.
     * Utiliza 1 hora como tiempo estimado por defecto.
     * 
     * @param id Identificador único de la tarea
     * @param descripcion Descripción detallada de la tarea
     * @param departamento Departamento responsable
     * @param urgencia Nivel de urgencia textual ("Alta", "Media", "Baja")
     * @param prioridad Prioridad numérica (1-4, donde 1 es más crítica)
     * @param fechaEntrega Fecha límite en formato yyyy-MM-dd
     */
    public TareaPrioridad(String id, String descripcion, String departamento, String urgencia, int prioridad, String fechaEntrega) {
        super(id, descripcion, departamento, urgencia);
        this.prioridad = prioridad;
        this.fechaEntrega = fechaEntrega;
    }

    /**
     * Constructor completo que incluye horas estimadas para la tarea.
     * 
     * @param id Identificador único de la tarea
     * @param descripcion Descripción detallada de la tarea
     * @param departamento Departamento responsable
     * @param urgencia Nivel de urgencia textual
     * @param horasEstimadas Tiempo estimado en horas para completar la tarea
     * @param prioridad Prioridad numérica (1-4)
     * @param fechaEntrega Fecha límite en formato yyyy-MM-dd
     */
    public TareaPrioridad(String id, String descripcion, String departamento, String urgencia, int horasEstimadas, int prioridad, String fechaEntrega) {
        super(id, descripcion, departamento, urgencia, horasEstimadas);
        this.prioridad = prioridad;
        this.fechaEntrega = fechaEntrega;
    }

    // ===============================
    // MÉTODOS GETTER Y SETTER
    // ===============================

    /**
     * Obtiene el nivel de prioridad numérica de la tarea.
     * 
     * @return Número de prioridad (1=más crítica, 4=menos crítica)
     */
    public int getPrioridad() { 
        return prioridad; 
    }
    
    /**
     * Obtiene la fecha límite de entrega de la tarea.
     * 
     * @return Fecha en formato yyyy-MM-dd (ej: "2025-12-31")
     */
    public String getFechaEntrega() { 
        return fechaEntrega; 
    }

    /**
     * Modifica el nivel de prioridad de la tarea.
     * 
     * @param prioridad Nueva prioridad (1-4, donde 1 es más crítica)
     */
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Actualiza la fecha límite de entrega.
     * 
     * @param fechaEntrega Nueva fecha en formato yyyy-MM-dd
     */
    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    // ===============================
    // MÉTODOS DE UTILIDAD
    // ===============================

    /**
     * Convierte el número de prioridad a su representación textual.
     * Facilita la visualización en la interfaz de usuario.
     * 
     * @return Nombre textual de la prioridad ("Crítica", "Alta", "Media", "Baja")
     */
    public String getPrioridadNombre() {
        switch (prioridad) {
            case 1: return "Crítica";    // Máxima prioridad
            case 2: return "Alta";       // Prioridad elevada
            case 3: return "Media";      // Prioridad estándar
            case 4: return "Baja";       // Prioridad mínima
            default: return "No definida"; // Valor no válido
        }
    }

    // ===============================
    // IMPLEMENTACIÓN DE COMPARABLE
    // ===============================

    /**
     * Compara esta tarea con otra TareaPrioridad para determinar el orden.
     * 
     * Criterios de comparación (en orden de importancia):
     * 1. Prioridad numérica (1 tiene mayor prioridad que 2, 2 que 3, etc.)
     * 2. Fecha de entrega (si tienen la misma prioridad, la fecha más cercana tiene prioridad)
     * 
     * Este método permite que las TareaPrioridad se ordenen automáticamente
     * en estructuras como PriorityQueue, donde las tareas más urgentes
     * aparecen primero.
     * 
     * @param o La otra TareaPrioridad a comparar
     * @return Un valor negativo si esta tarea tiene mayor prioridad,
     *         positivo si tiene menor prioridad, 0 si son equivalentes
     */
    @Override
    public int compareTo(TareaPrioridad o) {
        // Comparar primero por prioridad (números menores = mayor prioridad)
        if (this.prioridad != o.prioridad) {
            return Integer.compare(this.prioridad, o.prioridad);
        }
        
        // Si tienen la misma prioridad, comparar por fecha de entrega
        // (fechas más cercanas tienen mayor prioridad)
        return this.fechaEntrega.compareTo(o.fechaEntrega);
    }

    // ===============================
    // MÉTODOS SOBRESCRITOS
    // ===============================

    /**
     * Proporciona una representación textual completa de la tarea con prioridad.
     * Incluye toda la información de la tarea base más los campos específicos
     * de prioridad y fecha de entrega.
     * 
     * @return Cadena con toda la información de la tarea prioritaria
     */
    @Override
    public String toString() {
        return "TareaPrioridad{" +
                "id='" + getId() + '\'' +
                ", descripcion='" + getDescripcion() + '\'' +
                ", departamento='" + getDepartamento() + '\'' +
                ", urgencia='" + getUrgencia() + '\'' +
                ", horasEstimadas=" + getHorasEstimadas() +
                ", prioridad=" + prioridad + " (" + getPrioridadNombre() + ")" +
                ", fechaEntrega='" + fechaEntrega + '\'' +
                '}';
    }
}