package com.techsolutions.model;

import java.util.List;

/**
 * Árbol binario para gestionar empleados organizados por departamento
 */
public class ArbolEmpleados {
    private Empleado raiz;

    public ArbolEmpleados() {
        this.raiz = null;
    }

    /**
     * Inserta un nuevo empleado en el árbol
     */
    public void insertar(Empleado nuevo) {
        raiz = insertarRec(raiz, nuevo);
    }

    /**
     * Método recursivo para insertar empleado
     */
    private Empleado insertarRec(Empleado actual, Empleado nuevo) {
        if (actual == null) {
            return nuevo;
        }
        
        if (nuevo.getDepartamento().compareTo(actual.getDepartamento()) < 0) {
            actual.setIzquierda(insertarRec(actual.getIzquierda(), nuevo));
        } else {
            actual.setDerecha(insertarRec(actual.getDerecha(), nuevo));
        }
        
        return actual;
    }

    /**
     * Busca empleados por departamento
     * Si departamento es null o vacío, retorna todos los empleados
     */
    public void buscarPorDepartamento(String departamento, List<Empleado> resultado) {
        if (departamento == null || departamento.trim().isEmpty()) {
            buscarTodos(raiz, resultado);
        } else {
            buscarRec(raiz, departamento, resultado);
        }
    }

    /**
     * Obtiene todos los empleados del árbol
     */
    private void buscarTodos(Empleado actual, List<Empleado> resultado) {
        if (actual == null) return;
        
        resultado.add(actual);
        buscarTodos(actual.getIzquierda(), resultado);
        buscarTodos(actual.getDerecha(), resultado);
    }

    /**
     * Búsqueda recursiva por departamento específico
     */
    private void buscarRec(Empleado actual, String departamento, List<Empleado> resultado) {
        if (actual == null) return;
        
        if (actual.getDepartamento().equalsIgnoreCase(departamento)) {
            resultado.add(actual);
        }
        
        buscarRec(actual.getIzquierda(), departamento, resultado);
        buscarRec(actual.getDerecha(), departamento, resultado);
    }

    /**
     * Busca un empleado específico por ID
     */
    public Empleado buscarPorId(String id) {
        return buscarPorIdRec(raiz, id);
    }

    /**
     * Búsqueda recursiva por ID
     */
    private Empleado buscarPorIdRec(Empleado actual, String id) {
        if (actual == null) return null;
        
        if (actual.getId().equals(id)) {
            return actual;
        }
        
        Empleado encontrado = buscarPorIdRec(actual.getIzquierda(), id);
        if (encontrado != null) return encontrado;
        
        return buscarPorIdRec(actual.getDerecha(), id);
    }

    /**
     * Obtiene la raíz del árbol
     */
    public Empleado getRaiz() {
        return raiz;
    }

    /**
     * Verifica si el árbol está vacío
     */
    public boolean estaVacio() {
        return raiz == null;
    }

    /**
     * Cuenta el número total de empleados en el árbol
     */
    public int contarEmpleados() {
        return contarRec(raiz);
    }

    /**
     * Método recursivo para contar empleados
     */
    private int contarRec(Empleado actual) {
        if (actual == null) return 0;
        return 1 + contarRec(actual.getIzquierda()) + contarRec(actual.getDerecha());
    }

    /**
     * Obtiene la altura del árbol
     */
    public int obtenerAltura() {
        return obtenerAlturaRec(raiz);
    }

    /**
     * Método recursivo para obtener la altura
     */
    private int obtenerAlturaRec(Empleado actual) {
        if (actual == null) return 0;
        return 1 + Math.max(obtenerAlturaRec(actual.getIzquierda()), 
                           obtenerAlturaRec(actual.getDerecha()));
    }

    /**
     * Representación en String del árbol (recorrido inorden)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringInorden(raiz, sb, 0);
        return sb.toString();
    }

    /**
     * Recorrido inorden para mostrar la estructura del árbol
     */
    private void toStringInorden(Empleado actual, StringBuilder sb, int nivel) {
        if (actual == null) return;
        
        toStringInorden(actual.getDerecha(), sb, nivel + 1);
        
        for (int i = 0; i < nivel; i++) {
            sb.append("    ");
        }
        sb.append(actual.getNombre()).append(" (").append(actual.getDepartamento()).append(")\n");
        
        toStringInorden(actual.getIzquierda(), sb, nivel + 1);
    }
}