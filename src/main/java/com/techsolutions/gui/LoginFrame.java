package com.techsolutions.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.techsolutions.SistemaGestionTareas;
import com.techsolutions.model.Usuario;
import com.techsolutions.services.UsuarioService;

/**
 * Pantalla de inicio de sesión del sistema
 * Permite autenticación con diferentes roles de usuario
 */
public class LoginFrame extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancelar;
    private JLabel lblEstado;
    
    // Servicio de gestión de usuarios
    private UsuarioService usuarioService;
    
    public LoginFrame() {
        this.usuarioService = UsuarioService.getInstance();
        configurarVentana();
        crearComponentes();
        configurarEventos();
        centrarVentana();
    }
    
    /**
     * Configura la ventana principal del login
     */
    private void configurarVentana() {
        setTitle("TechSolutions - Inicio de Sesión");
        setSize(550, 450); // Aumentar tamaño
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Permitir redimensionar
        
        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error configurando Look and Feel: " + e.getMessage());
        }
    }
    
    /**
     * Crea y configura todos los componentes de la interfaz
     */
    private void crearComponentes() {
        // Panel principal con degradado
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 
                        0, getHeight(), new Color(100, 149, 237));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        
        // Panel del logo y título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        panelTitulo.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel lblLogo = new JLabel("🏢", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblLogo.setForeground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("TECHSOLUTIONS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Tareas", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(220, 220, 220));
        
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.add(lblLogo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);
        
        // Panel del formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(new EmptyBorder(20, 40, 20, 40)); // Más espacio
        panelFormulario.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Más espacio entre componentes
        
        // Campo usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Fuente más grande
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(lblUsuario, gbc);
        
        txtUsername = new JTextField(25); // Más ancho
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Fuente más grande
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 12, 10, 12))); // Más padding
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtUsername, gbc);
        
        // Campo contraseña
        JLabel lblPassword = new JLabel("🔒 Contraseña:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Fuente más grande
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(25); // Más ancho
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Fuente más grande
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 12, 10, 12))); // Más padding
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtPassword, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setOpaque(false);
        
        btnLogin = crearBoton("🚀 Iniciar Sesión", new Color(34, 139, 34));
        btnCancelar = crearBoton("Cancelar", new Color(220, 20, 60));
        
        panelBotones.add(btnLogin);
        panelBotones.add(Box.createHorizontalStrut(10));
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.insets = new Insets(15, 5, 5, 5);
        panelFormulario.add(panelBotones, gbc);
        
        // Label de estado
        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setForeground(Color.YELLOW);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = 5; gbc.insets = new Insets(5, 5, 5, 5);
        panelFormulario.add(lblEstado, gbc);
        
        // Ensamblar la ventana
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    /**
     * Crea un botón con estilo personalizado
     */
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
        
        return boton;
    }
    
    /**
     * Configura los eventos de la interfaz
     */
    private void configurarEventos() {
        // Evento del botón login
        btnLogin.addActionListener(e -> intentarLogin());
        
        // Evento del botón cancelar
        btnCancelar.addActionListener(e -> System.exit(0));
        
        // Enter en los campos de texto
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        };
        
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }
    
    /**
     * Intenta realizar el login con las credenciales ingresadas
     */
    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña");
            return;
        }
        
        Usuario usuario = usuarioService.autenticar(username, password);
        
        if (usuario != null) {
            mostrarEstado("Login exitoso. Cargando sistema...", Color.GREEN);
            
            // Pequeña pausa para mostrar el mensaje
            Timer timer = new Timer(1500, e -> {
                dispose(); // Cerrar ventana de login
                abrirSistema(usuario); // Abrir sistema con el usuario autenticado
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
    
    /**
     * Abre el sistema principal con el usuario autenticado
     */
    private void abrirSistema(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaGestionTareas sistema = new SistemaGestionTareas(usuario);
                sistema.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al cargar el sistema: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        mostrarEstado(mensaje, Color.YELLOW);
    }
    
    /**
     * Muestra un mensaje de estado
     */
    private void mostrarEstado(String mensaje, Color color) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(color);
    }
    
    /**
     * Centra la ventana en la pantalla
     */
    private void centrarVentana() {
        setLocationRelativeTo(null);
    }
    
    /**
     * Método principal para probar la ventana de login
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}