package com.techsolutions.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techsolutions.model.Tarea;
import com.techsolutions.model.TareaPrioridad;

/**
 * Clase de utilidades para el sistema de gestión de tareas
 */
public class Utilidades {

    // ==================== VALIDACIONES ====================

    /**
     * Valida que un ID de tarea tenga el formato correcto
     */
    public static boolean validarIdTarea(String id) {
        return id != null && id.matches("T\\d+");
    }

    /**
     * Valida que un ID de empleado tenga el formato correcto
     */
    public static boolean validarIdEmpleado(String id) {
        return id != null && id.matches("EMP\\d+");
    }

    /**
     * Valida que una fecha tenga el formato yyyy-MM-dd
     */
    public static boolean validarFormatoFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida que un texto no esté vacío o sea solo espacios
     */
    public static boolean validarTextoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Valida que un valor de prioridad esté en el rango válido (1-3)
     */
    public static boolean validarPrioridad(int prioridad) {
        return prioridad >= 1 && prioridad <= 3;
    }

    /**
     * Valida que las horas estimadas sean un valor positivo
     */
    public static boolean validarHorasEstimadas(int horas) {
        return horas > 0 && horas <= 999; // Límite razonable
    }

    // ==================== ALGORITMOS DE ORDENAMIENTO ====================

    /**
     * Ordena tareas por prioridad usando QuickSort
     */
    public static void ordenarTareasPorPrioridad(List<TareaPrioridad> tareas) {
        if (tareas == null || tareas.size() <= 1) return;
        quickSortPrioridad(tareas, 0, tareas.size() - 1);
    }

    private static void quickSortPrioridad(List<TareaPrioridad> tareas, int bajo, int alto) {
        if (bajo < alto) {
            int pi = particionPrioridad(tareas, bajo, alto);
            quickSortPrioridad(tareas, bajo, pi - 1);
            quickSortPrioridad(tareas, pi + 1, alto);
        }
    }

    private static int particionPrioridad(List<TareaPrioridad> tareas, int bajo, int alto) {
        TareaPrioridad pivot = tareas.get(alto);
        int i = bajo - 1;
        
        for (int j = bajo; j < alto; j++) {
            if (tareas.get(j).getPrioridad() <= pivot.getPrioridad()) {
                i++;
                intercambiar(tareas, i, j);
            }
        }
        
        intercambiar(tareas, i + 1, alto);
        return i + 1;
    }

    private static void intercambiar(List<TareaPrioridad> tareas, int i, int j) {
        TareaPrioridad temp = tareas.get(i);
        tareas.set(i, tareas.get(j));
        tareas.set(j, temp);
    }

    /**
     * Ordena tareas por departamento usando MergeSort
     */
    public static void ordenarTareasPorDepartamento(List<Tarea> tareas) {
        if (tareas == null || tareas.size() <= 1) return;
        mergeSortDepartamento(tareas, 0, tareas.size() - 1);
    }

    private static void mergeSortDepartamento(List<Tarea> tareas, int izq, int der) {
        if (izq < der) {
            int medio = (izq + der) / 2;
            mergeSortDepartamento(tareas, izq, medio);
            mergeSortDepartamento(tareas, medio + 1, der);
            merge(tareas, izq, medio, der);
        }
    }

    private static void merge(List<Tarea> tareas, int izq, int medio, int der) {
        List<Tarea> temp = new ArrayList<>();
        int i = izq, j = medio + 1;
        
        while (i <= medio && j <= der) {
            if (tareas.get(i).getDepartamento().compareTo(tareas.get(j).getDepartamento()) <= 0) {
                temp.add(tareas.get(i++));
            } else {
                temp.add(tareas.get(j++));
            }
        }
        
        while (i <= medio) temp.add(tareas.get(i++));
        while (j <= der) temp.add(tareas.get(j++));
        
        for (int k = 0; k < temp.size(); k++) {
            tareas.set(izq + k, temp.get(k));
        }
    }

    // ==================== ALGORITMOS DE BÚSQUEDA ====================

    /**
     * Búsqueda binaria de tarea por ID (requiere lista ordenada por ID)
     */
    public static Tarea busquedaBinariaPorId(List<Tarea> tareas, String id) {
        if (tareas == null || tareas.isEmpty() || id == null) return null;
        
        int izq = 0, der = tareas.size() - 1;
        
        while (izq <= der) {
            int medio = (izq + der) / 2;
            Tarea tareaMedio = tareas.get(medio);
            int comparacion = tareaMedio.getId().compareTo(id);
            
            if (comparacion == 0) {
                return tareaMedio;
            } else if (comparacion < 0) {
                izq = medio + 1;
            } else {
                der = medio - 1;
            }
        }
        
        return null;
    }

    /**
     * Búsqueda lineal de tareas por departamento
     */
    public static List<Tarea> buscarTareasPorDepartamento(List<Tarea> tareas, String departamento) {
        List<Tarea> resultado = new ArrayList<>();
        
        if (tareas == null || departamento == null) return resultado;
        
        for (Tarea tarea : tareas) {
            if (tarea.getDepartamento().equalsIgnoreCase(departamento)) {
                resultado.add(tarea);
            }
        }
        
        return resultado;
    }

    // ==================== ALGORITMOS RECURSIVOS ====================

    /**
     * Calcula el tiempo total de tareas usando recursión
     */
    public static int calcularTiempoTotalRecursivo(List<Tarea> tareas) {
        return calcularTiempoRecursivo(tareas, 0);
    }

