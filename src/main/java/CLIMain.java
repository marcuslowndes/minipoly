import java.util.Scanner;
import model.Model;


/**
 * Main Class for the Command Line Interface version of the Minipoly game.
 * This class uses the same Model class as the GUI version of the game,
 * but does not follow the model-view/observable-observer paradigm.
 * <p>
 * This version of the game  uses only print statements to communicate
 * to the player and takes user inputs as text strings from the command line.
 * <p>
 * Written according to specification provided by Dr. Ian Bailey
 * for Oxford Brookes Computer Science BSc
 * <p> Module COMP6018: Advanced Object Oriented Programming
 *
 * @author Marcus Lowndes
 */
public class CLIMain{

    private static Model model;
    private static Scanner scan;
    private static boolean cheatMode;
    private static final String
            LINE = "-------------------------------------------------------\n",
            INVALID_INPUT = "\nPlease enter a valid input.\n";


    /** @param args the command line arguments */
    public static void main(String [] args){
        scan = new Scanner(System.in);
        model = new Model();
        cheatMode = cheatModeDialog();

        System.out.println(titleToString() + "\nProgrammed by Marcus Lowndes,"
                + " designed by Dr Ian Bayley\n" + LINE);

        boolean quitGame = false;
        // main game loop
        while (!quitGame) {
            // display game board and dynamically display controls
            System.out.print(model.toString() + "\n" + LINE
                    + playersMoneyToString() + "\nIt is now "
                    + model.getCurrentPlayer().toString() + "'s turn.\n"
                    + controlsToString() + "\n >> ");

            // take user input
            switch (scan.nextLine()) {
                // 0 allows user to quit the game
                case "0":
                    quitGame = confirmQuitGame();
                    break;

                // 1 initiates the next turn
                case "1":
                    System.out.println(LINE + "\n" + model.nextTurn() + "\n");
                    break;

                // 2 buys or improves a property if possible, otherwise is disabled
                case "2":
                    System.out.println(LINE + interactCurrentPosn());
                    break;

                // 3 initiates the next turn with a user-inputted roll (cheat mode)
                case "3":
                    cheatRoll();
                    break;

                // any other input is invalid
                default:
                    System.out.println(LINE + INVALID_INPUT);
            }

            // end the game if the model says so
            if (model.isGameOver()) {
                System.out.println(model.toString() + "\n" + gameOverToString());
                quitGame = true;
            }
        }
        System.out.println(LINE + "\nGAME OVER\n");
    }


    /**
     * Allow user to enable cheat mode. Asks if the user would like to enable
     * cheat mode and takes the user's next input.
     *
     * @return  true if the user inputs 'yes' to enable cheats, returns false
     *          if the user inputs 'no', otherwise asks the user to try again.
     */
    private static boolean cheatModeDialog(){
        boolean cheatModeConf = false;
        while(!cheatModeConf) {
            System.out.print("Enable Cheats?\n\nThis mode allows you to choose"
                    + " what the next player\nrolls at the start of their turn."
                    + "\nThis is designed for testing purposes only. Y/N: ");

            switch(scan.nextLine()){
                case "Y": case "y": case "yes": case "Yes": case "YES":
                    System.out.println("\n" + LINE);
                    return true;

                case "N": case "n": case "no": case "No": case "NO":
                    System.out.println("\n" + LINE);
                    cheatModeConf = true;
                    break;

                default:
                    System.out.println(INVALID_INPUT + "\n" + LINE);
            }
        }
        return false;
    }


    /**
     * A String representation of the controls to the Minipoly game that are
     * available to the user. Controls to buy/improve/cheat the game are
     * displayed dynamically depending on the model's state.
     *
     * @return  the available user controls
     */
    private static String controlsToString(){
        String  controls1 = "\nControls:\n    0: Exit game.\n    1: Next turn.\n",
                two = "    2: ",    buy = "Buy",    improve = "Improve",
                controls2 = " the current\n       player's position.\n",
                controls3 = "    3: Choose dice roll\n       for next turn.\n";

        String controls = controls1;
        if (model.isCurrentPosnBuyable())
            controls += two + buy + controls2;
        if (model.isCurrentPosnImprovable())
            controls += two + improve + controls2;
        if (cheatMode)
            controls += controls3;

        return controls;
    }


