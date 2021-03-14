package model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * JUnit Tests for the Model Class
 *
 * @author Marcus Lowndes
 */
public class ModelTest {
    
    public ModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Start Model Tests");
        System.out.println("=================================\n");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("End Model Tests\n");
    }
    
    @Before
    public void setUp() {
        System.out.println("Test start");
    }
    
    @After
    public void tearDown() {
        System.out.println("End test\n");
        System.out.println("=================================\n");
    }


    /**
     * Test of nextTurn method, of class Model, given no arguments.
     * Expected: A Player 1 rolls the dice and randomly moves a number
     * of positions equivalent to that roll.
     */
    @Test
    public void testNextTurnNoAargs() {
        System.out.println("nextTurn, no arguments");
        Model instance = new Model();

        String result = instance.nextTurn();
        int roll = instance.getDiceRoll();
        String expResult = "[P1] rolls " + String.valueOf(roll) + "\nMoving"
                + " from Posn 1 to Posn " + String.valueOf(roll + 1);

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of nextTurn method, of class Model, given an integer argument.
     * Expected: Player 1 rolls 6 and moves 6 positions,
     * from position 1 to position 7.
     */
    @Test
    public void testNextTurnInt() {
        System.out.println("nextTurn, int argument");
        int roll = 6;
        Model instance = new Model();

        String expResult = "[P1] rolls 6\nMoving from Posn 1 to Posn 7";
        String result = instance.nextTurn(roll);

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of nextTurn method, of class Model.
     * Expected: A roll of 12 is within the upper boundary limit,
     * because it is a valid roll of two six-sided dice.
     */
    @Test
    public void testNextTurnIntInsideUpperBoundary() {
        System.out.println("nextTurn, int argument, in upper boundary");
        int roll = 12;
        Model instance = new Model();

        String expResult = "[P1] rolls 12\nMoving from Posn 1 to Posn 13";
        String result = instance.nextTurn(roll);

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of nextTurn method, of class Model.
     * Expected: A roll of 13 is not within the upper boundary limit,
     * because it is not a valid roll of two six-sided dice.
     */
    @Test
    public void testNextTurnIntOutsideUpperBoundary() {
        System.out.println("nextTurn, int argument, out upper boundary");
        int roll = 13;
        Model instance = new Model();
        
        Error assError = assertThrows(AssertionError.class, () -> {
            instance.nextTurn(roll);
        });
        String expError = "This is not a valid roll. Must be"
                + " equivalent to the total of two six-sided dice.";
        String result = assError.getMessage();

        assertEquals(expError, result);
        System.out.println("\nExpected:\n" + expError);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of nextTurn method, of class Model.
     * Expected: A roll of 1 is within the lower boundary limit,
     * because it is a valid roll of two six-sided dice.
     */
    @Test
    public void testNextTurnIntInsideLowerBoundary() {
        System.out.println("nextTurn, int argument, in lower boundary");
        int roll = 1;
        Model instance = new Model();

        String expResult = "[P1] rolls 1\nMoving from Posn 1 to Posn 2";
        String result = instance.nextTurn(roll);

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of nextTurn method, of class Model.
     * Expected: A roll of 0 is not within the lower boundary limit,
     * because it is not a valid roll of two six-sided dice.
     */
    @Test
    public void testNextTurnIntOutsideLowerBoundary() {
        System.out.println("nextTurn, int argument, out lower boundary");
        int roll = 0;
        Model instance = new Model();
        
        Error assError = assertThrows(AssertionError.class, () -> {
            instance.nextTurn(roll);
        });
        String expError = "This is not a valid roll. Must be"
                + " equivalent to the total of two six-sided dice.";
        String result = assError.getMessage();

        assertEquals(expError, result);
        System.out.println("\nExpected:\n" + expError);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of interactCurrentPrpt method, of class Model.
     * Player 1 is moved to a buyable position and then interacts with it.
     * Expected: The player buys the position.
     */
    @Test
    public void testInteractCurrentPrptBuy() {
        System.out.println("interactCurrentPrpt, buy a property");
        Model instance = new Model();
        System.out.println(instance.nextTurn(1));

        String expResult = "A1 has been bought by [P1] for \u00a350.00";
        String result = instance.interactCurrentPrpt();

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }
    
    private void testInteractCurrentPrptImproveSetUp(Model instance){
        System.out.println(instance.nextTurn(1));
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.nextTurn());
        System.out.println(instance.nextTurn(2));
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.nextTurn());
        System.out.println(instance.nextTurn(1));
        System.out.println(instance.interactCurrentPrpt());
    }


    /**
     * Test of interactCurrentPrpt method, of class Model.
     * Player 1 is buys the first three properties and then improves
     * the third one.
     * Expected: The player improves that property.
     */
    @Test
    public void testInteractCurrentPrptImprove() {
        System.out.println("interactCurrentPrpt, improve a property");
        Model instance = new Model();
        testInteractCurrentPrptImproveSetUp(instance);

        String expResult = "[P1] pays \u00a335.00 to build a house at the property.";
        String result = instance.interactCurrentPrpt();

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of interactCurrentPrpt method, of class Model.
     * Player 1 is buys the first three properties and then improves
     * the third one until it builds a hotel.
     * Expected: The player improves that property 5 times, to build a hotel.
     */
    @Test
    public void testInteractCurrentPrptImproveToHotel() {
        System.out.println("interactCurrentPrpt, improve a property to hotel");
        Model instance = new Model();
        testInteractCurrentPrptImproveSetUp(instance);
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());

        String expResult = "[P1] pays \u00a356.00 to build a hotel at the property.";
        String result = instance.interactCurrentPrpt();

        assertEquals(expResult, result);
        System.out.println("\nExpected:\n" + expResult);
        System.out.println("\nResult:\n" + result);
    }


    /**
     * Test of interactCurrentPrpt method, of class Model.
     * Player 1 is cannot improve a property once a hotel is built
     * Expected: The player attempts to improve that property 6 times,
     * but an exception is caught.
     */
    @Test
    public void testInteractCurrentPrptImproveWithHotel() {
        System.out.println("interactCurrentPrpt, improve a property to hotel");
        Model instance = new Model();
        testInteractCurrentPrptImproveSetUp(instance);
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        System.out.println(instance.interactCurrentPrpt());
        
        Error assError = assertThrows(AssertionError.class, () -> {
            instance.interactCurrentPrpt();
        });

        // 3 possible assertion errors may be encountered
        String[] expErrors = {
            "This position is not buyable or improvable.",
            "This property cannot be improved further.",
            "Improvements cannot advance beyond 4."
        };
        String expError = "No error found";
        String result = assError.getMessage();

        // check for each possible error, if one encountered break
        boolean errorEncountered = false;
        for (String e : expErrors) {
            errorEncountered = e.equals(result);
            expError = errorEncountered ? e : expError;
            if (errorEncountered) break;
        }

        assertTrue(errorEncountered);
        System.out.println("\nExpected (Could be either one of these):");
        for (String e : expErrors)
            System.out.println(e);
        System.out.println("\nResult:\n" + result);
    }
    
}
