import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticipantButton extends JButton {
   private Participant participant;
   private Rectangle bounds;

   public ParticipantButton(Participant participant, JPanel infoPanel, CalendarChart calendar){
       super(participant.getName().replaceAll(" ", "\n"));
       this.participant = participant;
       JLabel infoLabel = new JLabel(participant.getName());
       infoLabel.setBounds(10, 10, 80, 30);
       addActionListener(e -> {
           System.out.println(participant.getName());
           infoPanel.setBounds(bounds);
           infoPanel.setVisible(true);
           infoPanel.removeAll();
           infoPanel.add(infoLabel);
           calendar.activateParticipant(participant);
       });
   }

   @Override
    public void setBounds(int x, int y, int width, int height){
       super.setBounds(x, y, width, height);
       bounds = new Rectangle(x, y, width, height);
       bounds.x += 300;
   }

}
