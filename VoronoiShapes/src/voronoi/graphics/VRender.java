package voronoi.graphics;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import voronoi.driver.SimpleImage;
import voronoi.driver.VoronoiMapper;

public class VRender{
	
	VoronoiMapper mapper;
	SimpleImage im;
	
	public VRender(VoronoiMapper mapper) {
		this.mapper = mapper;
		this.im = mapper.image;
		EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
            try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
            JFrame frame = new JFrame("Testing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new VPanel(im.toImage()));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    });
    }

	public class VPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private BufferedImage img;
	    private JLabel label;
		
		public VPanel(BufferedImage image) {
			
			setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridwidth = GridBagConstraints.REMAINDER;
	        label = new JLabel();
	        
            img = image;
            label.setIcon(new ImageIcon(img));
            
	        add(label, gbc);	
	        label.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	new Thread(new Runnable() {
						
						@Override
						public void run() {
							mapper.handleClick(e.getX(), e.getY(), label);							
						}
					}).start();
	            }
	        });
		}
	}
}

