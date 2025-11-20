package games;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{
	private Image img;
	
	public ImagePanel(Image img) {
		this.img=img;
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, getWidth(), getHeight(), 0, 0, img.getWidth(null), img.getHeight(null), null);
	}
	
	public void setImage(Image newImage) {
		this.img = newImage;
		repaint();
	}
}
