package gui;

import comonent.Mnist;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PaintPanel extends JPanel{
  private static final Stroke STROKE = new BasicStroke(
    3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private transient List<Shape> list;
  private transient Path2D path;
  private transient MouseAdapter handler;
  @Override public void updateUI() {
    removeMouseMotionListener(handler);
    removeMouseListener(handler);
    super.updateUI();
    handler = new MouseAdapter() {
      @Override public void mousePressed(MouseEvent e) {
        path = new Path2D.Double();
        list.add(path);
        Point p = e.getPoint();
        path.moveTo(p.x, p.y);
        repaint();
      }
      @Override public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        path.lineTo(p.x, p.y);
        repaint();
      }
    };
    addMouseMotionListener(handler);
    addMouseListener(handler);
    list = new ArrayList<Shape>();
  }
  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (list != null) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setPaint(Color.BLACK);
      g2.setStroke(STROKE);
      for (Shape s : list) {
        g2.draw(s);
      }
      g2.dispose();
    }
  }
}