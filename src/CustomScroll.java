import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class CustomScroll extends JComponent implements MouseListener, MouseWheelListener {

    private Color defaultColor, hoverColor;
    private int mouseY, originalY, lowerBound;
    private Timer t;
    private JPanel jPanel;

    public CustomScroll(JPanel jPanel, int x, int y, int width, int height, int lowerBound){

        /*--------------------Instance Fields--------------------*/
        this.jPanel = jPanel;
        this.lowerBound = lowerBound;

        /*--------------------Set Aesthetics--------------------*/
        setOpaque(true);
        defaultColor = new Color(130, 130, 130);
        hoverColor = new Color(200, 200, 200);
        setBackground(defaultColor);
        setBorder(BorderFactory.createLineBorder(Color.black, 1));

        /*--------------------Set position--------------------*/
        setBounds(x, y, width, height);
        originalY = jPanel.getY();

        /*--------------------Set up timer--------------------*/
        t = new Timer(10, e -> {
            Point currMouse = getMousePosition();
            if(currMouse != null) {
                int delta = currMouse.y - mouseY;
                scroll(delta, lowerBound);
            }
        });

        addMouseListener(this);

        jPanel.addMouseWheelListener(this);

    }

    public void scroll(int delta, int bound){
        int newY = getY() + delta;
        if(newY >= 0 && newY <= bound) {
            setLocation(getX(), getY() + delta);
            jPanel.setLocation(jPanel.getX(), jPanel.getY() - delta * 11 / 3);
            repaint();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Current Y: " + getBounds().y);
        mouseY = getMousePosition().y;
        t.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        t.stop();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBackground(hoverColor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        t.stop();
        setBackground(defaultColor);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scroll(e.getWheelRotation(), lowerBound);
    }
}
