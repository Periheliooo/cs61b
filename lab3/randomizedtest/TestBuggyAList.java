package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    /**
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> Alst = new AListNoResizing<>();
        BuggyAList<Integer> Blst = new BuggyAList<>();
        Alst.addLast(4);
        Alst.addLast(5);
        Alst.addLast(6);
        Blst.addLast(4);
        Blst.addLast(5);
        Blst.addLast(6);

        assertEquals(Alst.removeLast(), Blst.removeLast());
        assertEquals(Alst.removeLast(), Blst.removeLast());
        assertEquals(Alst.removeLast(), Blst.removeLast());
    }
    */

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                // System.out.println("size: " + L.size());
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0) {
                    // System.out.println("getLast: " + L.getLast());
                    assertEquals(L.getLast(), B.getLast());
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0) {
                    // System.out.println("removeLast: " + L.getLast()); // 打印即将被移除的值
                    assertEquals(L.removeLast(), B.removeLast());
                }
            }
        }
    }
}
