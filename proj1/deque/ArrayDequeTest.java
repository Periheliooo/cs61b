package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Optional;

public class ArrayDequeTest {

    @Test
    public void randomizedTest() {
        // 你的数据结构
        ArrayDeque<Integer> bugDeque = new ArrayDeque<>();
        // Java 官方肯定正确的数据结构（作为标准答案）
        java.util.LinkedList<Integer> correctDeque = new java.util.LinkedList<>();

        int N = 5000; // 模拟 5 万次随机操作
        for (int i = 0; i < N; i++) {
            // 生成 0 到 4 之间的随机整数，决定这次做什么操作
            int operationNumber = StdRandom.uniform(0, 5);

            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                System.out.println("bugDeque.addFirst(" + randVal + ");");
                bugDeque.addFirst(randVal);
                correctDeque.addFirst(randVal);
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                System.out.println("bugDeque.addLast(" + randVal + ");");
                bugDeque.addLast(randVal);
                correctDeque.addLast(randVal);
            } else if (operationNumber == 2) {
                // size
                System.out.println("bugDeque.size();");
                assertEquals(correctDeque.size(), bugDeque.size());
            } else if (operationNumber == 3 && correctDeque.size() > 0) {
                // removeFirst
                System.out.println("bugDeque.removeFirst();");
                Integer expected = correctDeque.removeFirst();
                Integer actual = bugDeque.removeFirst();
                assertEquals(expected, actual);
            } else if (operationNumber == 4 && correctDeque.size() > 0) {
                // removeLast
                System.out.println("bugDeque.removeLast();");
                Integer expected = correctDeque.removeLast();
                Integer actual = bugDeque.removeLast();
                assertEquals(expected, actual);
            }
        }
    }


    //@Test
    public void reproduceBugTest() {
        ArrayDeque<Integer> bugDeque = new ArrayDeque<>();
        // 把你从控制台抄下来的罪魁祸首代码粘贴在这里：
        bugDeque.addLast(65);
        bugDeque.addFirst(61);
        bugDeque.removeFirst();
        bugDeque.addLast(19);
        bugDeque.removeLast();
        bugDeque.size();
        bugDeque.addFirst(48);
        bugDeque.addLast(77);
        bugDeque.size();
        bugDeque.size();
        bugDeque.addLast(91);
        bugDeque.removeFirst();
        bugDeque.addLast(0);
        bugDeque.size();
        bugDeque.addLast(16);
        bugDeque.addLast(32);
        bugDeque.addFirst(27);
        bugDeque.addFirst(80);
        bugDeque.addLast(50);
        bugDeque.addFirst(0);
        bugDeque.removeLast();
        bugDeque.size();
        bugDeque.size();
        bugDeque.addFirst(7);
        bugDeque.removeLast();
        // 验证错误是否能稳定复现
        Integer actual = bugDeque.removeLast(); // 这里本该返回 99，老代码会返回 null
        Integer expected = 32;
        assertEquals(expected, actual);
    }

}