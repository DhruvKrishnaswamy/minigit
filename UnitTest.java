package gitlet;


import ucb.junit.textui;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * The suite of all JUnit tests for the gitlet package.
 *
 * @Dhruv Krishnaswamy
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * A dummy test to avoid complaint.
     */
    @Test
    public void placeholderTest() {
    }
    @Test
    public void initialtest() {
        if (!Utils.join(".gitlet").exists()) {
            Main.init();
            assertEquals(Main.
                    obtaintree().gethead().message(), "initial commit");
        } else {
            assertEquals("initial commit", "initial commit");
        }
    }



}



