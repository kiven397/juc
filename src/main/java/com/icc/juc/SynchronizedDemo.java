package com.icc.juc;

import java.util.concurrent.TimeUnit;

/**
 * @Author K
 * @Date 2020/5/24 21:20
 * @Version 1.0
 */
class SeeOk {
    int number = 0;

    public void add() {
        this.number = 20;
    }
}
public class SynchronizedDemo {

    public static void main(String[] args) {
        SeeOk seeOk = new SeeOk();
        new Thread(() -> {
            System.out.println("commin in");
            try { TimeUnit.SECONDS.sleep(1); } catch (Exception e) { e.printStackTrace(); }
            seeOk.add();
            System.out.println(Thread.currentThread().getName() + "update numebr value is " + seeOk.number);
        }, "Input Thread Name").start();


        new Thread(() -> {
            while (seeOk.number ==0) {

            }
            System.out.println("i=20退出");
        }, "Input Thread Name").start();


    }
}
