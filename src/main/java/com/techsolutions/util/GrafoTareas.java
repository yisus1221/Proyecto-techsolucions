package com.techsolutions.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.techsolutions.model.Tarea;

/**
 * Clase para manejar dependencias entre tareas usando un grafo
 */
public class GrafoTareas {
    private Map<String, List<String>> dependencias; // tarea -> lista de tareas de las que depende
    private Map<String, Tarea> tareas; // id -> tarea

    /**
     * Constructor
     */
    public GrafoTareas() {
        this.dependencias = new HashMap<>();
        this.tareas = new HashMap<>();
    }

    /**
     * Agrega una tarea al grafo
     */
    public void agregarTarea(Tarea tarea) {
        tareas.put(tarea.getId(), tarea);
        if (!dependencias.containsKey(tarea.getId())) {
            dependencias.put(tarea.getId(), new ArrayList<>());
        }
    }

    /**
     * Agrega una dependencia: tareaId depende de dependeDeId
     */
    public boolean agregarDependencia(String tareaId, String dependeDeId) {
        // Verificar que ambas tareas existen
        if (!tareas.containsKey(tareaId) || !tareas.containsKey(dependeDeId)) {
            return false;
        }

        // Verificar que no se cree un ciclo
        if (existeCiclo(tareaId, dependeDeId)) {
            return false;
        }

        // Agregar la dependencia
        dependencias.computeIfAbsent(tareaId, k -> new ArrayList<>()).add(dependeDeId);
        return true;
    }

