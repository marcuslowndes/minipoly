package model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;


/**
 * The Model Class for the Minipoly Game, extending the Observable class to
 * optionally incorporate the Model-View-Controller pattern, but does not
 * require to be implemented using this pattern to operate as intended.
 * To be made traversable, all position objects are stored in a Collection.
 * <p>
 * This provides the data structure and core methods that represent the game
 * without any methods that involve directly displaying the information to
 * the screen, such as GUI or CLI code.
 * <p>
 * Written according to specification provided by Dr. Ian Bailey
 * for Oxford Brookes Computer Science BSc
 * <p> Module COMP6018: Advanced Object Oriented Programming
 *
 * @author Marcus Lowndes
 */
public class Model extends Observable {

    private final ArrayList<Position> board;

    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;

    private final Random rand;
    private boolean firstTurn;
    private int diceRoll;

    public ArrayList<Position> getBoard() {
        return board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public int getDiceRoll(){
        return diceRoll;
    }


    /**
     * Construct a Minipoly game Model.
     * This creates a new game board, including every position that is on
     * the board, and then creates both players and initiates the game.
     *
     * @post the position number cannot end up higher than 40, as that is
     *       the size of the board
     */
    public Model(){
        rand = new Random();

        // construct the game board
        board = new ArrayList<>();
        int posn = 1;

        // add each section of the board, 4 or 5 positions per section
        for (int secn=0; secn<12; secn++){
            // GO
            if (secn == 0)
                board.add(new Position(posn));

            // A,B,C
            else if (secn>0 && secn<4)
                posn = addBoardSection(secn, posn, true);

            // D
            else if (secn == 4)
                posn = addBoardSection(secn, posn, false);

            // JAIL
            else if (secn == 5) {
                posn++;
                board.add(new Position(posn));
            }
            // E,F,G
            else if (secn>5 && secn<9)
                posn = addBoardSection(secn-1, posn, true);

            // H
            else if (secn == 9)
                posn = addBoardSection(secn-1, posn, false);

        }

        assert (posn < 41) : "The position number must not go beyond 40.";

        //construct the players
        playerOne = new Player(true, board.iterator());
        playerTwo = new Player(false, board.iterator());
        currentPlayer = playerOne;
        firstTurn = true;
    }


    /**
     * Add a section  of positions to the game board. Each section represents
     * a road with three numbered properties on it, and one or two blank positions.
     *
     * @param   secnNum     the section number, represented by a char
     * @param   posnNum     the current position number
     * @param   is5Posns    true if this section contains 5 positions,
     *                      otherwise this section contains 4 positions
     * @return  the new position number after the section has been made
     */
    private int addBoardSection(int secnNum, int posnNum, boolean is5Posns){
        char[] roads    = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
        int[][] prices  = { {50, 70}, {100, 120}, {150, 170}, {200, 220},
                            {250, 270}, {300, 320}, {350, 370}, {400, 420} };

        // find section
        char roadName = roads[secnNum-1];
        int[] roadPrices = prices[secnNum-1];

        // create a section of the board position-by-position,
        //      while incrementing the position number
        posnNum++;
        board.add(new Position(posnNum, roadName, 1, roadPrices[0]));
        posnNum++;
        board.add(new Position(posnNum));
        posnNum++;
        board.add(new Position(posnNum, roadName, 2, roadPrices[0]));
        posnNum++;
        board.add(new Position(posnNum, roadName, 3, roadPrices[1]));
        if (is5Posns) {
            posnNum++;
            board.add(new Position(posnNum));
            return posnNum;
        }

        return posnNum;
    }


    /**
     * Initiate the next turn, roll the dice and move the player counters.
     * Swaps the current player to initiate a new turn, then simulates the roll
     * of two 6-sided dice and moves a player that many positions on the board,
     * before calculating and charging any rent for the position that is landed on.
     *
     * @return  a String representation of the move made by the current player,
     *          including representation of any rent that is calculated.
     */
    public String nextTurn(){
        return nextTurn(
                (rand.nextInt(6) + 1)
                        + (rand.nextInt(6) + 1)
        );
    }


    /**
     * Initiate the next turn, roll the dice and move the player counters.
     * Swaps the current player to initiate a new turn, then moves a player
     * a specified number of positions on the board, before calculating and
     * charging any rent for the position that is landed on.
     *
     * @param   roll    a specified dice roll
     * @pre     roll must be in the range of 1-12, to simulate two six-sided dice
     * @return  a String representation of the move made by the current player,
     *          including representation of any rent that is calculated.
     */
    public String nextTurn(int roll){
        assert (roll > 0 && roll < 13) : "This is not a valid roll. Must be"
                + " equivalent to the total of two six-sided dice.";

        String posStr = "";
        diceRoll = roll;

        // swap current player
        if (!firstTurn){
            currentPlayer = (currentPlayer != playerOne) ? playerOne : playerTwo;
        }

        // move current player
        Position prevPosn = currentPlayer.getPosition();
        Position nextPosn = currentPlayer.move(diceRoll, board.iterator());

        // if player lands on Jail, they are moved to Go
        if (nextPosn.getNumber() == 21) {
            nextPosn = currentPlayer.move(20, board.iterator());
            posStr += "21 (JAIL)\nSent back to Posn ";
        }

        // calculate rent
        String s = isCurrentPosnRentable() ? calculateRent() : "";

        // update any views before returning control to the user
        firstTurn = false;
        setChanged();
        notifyObservers();

        return currentPlayer.toString() + " rolls " + String.valueOf(diceRoll)
                + "\nMoving from Posn " + String.valueOf(prevPosn.getNumber())
                + " to Posn " + posStr + String.valueOf(nextPosn.getNumber()) + s;
    }


    /**
     * Calculate and charge any rent owed the owner of the property that the
     * current player has landed on, if the current player is not themselves
     * the owner.
     *
     * @pre     <code>posn</code> requires the current player to rent it
     * @return  a string representation of the rent that has been calculated
     *          and the transaction between the players
     */
    private String calculateRent(){
        assert (isCurrentPosnRentable()) : "This position does not require rent";

        Position posn = currentPlayer.getPosition();
        double rent = 0;

        // calculate rent if owner owns whole road (set/section)...
        if (posn.getOwner().ownsAllPrptsOnRoad(posn.getRoad())) {
            rent = 0.2 * posn.getPrice();

            // ...and if owner owns a hotel on the property
            if (posn.isHotel())
                rent += 0.1 * ((4.0 * 0.5 * posn.getPrice())
                               + (0.8 * posn.getPrice()));

            // ...and if owner owns improvements on the prpt
            if (posn.getImprovements() > 0) {
                rent += 0.1 * (posn.getImprovements()
                               * 0.5 * posn.getPrice());
            }
        } else {
            // otherwise, basic rent is calculated
            rent += 0.1 * posn.getPrice();
        }

        // rent transaction
        currentPlayer.setMoney(-rent);
        if (currentPlayer != playerOne)
            playerOne.setMoney(rent);
        else
            playerTwo.setMoney(rent);

        return "\nThis position is owned by " + posn.getOwner().toString()
                + ", therefore " + currentPlayer.toString()
                + " is\ncharged £" + String.valueOf(rent) + "0 in rent.";
    }


    /**
     * Take an action on a position, given that it is a property.
     * If the property is considered buyable, the buy action is taken.
     * If the property is considered improvable, the improve action is taken.
     *
     * @pre     the current player's position is buyable or improvable
     * @return  a string representation of the action taken on that property
     */
    public String interactCurrentPrpt(){
        assert (isCurrentPosnBuyable() || isCurrentPosnImprovable()) :
                "This position is not buyable or improvable.";

        Position currentPosn = currentPlayer.getPosition();

        String s;
        if (currentPosn.getOwner() == null){
            s = currentPosn.buy(currentPlayer);
            setChanged();
            notifyObservers();
            return s;
        }
        else {
            s = currentPosn.improve(currentPlayer);
            setChanged();
            notifyObservers();
            return s;
        }
    }


    /** @return true if is required for the current player to rent
     *          their position */
    private boolean isCurrentPosnRentable(){
        return  currentPlayer.getPosition().isProperty()
                && currentPlayer.getPosition().getOwner() != null
                && currentPlayer != currentPlayer.getPosition().getOwner();
    }


    /** @return true if it is valid for the current player to buy
     *          their position */
    public boolean isCurrentPosnBuyable(){
        return  currentPlayer.getPosition().isProperty()
                && currentPlayer.getPosition().getOwner() == null;
    }


    /** @return true if it is valid for the current player to improve
     *          their position */
    public boolean isCurrentPosnImprovable(){
        return  currentPlayer.getPosition().isProperty()
                && (currentPlayer.ownsAllPrptsOnRoad(
                        currentPlayer.getPosition().getRoad()
                ) && !currentPlayer.getPosition().isHotel());
    }


    /** @return true if a player has run out of money and the game is over */
    public boolean isGameOver() {
        return (playerOne.getMoney() <= 0 || playerTwo.getMoney() <= 0);
    }


    /**
     * @param   p   a specified position on the board
     * @return  a String representation of all the players on the position
     */
    private String playersOnPosnToString(Position p){
        String s = "";
        if (!p.isProperty() && p.getNumber() != 1
                && p.getNumber() != 21)
            s += "\t\t\t";

        if (playerOne.getPosition().equals(p))
            s += "\t" + playerOne.toString();

        if (playerTwo.getPosition().equals(p))
            s += "\t" + playerTwo.toString();

        return s;
    }


    /** @return a String representation of the entire game board */
    @Override
    public String toString(){
        String s = "Posn\tName\tPrice\tOwner\tImpvmts\tPlayerCounters\n";
        for (Position p: board)
            s += (p.toString() + playersOnPosnToString(p) + "\n");
        return s;
    }
}
