package simpledb;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import junit.framework.JUnit4TestAdapter;

public class HeapPageIdTest {

    private HeapPageId pid;

    @Before public void setUp() {
        pid = new HeapPageId(1, 1);
    }

    /**
     * Unit test for HeapPageId.tableid()
     */
    @Test public void tableid() {
        assertEquals(1, pid.tableid());
    }

    /**
     * Unit test for HeapPageId.pageno()
     */
    @Test public void pageno() {
        assertEquals(1, pid.pageno());
    }

    /**
     * Unit test for HeapPageId.hashCode()
     */
    @Test public void testHashCode() {
        int code1, code2;

        // NOTE(ghuo): the hashCode could be anything. test determinism,
        // at least.
        pid = new HeapPageId(1, 1);
        code1 = pid.hashCode();
        assertEquals(code1, pid.hashCode());
        assertEquals(code1, pid.hashCode());

        pid = new HeapPageId(2, 2);
        code2 = pid.hashCode();
        assertEquals(code2, pid.hashCode());
        assertEquals(code2, pid.hashCode());
    }

    /**
     * Unit test for HeapPageId.equals()
     */
    @Test public void equals() {
        HeapPageId pid1 = new HeapPageId(1, 1);
        HeapPageId pid1Copy = new HeapPageId(1, 1);
        HeapPageId pid2 = new HeapPageId(2, 2);

        // .equals() with null should return false
        assertFalse(pid1.equals(null));

        // .equals() with the wrong type should return false
        assertFalse(pid1.equals(new Object()));

        assertTrue(pid1.equals(pid1));
        assertTrue(pid1.equals(pid1Copy));
        assertTrue(pid1Copy.equals(pid1));
        assertTrue(pid2.equals(pid2));

        assertFalse(pid1.equals(pid2));
        assertFalse(pid1Copy.equals(pid2));
        assertFalse(pid2.equals(pid1));
        assertFalse(pid2.equals(pid1Copy));
    }

    /**
     * JUnit suite target
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HeapPageIdTest.class);
    }
}

