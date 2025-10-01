package com.techsolutions.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techsolutions.db.MongoDBManager;
import com.techsolutions.model.Empleado;
import com.techsolutions.model.Usuario;

/**
 * Servicio para gestionar usuarios y autenticación del sistema
 */
public class UsuarioService {
    
    private static UsuarioService instance;
    private Map<String, Usuario> usuarios;
    private Usuario usuarioActual;
    
    private UsuarioService() {
        this.usuarios = new HashMap<>();
        inicializarUsuariosPredeterminados();
        crearCuentasParaEmpleadosDeDB();
    }
    
    public static UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }
    
    /**
     * Inicializa usuarios predeterminados del sistema
     */
    private void inicializarUsuariosPredeterminados() {
        
        // Usuario CEO - Acceso total al sistema
        Usuario ceo = new Usuario("ceo", "ceo123", Usuario.Rol.CEO, "Direccion Ejecutiva");
        ceo.setId("U000");
        ceo.setNombre("CEO TechSolutions");
        ceo.setEmail("ceo@techsolutions.com");
        usuarios.put("ceo", ceo);


        // Usuario jefe principal
        Usuario jefe = new Usuario("jefe", "jefe123", Usuario.Rol.JEFE, "Direccion General");
        jefe.setId("U001");
        jefe.setNombre("Director General");
        jefe.setEmail("director@techsolutions.com");
        usuarios.put("jefe", jefe);

    }
    
    /**
     * Crea cuentas automáticamente para todos los empleados de la base de datos
     */
    private void crearCuentasParaEmpleadosDeDB() {
        try {
            MongoDBManager dbManager = MongoDBManager.getInstance();
            List<Empleado> empleadosDB = dbManager.obtenerTodosLosEmpleados();
            
            // Agrupar empleados por departamento
            Map<String, List<Empleado>> empleadosPorDepartamento = agruparPorDepartamento(empleadosDB);
            
            int cuentasCreadas = 0;
            int jefesAsignados = 0;
            System.out.println("Iniciando creacion automatica de cuentas...");
            System.out.println("Departamentos encontrados: " + empleadosPorDepartamento.size());
            
            for (Map.Entry<String, List<Empleado>> entry : empleadosPorDepartamento.entrySet()) {
                String departamento = entry.getKey();
                List<Empleado> empleadosDept = entry.getValue();
                
                System.out.println("\nProcesando departamento: " + departamento + " (" + empleadosDept.size() + " empleados)");
                
                // Designar el primer empleado como jefe de departamento
                boolean jefeAsignado = false;
                
                for (int i = 0; i < empleadosDept.size(); i++) {
                    Empleado empleado = empleadosDept.get(i);
                    String username = generarUsername(empleado);
                    
                    if (!usuarios.containsKey(username)) {
                        String password = "emp" + empleado.getId().toLowerCase();
                        Usuario.Rol rol;
                        
                        // Asignar roles: primer empleado = jefe, resto = empleados
                        if (!jefeAsignado && esElegibleParaJefe(empleado)) {
                            rol = Usuario.Rol.JEFE_DEPARTAMENTO;
                            jefeAsignado = true;
                            jefesAsignados++;
                            System.out.println("  *** JEFE: " + empleado.getNombre() + " | Usuario: " + username + " | Contraseña: " + password + " ***");
                        } else {
                            rol = Usuario.Rol.EMPLEADO;
                            System.out.println("  EMPLEADO: " + empleado.getNombre() + " | Usuario: " + username + " | Contraseña: " + password);
                        }
                        
                        Usuario nuevoUsuario = new Usuario(username, password, rol, departamento);
                        nuevoUsuario.setId("AUTO_" + empleado.getId());
                        nuevoUsuario.setNombre(empleado.getNombre());
                        nuevoUsuario.setEmail(generarEmail(empleado));
                        nuevoUsuario.setActivo(true);
                        
                        usuarios.put(username, nuevoUsuario);
                        cuentasCreadas++;
                    }
                }
            }
            
            System.out.println("\n=== RESUMEN DE CREACION DE CUENTAS ===");
            System.out.println("Total de cuentas creadas: " + cuentasCreadas);
            System.out.println("Jefes de departamento asignados: " + jefesAsignados);
            System.out.println("Departamentos procesados: " + empleadosPorDepartamento.size());
            System.out.println("==========================================\n");
            
        } catch (Exception e) {
            System.err.println("Error al crear cuentas automaticas: " + e.getMessage());
        }
    }
    
    /**
     * Agrupa empleados por departamento
     */
    private Map<String, List<Empleado>> agruparPorDepartamento(List<Empleado> empleados) {
        Map<String, List<Empleado>> grupos = new HashMap<>();
        
        for (Empleado empleado : empleados) {
            String dept = empleado.getDepartamento();
            if (dept == null || dept.trim().isEmpty()) {
                dept = "Sin Departamento";
            }
            grupos.computeIfAbsent(dept, k -> new ArrayList<>()).add(empleado);
        }
        
        return grupos;
    }
    
    /**
     * Determina si un empleado es elegible para ser jefe
     */
    private boolean esElegibleParaJefe(Empleado empleado) {
        return empleado != null && 
               empleado.getNombre() != null && 
               !empleado.getNombre().trim().isEmpty();
    }
    
    /**
     * Genera un username único para el empleado
     */
    private String generarUsername(Empleado empleado) {
        return empleado.getNombre().toLowerCase()
                .replace(" ", "_")
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u").replace("ñ", "n");
    }
    
    /**
     * Genera un email para el empleado
     */
    private String generarEmail(Empleado empleado) {
        return generarUsername(empleado) + "@techsolutions.com";
    }
    
    /**
     * Autentica un usuario con username y password
     */
    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null && usuario.validarCredenciales(username, password)) {
            this.usuarioActual = usuario;
            return usuario;
        }
        return null;
    }
    
    /**
     * Obtiene el usuario actualmente autenticado
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
    }
    
    /**
     * Crea una nueva cuenta de usuario
     */
    public boolean crearUsuario(Usuario usuario) {
        if (usuario != null && !usuarios.containsKey(usuario.getUsername())) {
            usuarios.put(usuario.getUsername(), usuario);
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene todos los usuarios del sistema
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerUsuariosPorRol(Usuario.Rol rol) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getRol() == rol) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }
    
    /**
     * Obtiene usuarios por departamento
     */
    public List<Usuario> obtenerUsuariosPorDepartamento(String departamento) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            if (departamento.equals(usuario.getDepartamento())) {
                resultado.add(usuario);
            }
        }
        return resultado;
    }
    
    /**
     * Actualiza la información de un usuario
     */
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario != null && usuarios.containsKey(usuario.getUsername())) {
            usuarios.put(usuario.getUsername(), usuario);
            return true;
        }
        return false;
    }
    
    /**
     * Elimina un usuario del sistema
     */
    public boolean eliminarUsuario(String username) {
        return usuarios.remove(username) != null;
    }
    
    /**
     * Verifica si un username ya existe
     */
    public boolean existeUsername(String username) {
        return usuarios.containsKey(username);
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    public boolean cambiarPassword(String username, String passwordActual, String passwordNuevo) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null && usuario.getPassword().equals(passwordActual)) {
            usuario.setPassword(passwordNuevo);
            return true;
        }
        return false;
    }
    
    /**
     * Activa o desactiva un usuario
     */
    public boolean cambiarEstadoUsuario(String username, boolean activo) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null) {
            usuario.setActivo(activo);
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene estadísticas de usuarios
     */
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        int totalUsuarios = usuarios.size();
        int usuariosActivos = 0;
        int ceos = 0;
        int jefes = 0;
        int jefesDepartamento = 0;
        int empleados = 0;
        
        for (Usuario usuario : usuarios.values()) {
            if (usuario.isActivo()) {
                usuariosActivos++;
            }
            
            switch (usuario.getRol()) {
                case CEO:
                    ceos++;
                    break;
                case JEFE:
                    jefes++;
                    break;
                case JEFE_DEPARTAMENTO:
                    jefesDepartamento++;
                    break;
                case EMPLEADO:
                    empleados++;
                    break;
            }
        }
        
        estadisticas.put("total", totalUsuarios);
        estadisticas.put("activos", usuariosActivos);
        estadisticas.put("inactivos", totalUsuarios - usuariosActivos);
        estadisticas.put("ceos", ceos);
        estadisticas.put("jefes", jefes);
        estadisticas.put("jefes_departamento", jefesDepartamento);
        estadisticas.put("empleados", empleados);
        
        return estadisticas;
    }
    
    /**
     * Restablece la contraseña de un usuario
     */
    public boolean restablecerPassword(String username, String nuevaPassword) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null && nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            usuario.setPassword(nuevaPassword);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si el usuario actual tiene permisos para realizar una acción
     */
    public boolean tienePermiso(String accion) {
        if (usuarioActual == null) {
            return false;
        }
        
        // CEO tiene todos los permisos
        if (usuarioActual.esCEO()) {
            return true;
        }
        
        // Implementar lógica específica de permisos según sea necesario
        return usuarioActual.tienePermisosAdministrativos();
    }
    
    /**
     * Crea una cuenta para un empleado específico
     */
    public boolean crearCuentaEmpleado(Empleado empleado, String password) {
        if (empleado == null || password == null || password.trim().isEmpty()) {
            return false;
        }
        
        String username = generarUsername(empleado);
        if (usuarios.containsKey(username)) {
            return false; // Ya existe
        }
        
        Usuario nuevoUsuario = new Usuario(username, password, Usuario.Rol.EMPLEADO, empleado.getDepartamento());
        nuevoUsuario.setId("EMP_" + empleado.getId());
        nuevoUsuario.setNombre(empleado.getNombre());
        nuevoUsuario.setEmail(generarEmail(empleado));
        nuevoUsuario.setActivo(true);
        
        usuarios.put(username, nuevoUsuario);
        return true;
    }
    
    /**
     * Busca un usuario por username
     */
    public Usuario buscarPorUsername(String username) {
        return usuarios.get(username);
    }
    
    /**
     * Desactiva un usuario
     */
    public boolean desactivarUsuario(String username) {
        return cambiarEstadoUsuario(username, false);
    }
    
    /**
     * Reactiva un usuario
     */
    public boolean reactivarUsuario(String username) {
        return cambiarEstadoUsuario(username, true);
    }
    
    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameDisponible(String username) {
        return !existeUsername(username);
    }
}