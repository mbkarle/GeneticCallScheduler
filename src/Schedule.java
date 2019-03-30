import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Schedule {

    public static int numWeeks, avgNumCalls;
    public static HashMap<String, Participant> participantHashMap = new HashMap<>();
    private HashMap<String, Participant> participants;
    private int score;
    private ArrayList<String> schedule;
    public static HashMap<Integer, Participant> requiredDocs = new HashMap<>();

    public Schedule(ArrayList<String> schedule){
        this.schedule = schedule;
        this.score = 0;
        participants = new HashMap<>();
        for(String s : participantHashMap.keySet()){
            participants.put(s, participantHashMap.get(s).clone());
        }
    }

    public static Schedule randomSchedule(){
        ArrayList<String> temp = new ArrayList<>();
        for(String s : participantHashMap.keySet()){
            for (int i = 0; i < avgNumCalls; i++) {
                temp.add(s);
            }
        }
        while(temp.size() < numWeeks)
            temp.add((String) participantHashMap.keySet().toArray()[(int) (Math.random() * participantHashMap.keySet().size())]);

        Collections.shuffle(temp);
        for(Integer i : requiredDocs.keySet()){
            temp.set(i, requiredDocs.get(i).getName());
        }
        return new Schedule(temp);
    }

    public int calcScore(){
//        if(this.score != 0)
//            return this.score;
        for(String s : participants.keySet())
            participants.get(s).reset();
        int score = 0;
        for (int i = 0; i < schedule.size(); i++) {
            Participant scheduled = participants.get(schedule.get(i));
            if(scheduled.hasConflict(i))
                score += 10000;
            int weeksPassed = i - scheduled.getLastWeek();
            if(weeksPassed < 4)
                score += 50 * (10 - (weeksPassed*3));
            scheduled.setLastWeek(i);
            scheduled.setNumCalls(scheduled.getNumCalls()+1);

        }
        for(String s : participants.keySet()){
            Participant scheduled = participants.get(s);
            int extraCalls = scheduled.getNumCalls() - avgNumCalls;
            if(extraCalls > 0) {
                score += (extraCalls < 5) ? Math.pow(10, extraCalls*2) : Math.pow(10, 10);
            }
            else if(extraCalls < 0)
                score += 1000;
        }
        this.score = score;
        return score;
    }

    public void mutate(double chance){
        ArrayList<Integer> conflicts = new ArrayList<>();
        boolean conflictChanged = true;
        ArrayList<Integer> mutableSchedule = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            if(requiredDocs.get(i) == null)
                mutableSchedule.add(i);
        }
        do{
            if(conflictChanged){
                for (int i = 0; i < schedule.size(); i++) {
                    if(participants.get(schedule.get(i)).hasConflict(i))
                        conflicts.add(i);
                }
            }
            int idx, i = mutableSchedule.get((int)(Math.random() * mutableSchedule.size()));
            if(Math.random() < .5 || conflicts.size() == 0) {
                conflictChanged = false;
                do {
                    idx = mutableSchedule.get((int)(Math.random() * mutableSchedule.size()));
                } while (idx == i);
            }
            else{
                conflictChanged = true;
                idx = conflicts.remove((int)(Math.random()*conflicts.size()));
                while(idx == i){
                    i = mutableSchedule.get((int)(Math.random() * mutableSchedule.size()));
                }
            }
            String temp = schedule.get(i);
            schedule.set(i, schedule.get(idx));
            schedule.set(idx, temp);
        }while(Math.random() < chance);
    }

    public Schedule clone(){
        return new Schedule((ArrayList<String>) schedule.clone());
    }

    public ArrayList<String> getSchedule() {
        return schedule;
    }

    public HashMap<String, Participant> getParticipants() {
        return participants;
    }
}
