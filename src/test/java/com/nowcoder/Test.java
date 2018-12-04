package com.nowcoder;

import java.util.Random;

/**
 * Created by ALTERUI on 2018/12/3 9:28
 */

public class Test {
    @org.junit.Test
    public  void test() {
        Random random = new Random();
        for (int i = 0; i < 500; i++) {

            System.out.print(random.nextInt(16)+1+" ");
        }

    }
}
