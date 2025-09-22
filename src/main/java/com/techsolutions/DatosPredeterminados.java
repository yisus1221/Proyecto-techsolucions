package com.techsolutions;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * GENERADOR DE DATOS PREDETERMINADOS - TechSolutions S.A. de C.V.
 * 
 * Esta clase se encarga de poblar la base de datos MongoDB con datos
 * de ejemplo para demostrar todas las funcionalidades del sistema
 * de gestión de tareas.
 * 
 * DATOS INCLUIDOS:
 * ================
 * 
 * TAREAS (25 tareas variadas):
 * - Tareas urgentes para demostrar la Pila (Stack)
 * - Tareas programadas para demostrar la Cola (Queue)
 * - Tareas departamentales para demostrar la Lista (List)
 * - Tareas con prioridad para demostrar la Cola de Prioridad
 * - Tareas con dependencias para demostrar Grafos
 * 
 * EMPLEADOS (25 empleados diversos):
 * - Empleados de diferentes departamentos
 * - Información completa: ID, nombre, departamento, puesto, email
 * - Datos organizados para demostrar el Árbol Binario de Búsqueda
 * 
 * DEPARTAMENTOS REPRESENTADOS:
 * - Desarrollo (6 empleados)
 * - Seguridad (2 empleados)
 * - Marketing (3 empleados)
 * - Infraestructura (3 empleados)
 * - Soporte Técnico (3 empleados)
 * - Recursos Humanos (2 empleados)
 * - Contabilidad (2 empleados)
 * - Administración (2 empleados)
 * - Ventas (2 empleados)
 * - Legal (1 empleado)
 * 
 * USO:
 * ====
 * Ejecutar esta clase antes de usar el sistema principal para
 * cargar datos de ejemplo en MongoDB.
 * 
 * @author TechSolutions Development Team
 * @version 1.0.0
 * @since 2025-09-21
 */
public class DatosPredeterminados {
    