    private static int calcularTiempoRecursivo(List<Tarea> tareas, int indice) {
        if (indice >= tareas.size()) return 0;
        return tareas.get(indice).getHorasEstimadas() + calcularTiempoRecursivo(tareas, indice + 1);
    }

    /**
     * Distribuye tareas usando divide y vencerás
     */
    public static Map<String, List<Tarea>> distribuirTareasDivideVenceras(List<Tarea> tareas, String[] equipos) {
        Map<String, List<Tarea>> distribucion = new HashMap<>();
        for (String equipo : equipos) {
            distribucion.put(equipo, new ArrayList<>());
        }
        
        if (tareas.isEmpty() || equipos.length == 0) return distribucion;
        
        distribuirRecursivo(tareas, 0, tareas.size() - 1, equipos, 0, distribucion);
        return distribucion;
    }

    private static void distribuirRecursivo(List<Tarea> tareas, int inicio, int fin, 
                                          String[] equipos, int equipoActual, 
                                          Map<String, List<Tarea>> distribucion) {
        if (inicio > fin) return;
        
        int medio = (inicio + fin) / 2;
        String equipo = equipos[equipoActual % equipos.length];
        distribucion.get(equipo).add(tareas.get(medio));
        
        distribuirRecursivo(tareas, inicio, medio - 1, equipos, equipoActual + 1, distribucion);
        distribuirRecursivo(tareas, medio + 1, fin, equipos, equipoActual + 1, distribucion);
    }

    // ==================== UTILIDADES DE FECHA ====================

    /**
     * Obtiene la fecha actual en formato yyyy-MM-dd
     */
    public static String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * Agrega días a una fecha
     */
    public static String agregarDias(String fecha, int dias) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(fecha);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, dias);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return fecha;
        }
    }

    /**
     * Calcula los días entre dos fechas
     */
    public static long calcularDiasEntre(String fechaInicio, String fechaFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date inicio = sdf.parse(fechaInicio);
            Date fin = sdf.parse(fechaFin);
            long diferencia = fin.getTime() - inicio.getTime();
            return diferencia / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== UTILIDADES DE ESTADÍSTICAS ====================

    /**
     * Calcula estadísticas de tareas por departamento
     */
    public static Map<String, Integer> obtenerEstadisticasPorDepartamento(List<Tarea> tareas) {
        Map<String, Integer> estadisticas = new HashMap<>();
        
        for (Tarea tarea : tareas) {
            String depto = tarea.getDepartamento();
            estadisticas.put(depto, estadisticas.getOrDefault(depto, 0) + 1);
        }
        
        return estadisticas;
    }

    /**
     * Calcula el promedio de horas estimadas
     */
    public static double calcularPromedioHoras(List<Tarea> tareas) {
        if (tareas.isEmpty()) return 0.0;
        
        int total = calcularTiempoTotalRecursivo(tareas);
        return (double) total / tareas.size();
    }

    /**
     * Encuentra la tarea con más horas estimadas
     */
    public static Tarea encontrarTareaMasLarga(List<Tarea> tareas) {
        if (tareas.isEmpty()) return null;
        
        return tareas.stream()
                   .max(Comparator.comparingInt(Tarea::getHorasEstimadas))
                   .orElse(null);
    }

    // ==================== UTILIDADES DE FORMATO ====================

    /**
     * Convierte valor de prioridad a nombre
     */
    public static String prioridadANombre(int prioridad) {
        switch (prioridad) {
            case 1: return "Alta";
            case 2: return "Media";
            case 3: return "Baja";
            default: return "No definida";
        }
    }

    /**
     * Convierte nombre de prioridad a valor
     */
    public static int nombreAPrioridad(String nombre) {
        switch (nombre.toLowerCase()) {
            case "alta": return 1;
            case "media": return 2;
            case "baja": return 3;
            default: return 2; // Media por defecto
        }
    }

    /**
     * Formatea tiempo en horas a texto legible
     */
    public static String formatearTiempo(int horas) {
        if (horas < 24) {
            return horas + (horas == 1 ? " hora" : " horas");
        } else {
            int dias = horas / 24;
            int horasRestantes = horas % 24;
            String resultado = dias + (dias == 1 ? " día" : " días");
            if (horasRestantes > 0) {
                resultado += " y " + horasRestantes + (horasRestantes == 1 ? " hora" : " horas");
            }
            return resultado;
        }
    }

    /**
     * Capitaliza la primera letra de un texto
     */
    public static String capitalizarPrimeraLetra(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    /**
     * Trunca un texto a una longitud máxima
     */
    public static String truncarTexto(String texto, int longitudMaxima) {
        if (texto == null) return "";
        if (texto.length() <= longitudMaxima) return texto;
        return texto.substring(0, longitudMaxima - 3) + "...";
    }

    // ==================== UTILIDADES DE IDENTIFICADORES ====================

    /**
     * Extrae el número de un ID (ej: "T123" -> 123)
     */
    public static int extraerNumeroId(String id) {
        if (id == null || id.length() < 2) return 0;
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Genera el siguiente ID en secuencia
     */
    public static String generarSiguienteId(String prefijo, List<String> idsExistentes) {
        int max = 0;
        String patron = prefijo + "\\d+";
        
        for (String id : idsExistentes) {
            if (id.matches(patron)) {
                int numero = extraerNumeroId(id);
                if (numero > max) max = numero;
            }
        }
        
        return prefijo + (max + 1);
    }
}