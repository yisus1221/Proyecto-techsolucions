package com.techsolutions.model;

/**
 * Clase que representa un empleado en el sistema de gestión de TechSolutions S.A. de C.V.
 * 
 * Esta clase actúa como nodo en una estructura de Árbol Binario de Búsqueda (BST)
 * para organizar eficientemente a los empleados de la empresa. Cada empleado
 * puede tener referencias a empleados "izquierda" y "derecha" para formar el árbol.
 * 
 * Organización del Árbol:
 * - Los empleados se ordenan alfabéticamente por su ID
 * - Rama izquierda: empleados con ID menores (lexicográficamente)
 * - Rama derecha: empleados con ID mayores (lexicográficamente)
 * 
 * Funcionalidades del árbol:
 * - Búsqueda eficiente de empleados O(log n)
 * - Inserción ordenada automática
 * - Recorridos inorden, preorden, postorden
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class Empleado {
    
    // ===============================
    // ATRIBUTOS DEL EMPLEADO
    // ===============================
    
    /** Identificador único del empleado (ej: E1, E2, E3...) */
    private String id;
    
    /** Nombre completo del empleado */
    private String nombre;
    
    /** Departamento al que pertenece el empleado */
    private String departamento;
    
    // ===============================
    // REFERENCIAS DEL ÁRBOL BINARIO
    // ===============================
    
    /** Referencia al empleado de la rama izquierda (ID menor) */
    private Empleado izquierda;
    
    /** Referencia al empleado de la rama derecha (ID mayor) */
    private Empleado derecha;

    // ===============================
    // CONSTRUCTOR
    // ===============================

    /**
     * Constructor para crear un nuevo empleado.
     * Inicializa las referencias del árbol como null (nodo hoja).
     * 
     * @param id Identificador único del empleado (no puede ser null)
     * @param nombre Nombre completo del empleado (no puede ser null)
     * @param departamento Departamento al que pertenece (ej: "Desarrollo", "Marketing")
     */
    public Empleado(String id, String nombre, String departamento) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.izquierda = null;  // Inicialmente es un nodo hoja
        this.derecha = null;    // Inicialmente es un nodo hoja
    }

    // ===============================
    // MÉTODOS GETTER (ACCESO LECTURA)
    // ===============================

    /**
     * Obtiene el identificador único del empleado.
     * 
     * @return ID del empleado (ej: "E1", "E2", etc.)
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre completo del empleado.
     * 
     * @return Nombre del empleado
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el departamento al que pertenece el empleado.
     * 
     * @return Nombre del departamento
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Obtiene la referencia al empleado de la rama izquierda del árbol.
     * 
     * @return Empleado de la rama izquierda, o null si no existe
     */
    public Empleado getIzquierda() {
        return izquierda;
    }

    /**
     * Obtiene la referencia al empleado de la rama derecha del árbol.
     * 
     * @return Empleado de la rama derecha, o null si no existe
     */
    public Empleado getDerecha() {
        return derecha;
    }

    // ===============================
    // MÉTODOS SETTER (MODIFICACIÓN)
    // ===============================

    /**
     * Modifica el identificador del empleado.
     * PRECAUCIÓN: Cambiar el ID puede afectar el orden del árbol binario.
     * 
     * @param id Nuevo identificador único para el empleado
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Modifica el nombre del empleado.
     * 
     * @param nombre Nuevo nombre completo del empleado
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Cambia el departamento al que pertenece el empleado.
     * 
     * @param departamento Nuevo departamento asignado al empleado
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Establece la referencia al empleado de la rama izquierda.
     * Utilizado para construir y mantener la estructura del árbol binario.
     * 
     * @param izquierda Empleado que será el hijo izquierdo en el árbol
     */
    public void setIzquierda(Empleado izquierda) {
        this.izquierda = izquierda;
    }

    /**
     * Establece la referencia al empleado de la rama derecha.
     * Utilizado para construir y mantener la estructura del árbol binario.
     * 
     * @param derecha Empleado que será el hijo derecho en el árbol
     */
    public void setDerecha(Empleado derecha) {
        this.derecha = derecha;
    }

    // ===============================
    // MÉTODOS SOBRESCRITOS
    // ===============================

    /**
     * Proporciona una representación textual del empleado para depuración.
     * Incluye los atributos principales pero no las referencias del árbol
     * para evitar recursión infinita en la salida.
     * 
     * @return Cadena de texto con la información básica del empleado
     */
    @Override
    public String toString() {
        return "Empleado{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", departamento='" + departamento + '\'' +
                '}';
    }

    /**
     * Compara dos empleados para determinar si son iguales.
     * La comparación se basa únicamente en el ID del empleado.
     * Dos empleados son considerados iguales si tienen el mismo ID.
     * 
     * @param obj Objeto a comparar con este empleado
     * @return true si los empleados tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        // Verificar si es la misma referencia
        if (this == obj) return true;
        
        // Verificar si el objeto es null o de diferente clase
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // Convertir a Empleado y comparar por ID
        Empleado empleado = (Empleado) obj;
        return id != null && id.equals(empleado.id);
    }

    /**
     * Genera un código hash para el empleado basado en su ID.
     * Necesario para usar la clase en colecciones como HashMap o HashSet.
     * 
     * @return Código hash basado en el ID del empleado
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}