package dk.dtu.debugger.ecno.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public abstract class EFigure extends Shape{
	
	private Color backgroundHighlightColor = new Color(null, 255,255,0);
	private Color backgroundColor = new Color(null, 220,220,220);
	
	public EFigure(){
		setBackgroundColor(backgroundColor);
	}
	public void highlight(){
		System.err.println("highlighting...");
		setBackgroundColor(backgroundHighlightColor);
		invalidate();
		repaint();
	}
	
	public void unHighlight(){
		setBackgroundColor(backgroundColor);
		invalidate();
		repaint();
	}
	@Override
	public Dimension getMaximumSize() {
		Dimension test = super.getMaximumSize();
		System.err.println("max size: " + test);
		return test;
	}
	
	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		Dimension test = super.getMinimumSize(wHint, hHint);
		System.err.println("min size: " + test);
		return test;
	}
	
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension test = super.getPreferredSize(wHint, hHint);
		System.err.println("preferred size: " + test);
		return test;
	}
	
	@Override
	public Border getBorder() {
		Border test = super.getBorder();
//		if(test != null)System.out.println("border: " + test);
		return test;
	}
	
	@Override
	public Rectangle getBounds() {
		Rectangle test = super.getBounds();
//		System.out.println("bounds: " + test);
		return test;
	}
	
	
}
