import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WeekBox {

    private Rectangle bounds;
    private int week;
    private boolean conflicted, requested, assigned, error;
    private Color conflictedColor, requestedColor, assignedColor;
    private String weekString, assignedName;
    private final static BufferedImage exclamation;
    static{
        exclamation = GraphicsPanel.readImg("exclamation");
    }

    public WeekBox(int week, Rectangle bounds){
        this.week = week;
        this.bounds = bounds;
        conflictedColor = new Color(240, 0, 0, 127);
        requestedColor = new Color(0, 0, 240, 127);
        assignedColor = new Color(0, 255, 0, 127);

        weekString = GraphicsPanel.interpretWeek(week);
    }

    private Color getColor(){
        ArrayList<Color> colors = new ArrayList<>();
        if(conflicted)
            colors.add(conflictedColor);
        if(requested)
            colors.add(requestedColor);
        if(assigned)
            colors.add(assignedColor);

        if(colors.size() == 0)
            return null;

        if(conflicted && (assigned || requested))
            error = true;
        else
            error = false;

        int[] rgb = new int[]{0, 0, 0};
        /*--------------------Blend Colors--------------------*/
        for(Color c : colors){
            rgb[0] += c.getRed();
            rgb[1] += c.getGreen();
            rgb[2] += c.getBlue();
        }

        for (int i = 0; i < 3; i++) {
            rgb[i] /= colors.size();
        }

        return new Color(rgb[0], rgb[1], rgb[2], 127);
    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.black);
        g2.setFont(new Font("Roboto", Font.PLAIN, 14));
        g2.drawString(weekString, 20, bounds.y + 20);
        if(assignedName != null){
            g2.drawString(assignedName, 20, bounds.y + 50);
        }
        Color c = getColor();
        if(c != null) {
            g2.setColor(c);
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            if (error) {
                g2.drawImage(exclamation, bounds.width / 2 - 16, bounds.y + bounds.height / 2 - 16, 32, 32, null);
            }
        }

    }

    public void setConflicted(boolean conflicted) {
        this.conflicted = conflicted;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public void setAssignedName(String name){
        assignedName = name;
    }

    public void clear(){
        conflicted = false;
        requested = false;
        assigned = false;
    }
}