    /**
     * Método principal que ejecuta la carga de datos predeterminados.
     * 
     * Proceso de carga:
     * 1. Establece conexión con MongoDB
     * 2. Limpia las colecciones existentes
     * 3. Inserta 25 tareas variadas con diferentes tipos y prioridades
     * 4. Inserta 25 empleados de diversos departamentos
     * 5. Confirma la inserción exitosa
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Conexión automática con try-with-resources para manejo seguro
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            
            // Obtener base de datos y colecciones
            MongoDatabase database = mongoClient.getDatabase("techsolutions");
            MongoCollection<Document> tareas = database.getCollection("tareas");
            MongoCollection<Document> empleados = database.getCollection("empleados");

            // Limpiar colecciones existentes para evitar duplicados
            System.out.println("🗑️ Limpiando colecciones existentes...");
            tareas.deleteMany(new Document());
            empleados.deleteMany(new Document());

            // ===============================================
            // INSERCIÓN DE TAREAS PREDETERMINADAS
            // ===============================================
            System.out.println("📋 Insertando tareas predeterminadas...");
            tareas.insertMany(Arrays.asList(
                // TAREAS DE DESARROLLO
                new Document("id", "T1")
                        .append("descripcion", "Revisar código de módulo A")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 4)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-09-20"),
                new Document("id", "T2")
                        .append("descripcion", "Actualizar documentación")
                        .append("departamento", "Soporte Técnico")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 2)
                        .append("tipo", "programada")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-09-25"),
                new Document("id", "T3")
                        .append("descripcion", "Capacitación interna")
                        .append("departamento", "Recursos Humanos")
                        .append("urgencia", "Baja")
                        .append("horasEstimadas", 3)
                        .append("tipo", "departamento"),
                new Document("id", "T4")
                        .append("descripcion", "Implementar nueva funcionalidad")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 6)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-09-22")
                        .append("dependencias", Arrays.asList("T1")),
                new Document("id", "T5")
                        .append("descripcion", "Revisión de hardware")
                        .append("departamento", "Soporte Técnico")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 2)
                        .append("tipo", "programada")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-09-28"),
                
                // Nuevas tareas variadas
                new Document("id", "T6")
                        .append("descripcion", "Diseño de base de datos para nuevo módulo")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 8)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-09-30"),
                new Document("id", "T7")
                        .append("descripcion", "Auditoría de seguridad del sistema")
                        .append("departamento", "Seguridad")
                        .append("urgencia", "Crítica")
                        .append("horasEstimadas", 12)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-09-23"),
                new Document("id", "T8")
                        .append("descripcion", "Migración de datos legacy")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 16)
                        .append("tipo", "programada")
                        .append("prioridad", 3)
                        .append("fechaEntrega", "2025-10-15"),
                new Document("id", "T9")
                        .append("descripcion", "Configurar servidor de pruebas")
                        .append("departamento", "Infraestructura")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 4)
                        .append("tipo", "programada")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-10-01"),
                new Document("id", "T10")
                        .append("descripcion", "Presentación de resultados trimestrales")
                        .append("departamento", "Administración")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 6)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-09-29"),
                new Document("id", "T11")
                        .append("descripcion", "Análisis de mercado Q4")
                        .append("departamento", "Marketing")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 10)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-10-10"),
                new Document("id", "T12")
                        .append("descripcion", "Optimización de consultas de base de datos")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Baja")
                        .append("horasEstimadas", 8)
                        .append("tipo", "programada")
                        .append("prioridad", 4)
                        .append("fechaEntrega", "2025-11-01"),
                new Document("id", "T13")
                        .append("descripcion", "Implementar sistema de backup automático")
                        .append("departamento", "Infraestructura")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 12)
                        .append("tipo", "urgente")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-10-05")
                        .append("dependencias", Arrays.asList("T9")),
                new Document("id", "T14")
                        .append("descripcion", "Curso de actualización tecnológica")
                        .append("departamento", "Recursos Humanos")
                        .append("urgencia", "Baja")
                        .append("horasEstimadas", 20)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-11-15"),
                new Document("id", "T15")
                        .append("descripcion", "Revisión de políticas de privacidad")
                        .append("departamento", "Legal")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 6)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-10-20"),
                new Document("id", "T16")
                        .append("descripcion", "Desarrollo de API RESTful")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 15)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-10-08")
                        .append("dependencias", Arrays.asList("T6")),
                new Document("id", "T17")
                        .append("descripcion", "Análisis de vulnerabilidades web")
                        .append("departamento", "Seguridad")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 8)
                        .append("tipo", "urgente")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-09-27"),
                new Document("id", "T18")
                        .append("descripcion", "Campaña publicitaria digital")
                        .append("departamento", "Marketing")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 25)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-10-30"),
                new Document("id", "T19")
                        .append("descripcion", "Instalación de nuevo software contable")
                        .append("departamento", "Contabilidad")
                        .append("urgencia", "Alta")
                        .append("horasEstimadas", 8)
                        .append("tipo", "programada")
                        .append("prioridad", 2)
                        .append("fechaEntrega", "2025-10-03"),
                new Document("id", "T20")
                        .append("descripcion", "Mantenimiento preventivo de servidores")
                        .append("departamento", "Infraestructura")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 6)
                        .append("tipo", "programada")
                        .append("prioridad", 3)
                        .append("fechaEntrega", "2025-10-12"),
                new Document("id", "T21")
                        .append("descripcion", "Implementar autenticación de dos factores")
                        .append("departamento", "Seguridad")
                        .append("urgencia", "Crítica")
                        .append("horasEstimadas", 10)
                        .append("tipo", "urgente")
                        .append("prioridad", 1)
                        .append("fechaEntrega", "2025-09-25")
                        .append("dependencias", Arrays.asList("T7")),
                new Document("id", "T22")
                        .append("descripcion", "Evaluación de desempeño anual")
                        .append("departamento", "Recursos Humanos")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 30)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-12-01"),
                new Document("id", "T23")
                        .append("descripcion", "Actualización de sistemas operativos")
                        .append("departamento", "Soporte Técnico")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 8)
                        .append("tipo", "programada")
                        .append("prioridad", 3)
                        .append("fechaEntrega", "2025-10-15"),
                new Document("id", "T24")
                        .append("descripcion", "Desarrollo de módulo de reportes")
                        .append("departamento", "Desarrollo")
                        .append("urgencia", "Baja")
                        .append("horasEstimadas", 20)
                        .append("tipo", "programada")
                        .append("prioridad", 4)
                        .append("fechaEntrega", "2025-11-10")
                        .append("dependencias", Arrays.asList("T16")),
                new Document("id", "T25")
                        .append("descripcion", "Negociación con proveedores cloud")
                        .append("departamento", "Administración")
                        .append("urgencia", "Media")
                        .append("horasEstimadas", 12)
                        .append("tipo", "departamento")
                        .append("fechaEntrega", "2025-10-25")
            ));

            // Empleados de ejemplo
            empleados.insertMany(Arrays.asList(
                // Desarrollo
                new Document("id", "E1")
                        .append("nombre", "Juan Pérez")
                        .append("departamento", "Desarrollo")
                        .append("puesto", "Desarrollador Senior")
                        .append("email", "juan.perez@techsolutions.com"),
                new Document("id", "E2")
                        .append("nombre", "Ana López")
                        .append("departamento", "Soporte Técnico")
                        .append("puesto", "Especialista en Soporte")
                        .append("email", "ana.lopez@techsolutions.com"),
                new Document("id", "E3")
                        .append("nombre", "Carlos Ruiz")
                        .append("departamento", "Recursos Humanos")
                        .append("puesto", "Coordinador de RRHH")
                        .append("email", "carlos.ruiz@techsolutions.com"),
                new Document("id", "E4")
                        .append("nombre", "María Gómez")
                        .append("departamento", "Administración")
                        .append("puesto", "Gerente Administrativo")
                        .append("email", "maria.gomez@techsolutions.com"),
                new Document("id", "E5")
                        .append("nombre", "Luis Torres")
                        .append("departamento", "Ventas")
                        .append("puesto", "Ejecutivo de Ventas")
                        .append("email", "luis.torres@techsolutions.com"),
                
                // Nuevos empleados
                new Document("id", "E6")
                        .append("nombre", "Sofia Martínez")
                        .append("departamento", "Desarrollo")
                        .append("puesto", "Desarrolladora Full Stack")
                        .append("email", "sofia.martinez@techsolutions.com"),
                new Document("id", "E7")
                        .append("nombre", "Roberto Silva")
                        .append("departamento", "Seguridad")
                        .append("puesto", "Especialista en Ciberseguridad")
                        .append("email", "roberto.silva@techsolutions.com"),
                new Document("id", "E8")
                        .append("nombre", "Elena Vásquez")
                        .append("departamento", "Marketing")
                        .append("puesto", "Coordinadora de Marketing Digital")
                        .append("email", "elena.vasquez@techsolutions.com"),
                new Document("id", "E9")
                        .append("nombre", "Diego Herrera")
                        .append("departamento", "Infraestructura")
                        .append("puesto", "Administrador de Sistemas")
                        .append("email", "diego.herrera@techsolutions.com"),
                new Document("id", "E10")
                        .append("nombre", "Carmen Jiménez")
                        .append("departamento", "Contabilidad")
                        .append("puesto", "Contadora Principal")
                        .append("email", "carmen.jimenez@techsolutions.com"),
                new Document("id", "E11")
                        .append("nombre", "Fernando Castro")
                        .append("departamento", "Desarrollo")
                        .append("puesto", "Arquitecto de Software")
                        .append("email", "fernando.castro@techsolutions.com"),
                new Document("id", "E12")
                        .append("nombre", "Patricia Morales")
                        .append("departamento", "Legal")
                        .append("puesto", "Asesora Legal")
                        .append("email", "patricia.morales@techsolutions.com"),
                new Document("id", "E13")
                        .append("nombre", "Andrés Ramírez")
                        .append("departamento", "Soporte Técnico")
                        .append("puesto", "Técnico Senior")
                        .append("email", "andres.ramirez@techsolutions.com"),
                new Document("id", "E14")
                        .append("nombre", "Lucía Fernández")
                        .append("departamento", "Recursos Humanos")
                        .append("puesto", "Especialista en Capacitación")
                        .append("email", "lucia.fernandez@techsolutions.com"),
                new Document("id", "E15")
                        .append("nombre", "Gabriel Mendoza")
                        .append("departamento", "Seguridad")
                        .append("puesto", "Auditor de Seguridad")
                        .append("email", "gabriel.mendoza@techsolutions.com"),
                new Document("id", "E16")
                        .append("nombre", "Valentina Cruz")
                        .append("departamento", "Marketing")
                        .append("puesto", "Analista de Mercadeo")
                        .append("email", "valentina.cruz@techsolutions.com"),
                new Document("id", "E17")
                        .append("nombre", "Sebastián Rojas")
                        .append("departamento", "Infraestructura")
                        .append("puesto", "Ingeniero DevOps")
                        .append("email", "sebastian.rojas@techsolutions.com"),
                new Document("id", "E18")
                        .append("nombre", "Isabella García")
                        .append("departamento", "Desarrollo")
                        .append("puesto", "Desarrolladora Frontend")
                        .append("email", "isabella.garcia@techsolutions.com"),
                new Document("id", "E19")
                        .append("nombre", "Nicolás Vargas")
                        .append("departamento", "Administración")
                        .append("puesto", "Asistente Administrativo")
                        .append("email", "nicolas.vargas@techsolutions.com"),
                new Document("id", "E20")
                        .append("nombre", "Camila Soto")
                        .append("departamento", "Ventas")
                        .append("puesto", "Gerente de Ventas")
                        .append("email", "camila.soto@techsolutions.com"),
                new Document("id", "E21")
                        .append("nombre", "Mateo Álvarez")
                        .append("departamento", "Desarrollo")
                        .append("puesto", "Desarrollador Backend")
                        .append("email", "mateo.alvarez@techsolutions.com"),
                new Document("id", "E22")
                        .append("nombre", "Daniela Restrepo")
                        .append("departamento", "Contabilidad")
                        .append("puesto", "Auxiliar Contable")
                        .append("email", "daniela.restrepo@techsolutions.com"),
                new Document("id", "E23")
                        .append("nombre", "Santiago Ospina")
                        .append("departamento", "Soporte Técnico")
                        .append("puesto", "Coordinador de Soporte")
                        .append("email", "santiago.ospina@techsolutions.com"),
                new Document("id", "E24")
                        .append("nombre", "Mariana León")
                        .append("departamento", "Marketing")
                        .append("puesto", "Diseñadora Gráfica")
                        .append("email", "mariana.leon@techsolutions.com"),
                new Document("id", "E25")
                        .append("nombre", "Alejandro Parra")
                        .append("departamento", "Infraestructura")
                        .append("puesto", "Especialista en Redes")
                        .append("email", "alejandro.parra@techsolutions.com")
            ));

            // ===============================================
            // CONFIRMACIÓN DE CARGA EXITOSA
            // ===============================================
            
            System.out.println("✅ ¡Datos predeterminados insertados correctamente!");
            System.out.println("📊 Resumen de datos cargados:");
            System.out.println("   📋 Tareas: 25 (variadas por tipo, urgencia y departamento)");
            System.out.println("   👥 Empleados: 25 (distribuidos en 10 departamentos)");
            System.out.println("🚀 El sistema está listo para usar con datos de ejemplo.");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos predeterminados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}