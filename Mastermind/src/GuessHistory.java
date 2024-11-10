import java.util.ArrayList;

public class GuessHistory {
    ArrayList<String> guessHistory;

    public GuessHistory(){
        guessHistory = new ArrayList<String>();

    }

    public void addGuess(String guess, String feedback){
        String addGuess = guess + " " + feedback +"       ";
        guessHistory.add(addGuess);

    }

    public String printGuess(){
        StringBuilder SB = new StringBuilder(" ");
        for( int i =0; i< guessHistory.size(); i++){
            SB.append( guessHistory.get(i));
        }
        return SB.toString();
    }
}
