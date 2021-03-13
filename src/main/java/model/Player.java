package model;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * The Player class represents a player of the Minipoly game.
 * There can only be 2 players to the Minipoly game, the player has a position
 * and a way of traversing through the board, and is allocated some money to
 * buy and improve properties. The player can buy properties and if they all
 * properties in a set they can improve them.
 *
 * @author Marcus Lowndes
 */
public class Player {
    private final boolean playerOne;
    private final ArrayList<Position> properties = new ArrayList<>();
    private Position position;
    private Iterator<Position> positionIter;
    private double money;

    public boolean isPlayerOne() {
        return playerOne;
    }

    public Position getPosition() {
        return position;
    }

    public double getMoney() {
        return money;
    }


    /**
     * Construct a Minipoly game Player, with £2000.00 in it's bank account.
     * A Player object can only be constructed within the <code>minpolymodel</code>
     * package, and is only ever actually constructed by the Model itself.
     *
     * @param   isPlayerOne     true if this player is Player 1. otherwise,
     *                          this is player 2
     * @param   posIter         an iterator to track the position that this player
     *                          is on, on the board
     */
    protected Player(boolean isPlayerOne, Iterator<Position> posIter){
        playerOne = isPlayerOne;
        money = 2000.00;
        positionIter = posIter;
        position = positionIter.next();
    }


    /**
     * Construct a Minipoly game Player, with a specified amount of money in it's
     * bank account.
     * A Player object can only be constructed within the <code>minpolymodel</code>
     * package, and is only ever actually constructed by the Model itself.
     *
     * @param   isPlayerOne     true if this player is Player 1. otherwise,
     *                          this is player 2
     * @param   posIter         an iterator to track the position that this player
     *                          is on, on the board
     * @param   startingMoney   the amount of money the player starts with.
     *                          typically, this will be £2000.00
     */
    protected Player( boolean isPlayerOne,
                      Iterator<Position> posIter,
                      int startingMoney ){
        this(isPlayerOne, posIter);
        money = startingMoney;
    }


    /**
     * Move the player through the board. This method uses iterators to access
     * the Position objects on the board, and iterates along them <code>num</code>
     * number of times. If the player reaches the end of the board, they will cycle
     * back to the beginning of the board using a fresh iterator.
     *
     * @param   num         for this number of loops, the player's position will
     *                      iterate along the board
     * @param   boardIter   a fresh iterator to reset the player's iterator if the
     *                      player has reached the end of the board
     * @return  the position that the player arrives on after the move is made
     */
    protected Position move(int num, Iterator<Position> boardIter){
        for (int i=0; i<num; i++) {
            if (!positionIter.hasNext())
                positionIter = boardIter;

            position = positionIter.next();
        }
        return position;
    }


    /** @return true if player owns all 3 of the properties of a given set/road */
    public boolean ownsAllPrptsOnRoad(char road){
        int count = 0;

        if (!properties.isEmpty())
            for(Position p: properties)
                if (p.getRoad() == road)
                    count ++;

        return (count == 3);
    }


    /** @param  money   the amount of money to be given to the player */
    protected void setMoney(double money) {
        this.money += money;
    }


    /**
     * @param   p   the property to be given to the player
     * @pre     the position provided must be a property
     */
    protected void addProperty(Position p){
        assert(p.isProperty()) : "Not a property";
        properties.add(p);
    }


    /** @return a String representation of the player */
    @Override
    public String toString(){
        return playerOne ? "[P1]" : "[P2]";
    }
}