    /**
     * Verifica si agregar una dependencia crearía un ciclo
     */
    private boolean existeCiclo(String desde, String hacia) {
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        
        cola.offer(hacia);
        visitados.add(hacia);
        
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            
            // Si llegamos de vuelta al nodo inicial, hay un ciclo
            if (actual.equals(desde)) {
                return true;
            }
            
            // Explorar dependencias del nodo actual
            List<String> deps = dependencias.get(actual);
            if (deps != null) {
                for (String dep : deps) {
                    if (!visitados.contains(dep)) {
                        visitados.add(dep);
                        cola.offer(dep);
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Obtiene las dependencias de una tarea
     */
    public List<String> obtenerDependencias(String tareaId) {
        return dependencias.getOrDefault(tareaId, new ArrayList<>());
    }

    /**
     * Obtiene todas las tareas que dependen de una tarea específica
     */
    public List<String> obtenerTareasDependientes(String tareaId) {
        List<String> dependientes = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : dependencias.entrySet()) {
            if (entry.getValue().contains(tareaId)) {
                dependientes.add(entry.getKey());
            }
        }
        
        return dependientes;
    }

    /**
     * Elimina una dependencia
     */
    public boolean eliminarDependencia(String tareaId, String dependeDeId) {
        List<String> deps = dependencias.get(tareaId);
        if (deps != null) {
            return deps.remove(dependeDeId);
        }
        return false;
    }

    /**
     * Elimina todas las dependencias de una tarea
     */
    public void eliminarTarea(String tareaId) {
        // Eliminar la tarea del mapa de tareas
        tareas.remove(tareaId);
        
        // Eliminar sus dependencias
        dependencias.remove(tareaId);
        
        // Eliminar referencias a esta tarea en otras dependencias
        for (List<String> deps : dependencias.values()) {
            deps.removeIf(id -> id.equals(tareaId));
        }
    }

    /**
     * Obtiene un orden topológico de las tareas (orden de ejecución recomendado)
     */
    public List<String> obtenerOrdenTopologico() {
        Map<String, Integer> gradoEntrada = new HashMap<>();
        Queue<String> cola = new LinkedList<>();
        List<String> resultado = new ArrayList<>();
        
        // Inicializar grado de entrada
        for (String tarea : tareas.keySet()) {
            gradoEntrada.put(tarea, 0);
        }
        
        // Calcular grado de entrada para cada tarea
        for (List<String> deps : dependencias.values()) {
            for (String dep : deps) {
                gradoEntrada.put(dep, gradoEntrada.getOrDefault(dep, 0) + 1);
            }
        }
        
        // Agregar tareas sin dependencias a la cola
        for (Map.Entry<String, Integer> entry : gradoEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                cola.offer(entry.getKey());
            }
        }
        
        // Procesamiento topológico
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            resultado.add(actual);
            
            // Reducir grado de entrada de tareas dependientes
            List<String> dependientes = obtenerTareasDependientes(actual);
            for (String dependiente : dependientes) {
                int nuevoGrado = gradoEntrada.get(dependiente) - 1;
                gradoEntrada.put(dependiente, nuevoGrado);
                
                if (nuevoGrado == 0) {
                    cola.offer(dependiente);
                }
            }
        }
        
        return resultado;
    }

    /**
     * Verifica si el grafo tiene ciclos
     */
    public boolean tieneCiclos() {
        List<String> ordenTopologico = obtenerOrdenTopologico();
        return ordenTopologico.size() != tareas.size();
    }

    /**
     * Obtiene una representación en texto del grafo
     */
    public String obtenerRepresentacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GRAFO DE DEPENDENCIAS ===\n");
        
        for (Map.Entry<String, List<String>> entry : dependencias.entrySet()) {
            String tareaId = entry.getKey();
            List<String> deps = entry.getValue();
            
            Tarea tarea = tareas.get(tareaId);
            String nombreTarea = tarea != null ? tarea.getDescripcion() : "Tarea desconocida";
            
            sb.append(tareaId).append(" (").append(nombreTarea).append(")");
            
            if (deps.isEmpty()) {
                sb.append(" -> Sin dependencias\n");
            } else {
                sb.append(" -> Depende de: ");
                for (int i = 0; i < deps.size(); i++) {
                    String depId = deps.get(i);
                    Tarea depTarea = tareas.get(depId);
                    String nombreDep = depTarea != null ? depTarea.getDescripcion() : "Tarea desconocida";
                    
                    sb.append(depId).append(" (").append(nombreDep).append(")");
                    if (i < deps.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * Obtiene el camino crítico (la secuencia más larga de tareas dependientes)
     */
    public List<String> obtenerCaminoCritico() {
        Map<String, Integer> distancias = new HashMap<>();
        Map<String, String> predecessores = new HashMap<>();
        
        // Inicializar distancias
        for (String tarea : tareas.keySet()) {
            distancias.put(tarea, 0);
        }
        
        // Aplicar algoritmo de camino más largo usando ordenamiento topológico
        List<String> ordenTopologico = obtenerOrdenTopologico();
        
        for (String actual : ordenTopologico) {
            List<String> dependientes = obtenerTareasDependientes(actual);
            for (String dependiente : dependientes) {
                Tarea tareaActual = tareas.get(actual);
                int pesoActual = tareaActual != null ? tareaActual.getHorasEstimadas() : 1;
                
                if (distancias.get(actual) + pesoActual > distancias.get(dependiente)) {
                    distancias.put(dependiente, distancias.get(actual) + pesoActual);
                    predecessores.put(dependiente, actual);
                }
            }
        }
        
        // Encontrar la tarea con la distancia máxima
        String tareaFinal = null;
        int maxDistancia = -1;
        for (Map.Entry<String, Integer> entry : distancias.entrySet()) {
            if (entry.getValue() > maxDistancia) {
                maxDistancia = entry.getValue();
                tareaFinal = entry.getKey();
            }
        }
        
        // Reconstruir el camino crítico
        List<String> caminoCritico = new ArrayList<>();
        String actual = tareaFinal;
        while (actual != null) {
            caminoCritico.add(0, actual);
            actual = predecessores.get(actual);
        }
        
        return caminoCritico;
    }

    /**
     * Obtiene estadísticas del grafo
     */
    public String obtenerEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL GRAFO ===\n");
        sb.append("Total de tareas: ").append(tareas.size()).append("\n");
        sb.append("Total de dependencias: ").append(contarDependencias()).append("\n");
        sb.append("Tiene ciclos: ").append(tieneCiclos() ? "Sí" : "No").append("\n");
        
        List<String> caminoCritico = obtenerCaminoCritico();
        sb.append("Camino crítico: ").append(caminoCritico.size()).append(" tareas\n");
        if (!caminoCritico.isEmpty()) {
            sb.append("Secuencia: ").append(String.join(" -> ", caminoCritico)).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Cuenta el número total de dependencias
     */
    private int contarDependencias() {
        int total = 0;
        for (List<String> deps : dependencias.values()) {
            total += deps.size();
        }
        return total;
    }

    /**
     * Verifica si una tarea puede ejecutarse (todas sus dependencias están completas)
     */
    public boolean puedeEjecutarse(String tareaId, Set<String> tareasCompletadas) {
        List<String> deps = dependencias.get(tareaId);
        if (deps == null || deps.isEmpty()) {
            return true;
        }
        
        for (String dep : deps) {
            if (!tareasCompletadas.contains(dep)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Obtiene las tareas que están listas para ejecutarse
     */
    public List<String> obtenerTareasListas(Set<String> tareasCompletadas) {
        List<String> tareasListas = new ArrayList<>();
        
        for (String tareaId : tareas.keySet()) {
            if (!tareasCompletadas.contains(tareaId) && puedeEjecutarse(tareaId, tareasCompletadas)) {
                tareasListas.add(tareaId);
            }
        }
        
        return tareasListas;
    }
}