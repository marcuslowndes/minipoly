package controller;


import model.Model;
import model.Player;
import model.Position;
import view.View;


/**
 * The Controller Class for the GUI version of the Minipoly Game, designed to
 * follow the Model-View-Controller design pattern by controlling interactions
 * between the View and the Model.
 * <p>
 * This class allows for validation and communication between the view and the
 * model. When the view needs to change the model, any user inputs from the view
 * are validated through this class before being passed to the model.
 * This class also functions to update the view with information regarding the
 * state of the model and how the user can interact with the view in the future,
 * such as if buttons should be enabled or disabled.
 * <p>
 * Written according to specification provided by Dr. Ian Bailey
 * for Oxford Brookes Computer Science BSc
 * <p> Module COMP6018: Advanced Object Oriented Programming
 *
 * @author Marcus Lowndes
 */
public class Controller {
    private Model model;
    private View view;

    /** Describes the state of a board position. */
    public enum posnState{
        BUYABLE,
        IMPROVABLE,
        NONE
    }

    public void setView(View view){
        this.view = view;
    }


    /**
     * Construct a Controller object.
     *
     * @param model the model that represents the
     *              data structure of the game
     */
    public Controller(Model model){
        this.model = model;
    }


    /**
     * Initiates the next turn using the default method with no parameters.
     * This generates a random dice roll and move the current player across
     * the board.
     *
     * @return a String representation of the move and any rent calculated
     */
    public String nextTurn(){
        return model.nextTurn();
    }


    /**
     * Initiates the next turn with a specified dice roll. Moves the current
     * player across the board a number of positions equivalent to the number
     * passed in. This method ensures that an integer is passed in that is within
     * the limits of two six-sided dice.
     *
     * @param   roll    the roll number chosen by the user input
     * @return  a String representation of the move and any rent calculated
     */
    public String cheatNextTurn(String roll){
        int diceRoll = Integer.parseInt(roll);
        return (diceRoll > 0 && diceRoll < 13) ?
                model.nextTurn(diceRoll) : "Invalid roll.";
    }


    /**
     * The current player will take an action on a position.
     * This could be a buy action or an improve action,
     * depending on the state of the model.
     *
     * @return a String representation of the action taken
     */
    public String interact(){
        return model.interactCurrentPrpt();
    }


    /**
     * @param   id  the view's position pane ID
     * @param   num the model's position number
     * @return  true if the ID number is the same as the position's number
     */
    public boolean isPosn(String id, int num){
        return id.equals(String.valueOf(num));
    }


    /**
     * @param   id          the view's position pane ID
     * @param   isPlayerOne true if the player is Player One,
     *                      false if it's Player Two
     * @return  true if the ID number is the same as the position's number
     */
    public boolean isPlayerOnPosn(String id, boolean isPlayerOne){
        Player p = isPlayerOne ? model.getPlayerOne()
                               : model.getPlayerTwo();

        return isPosn(id, p.getPosition().getNumber());
    }


    /** @return the state of the position as a <code>posnState</code>
     *          enumerator instance */
    public posnState getCurrentPosnState(){
        if (model.isCurrentPosnBuyable())
            return posnState.BUYABLE;

        else if (model.isCurrentPosnImprovable())
            return posnState.IMPROVABLE;

        else
            return posnState.NONE;
    }


    /**
     * @param   i   a position number
     * @return  the Position with the specified number
     */
    public Position getPosition(int i){
        for (Position p : model.getBoard())
            if (p.getNumber() == i)
                return p;

        return null;
    }

}
