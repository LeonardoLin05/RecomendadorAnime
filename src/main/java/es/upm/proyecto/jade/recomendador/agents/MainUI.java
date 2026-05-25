package es.upm.proyecto.jade.recomendador.agents;

import javax.swing.*;
import java.util.*;
import java.util.List;

import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.Images;
import jade.core.Agent;

import java.awt.*;

public class MainUI extends JFrame 
{

    private static final long serialVersionUID = 1L;
    private UIAgent agent; 

    public MainUI(UIAgent agent) 
    {
    	this.agent = agent; 
        setTitle("Recomendador de Anime");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.BLACK);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(crearPantallaInicio(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel crearPantallaInicio() 
    {
    	// Fondo
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Espacio superior para centrar verticalmente
        panel.add(Box.createVerticalGlue());

        // Título
        JLabel titulo = new JLabel("Recomendador de Anime");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        titulo.setForeground(Color.MAGENTA);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(12));

        // Subtítulo
        JLabel subtitulo = new JLabel("¿Qué tipo de anime te gusta?");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(Color.YELLOW);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitulo);

        panel.add(Box.createVerticalStrut(32));

        // Barra de búsqueda
        JPanel barraPanel = new JPanel();
        barraPanel.setBackground(Color.BLACK);
        barraPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        barraPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JTextField campo = new JTextField(40);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        campo.setForeground(Color.YELLOW);
        campo.setBackground(new Color(35, 35, 35));
        campo.setCaretColor(Color.MAGENTA);
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1),BorderFactory.createEmptyBorder(12, 16, 12, 16)));


        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnBuscar.setForeground(Color.YELLOW);
        btnBuscar.setBackground(Color.MAGENTA);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBuscar.addActionListener(e -> {
            String texto = campo.getText().trim();
            if (!texto.isEmpty() && !campo.getForeground().equals(Color.GRAY)) 
            {
            	 agent.lanzarBusqueda(texto);
            	 pantallaLoading();
            }
        });
        campo.addActionListener(e -> btnBuscar.doClick());

        barraPanel.add(campo);
        barraPanel.add(btnBuscar);
        panel.add(barraPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JLabel textoEjemplo = new JLabel("Ej: me gustan las series de miedo acerca de vampiros y que sean de alrededor de los 2000");
        textoEjemplo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        textoEjemplo.setForeground(Color.GRAY);
        textoEjemplo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textoEjemplo);

        // Espacio inferior
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    
    
    private JPanel crearPantallaResultados(List<Anime> animes) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // Barra superior
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        topBar.setBackground(Color.BLACK);

        JLabel logo = new JLabel("AniRec");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(Color.MAGENTA);
        topBar.add(logo);
        
        JButton btnVolver = new JButton("Nueva búsqueda");
        btnVolver.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnVolver.setForeground(Color.YELLOW);
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane().add(crearPantallaInicio(), BorderLayout.CENTER);
            revalidate();
            repaint();
        });
        topBar.add(btnVolver);

        panel.add(topBar, BorderLayout.NORTH);

        // Grid de tarjetas
        JPanel grid = new JPanel(new GridLayout(0, 5, 16, 16));
        grid.setBackground(Color.BLACK);
        grid.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
                
        for (Anime anime : animes) 
        {
            grid.add(crearTarjeta(anime));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBackground(Color.BLACK);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjeta(Anime anime) 
    {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.BLACK);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Portada
        JLabel portada = new JLabel();
        portada.setPreferredSize(new Dimension(220, 320));
        portada.setMinimumSize(new Dimension(220, 320));
        portada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        portada.setOpaque(true);
        portada.setBackground(Color.BLACK);
        portada.setHorizontalAlignment(SwingConstants.CENTER);

        // Carga la imagen en segundo plano
        if (anime.getImages() != null && anime.getImages().getJpg() != null) 
        {
        	String urlImagen = anime.getImages().getJpg().getLargeImageUrl();
        	portada.addComponentListener(new java.awt.event.ComponentAdapter()
        	{
                public void componentResized(java.awt.event.ComponentEvent e) 
                {
                    portada.removeComponentListener(this); 
                    cargarImagenAsync(portada, urlImagen);
                }
            });
        }
        card.add(portada);

        // Info debajo de la portada
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.BLACK);
        info.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        // Título
        JLabel tituloLabel = new JLabel("<html><p style='width:200px'><b>" + anime.getTitle() + "</b></html>");
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        tituloLabel.setForeground(Color.MAGENTA);
        info.add(tituloLabel);   

        info.add(Box.createVerticalStrut(4));
        
        // Año y Episodios  
        int yearInt = anime.getYear(); 
        String year = String.valueOf(yearInt);
        if(yearInt == 0) { year = anime.getAired().getProp().getFrom().getYear()+""; }
        String eps  = anime.getEpisodes() + " eps";

        JLabel metaLabel = new JLabel( "<html>" + year + " &nbsp;-&nbsp; " + eps + "</html>");
        metaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        metaLabel.setForeground(Color.YELLOW);
        info.add(metaLabel);

        info.add(Box.createVerticalStrut(6));

        // Resumen IA
        if (anime.getAiSummary() != null && !anime.getAiSummary().isEmpty()) 
        {
            JLabel resumen = new JLabel("<html><p style='width:160px; color:#aaaaaa'>" + anime.getAiSummary() + "</p></html>");
            resumen.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info.add(resumen);
        }

        card.add(info);
        return card;
    }
    
    private void cargarImagenAsync(JLabel label, String imageUrl) 
    {
        new SwingWorker<ImageIcon, Void>() 
        {
            protected ImageIcon doInBackground() throws Exception 
            {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(new java.net.URL(imageUrl));;

                int ancho = label.getWidth();
                int alto  = label.getHeight();
                java.awt.Image scaled = img.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            protected void done() {
                try 
                {
                    label.setIcon(get());
                    label.revalidate();
                    label.repaint();
                } 
                catch (Exception e) 
                {
                    label.setText("Sin imagen");
                    label.setForeground(Color.BLACK);
                }
            }
        }.execute();
    }
    
    public void mostrarResultados(List<Anime> animes) 
    {
        getContentPane().removeAll();
        getContentPane().add(crearPantallaResultados(animes), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void pantallaLoading() 
    {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(Box.createVerticalGlue());
        
        JLabel loading = new JLabel("Loading...");
        loading.setFont(new Font("SansSerif", Font.BOLD, 25));
        loading.setForeground(Color.MAGENTA);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loading);
        
        panel.add(Box.createVerticalGlue());

        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> {
            MainUI gui = new MainUI(null);

            List<Anime> prueba = new ArrayList<>();
            for (int i = 1; i <= 15; i++) 
            {
                Anime a = new Anime();
                a.setTitle("Anime de prueba " + i);
                a.setScore(7.0 + i * 0.1);
                a.setAiScore(6.5 + i * 0.2);
                a.setAiSummary("Resumen generado por IA para este anime de prueba número" + i);
                a.setYear(2000 + i);
                a.setEpisodes(20+i);
                prueba.add(a); 
                
                Images images = new Images();
                es.upm.proyecto.jade.recomendador.models.Image img = new es.upm.proyecto.jade.recomendador.models.Image();
                img.setLargeImageUrl("https://cdn.myanimelist.net/images/anime/7/21569l.jpg");
                images.setJpg(img);
                a.setImages(images);
            }

            gui.getContentPane().removeAll();
            gui.getContentPane().add(gui.crearPantallaResultados(prueba), BorderLayout.CENTER);
            gui.revalidate();
            gui.repaint();
        });
    }
}