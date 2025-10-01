package com.techsolutions.model;

/**
 * Clase que representa un usuario del sistema con diferentes roles y permisos
 * Roles disponibles: JEFE, JEFE_DEPARTAMENTO, EMPLEADO
 * 
 * @author TechSolutions Team
 * @version 1.0
 */
public class Usuario {
    
    /**
     * Enumeración de roles disponibles en el sistema
     */
    public enum Rol {
        CEO("CEO/Director General"),
        JEFE("Jefe General"),
        JEFE_DEPARTAMENTO("Jefe de Departamento"), 
        EMPLEADO("Empleado");
        
        private final String descripcion;
        
        Rol(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Atributos del usuario
    private String id;
    private String username;
    private String password;
    private String nombre;
    private String email;
    private Rol rol;
    private String departamento;
    private boolean activo;
    
    /**
     * Constructor completo para Usuario
     */
    public Usuario(String id, String username, String password, String nombre, 
                   String email, Rol rol, String departamento) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.departamento = departamento;
        this.activo = true;
    }
    
    /**
     * Constructor básico para Usuario
     */
    public Usuario(String username, String password, Rol rol, String departamento) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.departamento = departamento;
        this.activo = true;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    /**
     * Verifica si el usuario es CEO/Director General
     */
    public boolean esCEO() {
        return rol == Rol.CEO;
    }
    
    /**
     * Verifica si el usuario tiene permisos de jefe general
     */
    public boolean esJefe() {
        return rol == Rol.JEFE;
    }
    
    /**
     * Verifica si el usuario es jefe de departamento
     */
    public boolean esJefeDepartamento() {
        return rol == Rol.JEFE_DEPARTAMENTO;
    }
    
    /**
     * Verifica si el usuario es empleado regular
     */
    public boolean esEmpleado() {
        return rol == Rol.EMPLEADO;
    }
    
    /**
     * Verifica si el usuario tiene permisos administrativos completos (CEO)
     */
    public boolean tienePermisosCompletos() {
        return rol == Rol.CEO;
    }
    
    /**
     * Verifica si el usuario tiene permisos administrativos (CEO, jefe o jefe de departamento)
     */
    public boolean tienePermisosAdministrativos() {
        return rol == Rol.CEO || rol == Rol.JEFE || rol == Rol.JEFE_DEPARTAMENTO;
    }
    
    /**
     * Valida las credenciales del usuario
     */
    public boolean validarCredenciales(String username, String password) {
        return this.username.equals(username) && this.password.equals(password) && this.activo;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{id='%s', username='%s', nombre='%s', rol=%s, departamento='%s', activo=%s}",
                id, username, nombre, rol.getDescripcion(), departamento, activo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id != null ? id.equals(usuario.id) : usuario.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}