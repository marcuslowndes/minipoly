package model;


/**
 * The Position class represents a position on the board.
 * Each position has a position number. The position can be constructed as a
 * property, and if so, will also be given a road name and number on that road.
 * It is also given a price, and can be bought and improved upon.
 * <p>
 * Polymorphism was attempted, to implement this as 3 classes:
 * <ul>
 *      <li>Position</li>
 *      <li>Property (implements Position)</li>
 *      <li>SpecialPosition (implements Position)</li>
 * </ul>
 * However, this was seen as inelegant, since all of the methods involved in
 * Properties such as buying and improving were unsupported exceptions in the
 * Position class, and there was too much use of the <code>instanceof</code>
 * keyword throughout the program, which is seen as a bad code smell.
 * Hence, these classes have been collapsed back into one class.
 *
 * @author Marcus Lowndes
 */
public class Position {
    private final int number;
    private boolean property;
    private final char road;
    private final int roadNumber;
    private final double price;
    private Player owner = null;
    private int improvements = 0;
    private boolean hotel = false;

    public int getNumber() {
        return number;
    }

    public boolean isProperty(){
        return property;
    }

    public char getRoad() {
        return road;
    }

    public double getPrice() {
        return price;
    }

    public Player getOwner() {
        return owner;
    }

    public double getImprovements() {
        return improvements;
    }

    public boolean isHotel() {
        return hotel;
    }


    /**
     * Construct a Minipoly game property Position with an associated number,
     * road, number on that road, and price.
     *
     * @param   posnNum         the property's position number
     * @param   roadName        the name of the section/set/road
     * @param   roadNum         the number of the property on that road
     * @param   propertyPrice   the price of the property
     */
    protected Position(int posnNum, char roadName,
                       int roadNum, int propertyPrice){
        number = posnNum;
        road = roadName;
        roadNumber = roadNum;
        price = propertyPrice;
        property = true;
    }

    /**
     * Construct a standard Minipoly game Position with an associated number.
     *
     * @param posnNum the position's number
     */
    protected Position(int posnNum) {
        this(posnNum, ' ', 0, 0);
        property = false;
    }


    /** @return a String representation of the position's name */
    public String nameToString() {
        switch (number){
            case 1:
                return "GO";
            case 21:
                return "JAIL";
        }

        return property ?
                String.valueOf(road) + String.valueOf(roadNumber) : "";
    }


    /**
     * The Player that is passed into this method will purchase this property
     * Position, and the price of this property will be charged from that
     * player's bank account.
     *
     * @param   buyer   the player that is buying the property
     * @pre     this position is a property
     * @pre     this property position currently has no owner
     * @return  a String representation of the transaction
     */
    protected String buy(Player buyer){
        assert (property)       : "This is not a property.";
        assert (owner == null)  : "This property already has an owner.";

        buyer.setMoney(-price);
        owner = buyer;
        buyer.addProperty(this);

        return "" + nameToString() + " has been bought by " + buyer.toString()
                + " for £" + String.valueOf(price) + "0";
    }


    /**
     * The Player that is passed into this method will improve this property
     * Position, and the cost of the improvement will be charged from that
     * player's bank account. The cost is different depending on if a standard
     * improvement (a house) or a hotel is built at the property.
     *
     * @param   improver    the player that is improving the property
     * @pre     this position is a property
     * @pre     the improver is the owner of the property
     * @pre     the property is not already a hotel
     * @post    the improvements cannot be increased to more than 4
     * @return  a String representation of the transaction
     */
    protected String improve(Player improver){
        assert (property)           : "This is not a property.";
        assert (improver == owner)  : "The player does not own this property.";
        assert (!hotel)             : "This property cannot be improved further.";

        String posnType = "";
        double cost = 0;

        // if there are 4 improvements, buy a hotel
        if (improvements == 4) {
            cost = 0.8 * price;
            improver.setMoney(-cost);
            improvements = 0;
            hotel = true;
            posnType = "hotel";
        }

        // if there are less than 4 improvements, buy an improvement
        else if (improvements < 4){
            cost = 0.5 * price;
            improver.setMoney(-cost);
            improvements++;
            posnType = "house";
        }

        assert (!(improvements > 4)) : "Improvements cannot advance beyond 4.";

        return improver.toString() + " pays £" + String.valueOf(cost)
                + "0 to build a " + posnType + " at the property.";
    }


    @Override
    public String toString(){
        String i = "", o = "";

        i += String.valueOf(improvements);
        if (hotel)
            i = "Hotel";

        if (owner != null)
            o += " " + owner.toString();
        else
            o += " []";

        String s = String.valueOf(number) + ":\t " + nameToString();
        if (property)
            return s + "\t£" + String.valueOf(price) + "0\t" + o + "\t" + i;
        else if (number == 1 || number == 21)
            return s + "\t\t\t";
        else
            return s;
    }

}
