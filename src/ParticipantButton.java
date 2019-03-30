import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticipantButton extends JButton {
   private Participant participant;

   public ParticipantButton(Participant participant){
       super(participant.getName());
       this.participant = participant;
       addActionListener(e -> {
           System.out.println("clicked!");
       });
   }


}
