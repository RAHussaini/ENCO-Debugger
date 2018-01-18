package dk.dtu.debugger.ecno.figures;

import java.awt.GraphicsEnvironment;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class ElementFigure extends EFigure {

	/** The width and height radii applied to each corner. */
	protected Dimension corner = new Dimension(15, 15);
	private String text;
	private int expandWidth = 20;
	private int expandHeight = 10;
	
	/**
	 * Constructs a round cornered rectangle.
	 */
	public ElementFigure(String text) {
		// TODO Auto-generated constructor stub() { 
		super();
		
		this.text = text;
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Font font = new Font(null, new FontData(fonts[0], 15, java.awt.Font.BOLD));
				//			int width = FigureUtilities.getTextWidth(text, font);
				Dimension dim = FigureUtilities.getTextExtents(text, font);
		Dimension size = dim.expand(expandWidth, expandHeight);
		this.setSize(size);
		this.setBounds(new Rectangle(0, 0, size.width, size.height));
		
		font.dispose();

	}
	
	
	
	

	/**
	 * @see Shape#fillShape(Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		//	System.out.println("fill shape");

		//graphics.setBackgroundColor(getBackgroundColor()); // old
		graphics.setBackgroundColor(new Color(null, 125, 167, 116));
		//graphics.fillRoundRectangle(getBounds(), corner.width, corner.height);//old
		graphics.fillRectangle(10, 15, 90, 60); //old
		//graphics.fillRoundRectangle(400, 350, 60, 50);
		Point p = getBounds().getLocation();
		int height = getBounds().height-expandHeight;
		graphics.setForegroundColor(getForegroundColor());
		graphics.drawString(text, p.x+ expandWidth/2, p.y+height/8);
		
//		width/2-this.width/2, height/8);
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();

		f.x = r.x + getLineWidth() / 2;
		f.y = r.y + getLineWidth() / 2;
		f.width = r.width - getLineWidth();
		f.height = r.height - getLineWidth();
		//	getLineWidth()
		//	System.out.println(f.x + "," + f.y + ";" + f.width + "," + f.height + ";" + corner.width + "," + corner.height);
		graphics.drawRoundRectangle(f, corner.width, corner.height); //old
		//graphics.drawRoundRectangle(r, arcWidth, arcHeight);
		//drawRect(200, 210, 20, 30);
		//**********************
		//graphics.drawRoundRectangle(f, 200, 30);
		//drawRoundRectangle(10, 10, 200, 200, 30, 30);
	}

	/**
	 * Sets the dimensions of each corner. This will form the radii of the arcs which form the
	 * corners.
	 *
	 * @param d the dimensions of the corne
	 * @since 2.0
	 */
	public void setCornerDimensions(Dimension d) {
		corner.width = d.width;
		corner.height = d.height;
		
	}
	
	
}
