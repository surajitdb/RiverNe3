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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.BinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfLinesProcessing;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfPointsProcessing;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.traverser.RiverBinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.BinaryTree;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.RiverBinaryTree;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.decorator.Hydrometers;

public class RiverNe3 {
 
    static HashMap<Integer, Geometry> test;
    static HashMap<Integer, Geometry> points;
    static BinaryTree tb;
    static HashMap<Key, Component> binaryTree;

    public static void main(String[] args) {
    
        DbfProcessing dfbp;
        DbfProcessing dbfPoints;
        String filePath = "/home/francesco/vcs/git/personal/riverNe3/data/net.dbf";
        String filePathPoints = "/home/francesco/vcs/git/personal/riverNe3/data/mon_point.dbf";
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};
        String[] colNamesPoints = {"X_coord", "Y_coord"};

        dfbp = new DbfLinesProcessing();
        dfbp.process(filePath, colNames);
        test = dfbp.get();

        dbfPoints = new DbfPointsProcessing();
        dbfPoints.process(filePathPoints, colNamesPoints);
        points = dbfPoints.get();

        tb = new RiverBinaryTree(test, 4);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch l = new CountDownLatch(4);

        for (int i = 0; i < 4; i++)
            executor.submit(new MyRunnable(l));

        try {
            l.await();
        } catch (InterruptedException e) {}

        executor.shutdown();


        List<Geometry> pointList = new ArrayList<Geometry>(points.values());
        tb = new Hydrometers(tb, pointList, 500.0);
        tb.buildTree();
        binaryTree = tb.computeNodes();

        Iterator<Key> it = binaryTree.keySet().iterator();
        while(it.hasNext()) {
            Key next = it.next();
            Component comp = binaryTree.get(next);

            System.out.println(comp.toString());

        }




        // BinaryTreeTraverser<Component> traverser = new RiverBinaryTreeTraverser(binaryTree);
        // Key key = new Key(3.0);
        // // FluentIterable<Component> iterator = traverser.postOrderTraversal(binaryTree.get(key));
        // // List<Component> list = iterator.toList();
        // // Iterator<Component> it = list.iterator();

        // Component node = binaryTree.get(key);
        // node.setTraverser(traverser);
        // List<Component> list = node.preOrderTraversal();
        // Iterator<Component> it = list.iterator();
        // while(it.hasNext()) {
        //     Component tmp = it.next();
        //     System.out.println(tmp.getKey().getDouble());
        // }

    }

    public static class MyRunnable implements Runnable {

        CountDownLatch l;

        MyRunnable(CountDownLatch l) {
            this.l = l;
        }

        @Override
        public void run() {
            // System.out.println(Thread.currentThread().getName() + " start thread read tree");
            tb.buildTree();
            // System.out.println(Thread.currentThread().getName() + " end thread read tree");
            l.countDown();
        }

    }

}