    /**
     * Allow user to quit the game. Asks if the user would like to quit
     * the game and takes the user's next input.
     *
     * @return  true if the user inputs 'yes' to quit the game, returns false
     *          if the user inputs 'no', otherwise asks the user to try again.
     */
    private static boolean confirmQuitGame(){
        boolean quitGameConf = false;
        while(!quitGameConf) {
            System.out.print("\nAre you sure you want to quit? Y/N: ");

            switch(scan.nextLine()){
                case "Y": case "y": case "yes": case "Yes": case "YES":
                    return true;

                case "N": case "n": case "no": case "No": case "NO":
                    quitGameConf = true;
                    System.out.println("\n" + LINE + "\n");
                    break;

                default:
                    System.out.println(LINE + INVALID_INPUT);
            }
        }
        return false;
    }


    /**
     * Allows the user to buy or improve a position that their counter is on.
     * If the property is buyable or can be improved, and the position has no
     * hotel built on it, it will be interacted with, otherwise the user's
     * input will be treated as invalid. This option is not presented to the
     * user if the property cannot be interacted with.
     *
     * @return  a String representation of the property being interacted with,
     *          if it can be interacted with. Else, "invalid input" will be
     *          returned
     */
    private static String interactCurrentPosn(){
        return (model.isCurrentPosnImprovable() || model.isCurrentPosnBuyable()) ?
                ("\n" + model.interactCurrentPrpt() + "\n") : INVALID_INPUT;
    }


    /**
     * Initiates the next turn of the model with a user-inputted roll of the dice.
     * If cheat mode is enabled, allows the user to input a number to specify the
     * next roll of the dice, and initiates the next turn with that roll.
     */
    private static void cheatRoll(){
        boolean validDiceRoll = false;

        while(!validDiceRoll && cheatMode) {
            System.out.print("\nEnter Dice Roll (1-12), or 0 to cancel: ");

            String rollInput = scan.nextLine();
            switch(rollInput){
                case "1": case "2": case "3": case "4": case "5":
                case "6": case "7": case "8": case "9": case "10":
                case "11": case "12":
                    System.out.println(LINE + "\n"
                            + model.nextTurn(Integer.parseInt(rollInput))
                            + "\n");
                    validDiceRoll = true;
                    break;

                case "0":
                    System.out.println(LINE + "\nCheat dice roll cancelled\n");
                    validDiceRoll = true;
                    break;

                default:
                    System.out.println(LINE + INVALID_INPUT);
            }
        }
    }


    /** @return the game's title art */
    private static String titleToString(){
        return "  __  __ _____ _   _ _____ _____   ____  _  __     __\n"
                + " |  \\/  |_   _| \\ | |_   _|  __ \\ / __ \\| | \\ \\   / /\n"
                + " | \\  / | | | |  \\| | | | | |__) | |  | | |  \\ \\_/ / \n"
                + " | |\\/| | | | | . ` | | | |  ___/| |  | | |   \\   /  \n"
                + " | |  | |_| |_| |\\  |_| |_| |    | |__| | |____| |   \n"
                + " |_|  |_|_____|_| \\_|_____|_|     \\____/|______|_|   \n\n";
    }


    /**
     * @return  a string representation of both player's money and the
     *          winner of the game
     */
    private static String gameOverToString(){
        String winner = (model.getPlayerOne().getMoney() <= 0) ?
                model.getPlayerTwo().toString() : model.getPlayerOne().toString();
        return playersMoneyToString() + "\n" + winner + " WINS!\n";
    }


    /** @return a String representation both player's money */
    private static String playersMoneyToString(){
        return model.getPlayerOne().toString()
                + ": £" + String.valueOf(model.getPlayerOne().getMoney()) + "0\t\t"
                + model.getPlayerTwo().toString()
                + ": £" + String.valueOf(model.getPlayerTwo().getMoney()) + "0\n";
    }


}
