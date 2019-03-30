import java.util.ArrayList;
import java.util.HashMap;

public class Participant {

    private HashMap<Integer, Boolean> constraintMap = new HashMap<>();
    private ArrayList<Integer> constraints;
    private String name;
    private int lastWeek = -100;
    private int numCalls = 0;
    public Participant(String name, ArrayList<Integer> constraints){
        this.constraints = constraints;
        for (int i = 0; i < Schedule.numWeeks; i++) {
            constraintMap.put(i, false);
        }
        for(int i = constraints.size() -1; i >= 0; i--){
            constraintMap.put(constraints.get(i), true);
        }

        this.name = name;
        Schedule.participantHashMap.put(name, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(int lastWeek) {
        this.lastWeek = lastWeek;
    }


    public boolean hasConflict(int i){
        return constraintMap.get(i);
    }

    public Participant clone(){
        return new Participant(name, constraints);
    }

    public int getNumCalls() {
        return numCalls;
    }

    public void setNumCalls(int numCalls) {
        this.numCalls = numCalls;
    }

    public void reset(){
        lastWeek = -100;
        numCalls = 0;
    }
}
