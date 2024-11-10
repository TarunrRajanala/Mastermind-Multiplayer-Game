import java.util.Scanner;

public class Game {

    String play = "Y";
    private String secretCode;
    public int guessNum;
    Scanner scanner;
    GuessHistory guessHistory;

    String[] colorsArray;
    int validity;
    boolean test;


    public Game(Scanner scanner, boolean test, GuessHistory guessHistory, int guessNum, String[] colorsArray, String clientCode) {
        this.secretCode = clientCode;
        this.test = test;
        this.guessNum = guessNum;
        this.scanner = scanner;
        this.guessHistory = guessHistory;
        this.colorsArray = colorsArray;

    }


    public String runGame() {
        boolean win = false;
        if (test) {
            System.out.println("Secret Code: " + secretCode);
        }
        for (int n = guessNum; n > 0; n--) {
            System.out.println("You have " + n + " guesses left.");
            System.out.println("What is your next guess?");
            System.out.println("Type in the characters for your guess and press enter.");
            System.out.println("Enter Guess: ");
            String guess = scanner.nextLine();

            boolean valid = ValidGuess(guess);
            if (!valid) { //valid guess = 0
                System.out.println(guess+" -> INVALID GUESS\n");
                n++;
            }
            //history guess
            else if (guess.equals("HISTORY")) {
                //print guess history
                guessHistory.printGuess();
                n++;
            }
            //valid guess
            else {
                //check Guess
                Pegs pegs = checkGuess(guess);
                String feedback = pegs.blackPegs + "B_" + pegs.whitePegs +"W";
                if(pegs.blackPegs == GameConfiguration.pegNumber){
                    win = true;
                    System.out.println(guess + " -> Result: " + feedback +" - You Win !!");
                    break;
                }
                guessHistory.addGuess(guess, feedback);
                System.out.println(guess + " -> Result: " + feedback);
                //output guess
                //if black pegs = gameconfig total pegs, they won
            }
        }
        if(win){
            System.out.println("Are you ready for another game (Y/N): ");
            play = scanner.nextLine();
        }
        else{
            System.out.print("Sorry, you are out of guesses. You lose, boo-hoo.\n");
            System.out.println("Are you ready for another game (Y/N): ");
            play = scanner.nextLine();
        }
        return play;
    }


    // invalid guess, incorrect length, lowercase, or invalid letter
    public boolean ValidGuess(String vguess){
        String[] colors = GameConfiguration.colors;
        if(vguess.equals("HISTORY")){
            return true;
        }
        if(vguess.length()!= GameConfiguration.pegNumber){
            return false;
        }
        int counter =0;
        int colorLength = colorsArray.length;
        for(int i =0; i < vguess.length(); i++){
            char letter = vguess.charAt(i);
            String letterString = Character.toString(letter);
            if(Character.isLowerCase(letter)){     //check for any lowercase letters
                return false;
            }
            for(int j =0; j<colorLength;j++) {
                if ((letterString.equals(colors[j]))) {//test against colors
                    counter++;
                }
            }
        }
        if(!(counter==vguess.length())){
            return false;
        }
        //if the guess satisfies requirements
        return true;

    }



    public Pegs checkGuess(String guess){
        //figure out how to check guess against secret code

        String answer = this.secretCode;
        int blackCounter = 0;
        int whiteCounter =0;

        //create char array for guess and answer
        char[] guessArray = new char[guess.length()];
        char[] ansArray = new char[answer.length()];

        //populate array with the guess
        for(int i =0; i<guess.length();i++){
            guessArray[i] = guess.charAt(i);
        }
        //popoulate array with answer
        for(int i =0; i<answer.length(); i++){
            ansArray[i] = answer.charAt(i);
        }

        //loop to find Black Pegs
        for(int i =0; i<guess.length();i++){
            if(guessArray[i]==ansArray[i]){
                blackCounter++;
                guessArray[i] = ' ';
                ansArray[i] = '_';
                //need to remove the correct guess so we can loop again for white pegs
            }
            //Guess: YBBP
            //Anser: BBYP
            //Result : Y B

        }

        for(int i = 0; i<guess.length();i++){
            for(int j =0; j<answer.length();j++){
                if(guessArray[i]==ansArray[j]){
                    whiteCounter++;
                    ansArray[j]='_';        //need to remove ans so we dont double for the same Char
                    break;
                }
            }
        }

        //assign white pegs
        //assign black pegs
        //return total pegs;
        return new Pegs(blackCounter, whiteCounter);
    }

    public String processGuess(String guess){
        //figure out how to check guess against secret code

        String answer = this.secretCode;
        int blackCounter = 0;
        int whiteCounter =0;

        //create char array for guess and answer
        char[] guessArray = new char[guess.length()];
        char[] ansArray = new char[answer.length()];

        //populate array with the guess
        for(int i =0; i<guess.length();i++){
            guessArray[i] = guess.charAt(i);
        }
        //popoulate array with answer
        for(int i =0; i<answer.length(); i++){
            ansArray[i] = answer.charAt(i);
        }

        //loop to find Black Pegs
        for(int i =0; i<guess.length();i++){
            if(guessArray[i]==ansArray[i]){
                blackCounter++;
                guessArray[i] = ' ';
                ansArray[i] = '_';
                //need to remove the correct guess so we can loop again for white pegs
            }
            //Guess: YBBP
            //Anser: BBYP
            //Result : Y B

        }

        for(int i = 0; i<guess.length();i++){
            for(int j =0; j<answer.length();j++){
                if(guessArray[i]==ansArray[j]){
                    whiteCounter++;
                    ansArray[j]='_';        //need to remove ans so we dont double for the same Char
                    break;
                }
            }
        }

        //assign white pegs
        //assign black pegs
        //return total pegs;
        String feedback = blackCounter + "B_" + whiteCounter +"W";
        if(blackCounter == GameConfiguration.pegNumber){
            feedback = blackCounter + "B_" + whiteCounter +"W" + " Congrats you won the Game!";
        }
        return feedback;
    }

    public boolean Winner(String guess){
        //figure out how to check guess against secret code

        String answer = this.secretCode;
        int blackCounter = 0;
        int whiteCounter =0;

        //create char array for guess and answer
        char[] guessArray = new char[guess.length()];
        char[] ansArray = new char[answer.length()];

        //populate array with the guess
        for(int i =0; i<guess.length();i++){
            guessArray[i] = guess.charAt(i);
        }
        //popoulate array with answer
        for(int i =0; i<answer.length(); i++){
            ansArray[i] = answer.charAt(i);
        }

        //loop to find Black Pegs
        for(int i =0; i<guess.length();i++){
            if(guessArray[i]==ansArray[i]){
                blackCounter++;
                guessArray[i] = ' ';
                ansArray[i] = '_';
                //need to remove the correct guess so we can loop again for white pegs
            }
            //Guess: YBBP
            //Anser: BBYP
            //Result : Y B

        }

        for(int i = 0; i<guess.length();i++){
            for(int j =0; j<answer.length();j++){
                if(guessArray[i]==ansArray[j]){
                    whiteCounter++;
                    ansArray[j]='_';        //need to remove ans so we dont double for the same Char
                    break;
                }
            }
        }

        //assign white pegs
        //assign black pegs
        //return total pegs;
        if(blackCounter == GameConfiguration.pegNumber) {
            return true;
        }
        else{
            return false;
        }
    }

    public void addGuessHistory(String guess, String feedback){
        guessHistory.addGuess(guess, feedback);
    }
    public String printHistory(){
        return guessHistory.printGuess();
    }
}




