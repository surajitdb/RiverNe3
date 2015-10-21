/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wordpress.growworkinghard.riverNe3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfLinesProcessing;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

public class RiverNe3 {
 
    static DbfProcessing dfbp;
    static ConcurrentHashMap<Integer, Geometry> test;
    static TreeBuilding tb;
    static ConcurrentHashMap<Integer, Component> binaryTree;

    public static void main(String[] args) {
    
        String filePath = "/home/francesco/vcs/git/personal/riverNe3/data/net.dbf";
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};

        dfbp = new DbfLinesProcessing();
        dfbp.process(filePath, colNames);
        test = dfbp.get();

        tb = new TreeBuilding();

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch l = new CountDownLatch(4);

        for (int i = 0; i < 4; i++)
            executor.submit(new MyRunnable(l));

        try {
            l.await();
        } catch (InterruptedException e) {}

        executor.shutdown();
        binaryTree = tb.get();
        System.out.println(binaryTree);

    }

    public static class MyRunnable implements Runnable {

        CountDownLatch l;

        MyRunnable(CountDownLatch l) {
            this.l = l;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " start thread read tree");
            tb.buildTree(test);
            System.out.println(Thread.currentThread().getName() + " end thread read tree");
            l.countDown();
        }

    }

}
