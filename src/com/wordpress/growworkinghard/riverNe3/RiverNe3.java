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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.dataReader.DataReading;
import com.wordpress.growworkinghard.riverNe3.dataReader.Reader;
import com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing.DbfLinesProcessing;
import com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing.DbfPointsProcessing;
import com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing.DbfProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.Tree;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.binaryTree.RiverBinaryTree;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.decorator.Hydrometers;

/**
 * @mainpage
 *
 * @image html digitalBasin.jpg "The digital basin"
 *
 * @section Introduction Introduction to the RiverNe3 design
 *
 * In this section I'm going to introduce the main ideas behind the
 * implemetation of this component. Each and every pattern implemented will be
 * descripted.
 *
 * @section AddTree How to add a tree
 *
 * In this section I'm going to introduce how to add a new tree at this flexible
 * structure.
 *
 * @section AddDecorator How to add a decorator
 *
 * In this section I'm going to introduce how to add a decorator at this
 * flexible structure, in order to enrich a plain structure
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class RiverNe3 {
 
    static Tree tb;
    static HashMap<Key, Component> binaryTree;
    static RunSimulations sim;
    static List<Geometry> pointList;
    static int count;
    static List<DataReading> list = new ArrayList<DataReading>();
    static DbfProcessing dfbp;
    static DbfProcessing dbfPoints;
    static CountDownLatch lRead;

    public static void main(String[] args) throws InterruptedException {

        count = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(count);
        String filePath = "/home/francesco/vcs/git/personal/riverNe3/data/net.dbf";
        String filePathPoints = "/home/francesco/vcs/git/personal/riverNe3/data/mon_point.dbf";
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};
        String[] colNamesPoints = {"X_coord", "Y_coord"};

        dfbp = new DbfLinesProcessing(filePath, colNames);
        dbfPoints = new DbfPointsProcessing(filePathPoints, colNamesPoints);

        list.add(dfbp);
        list.add(dbfPoints);

        Reader reader = new Reader(list, executor);
        reader.start();

        pointList = new ArrayList<Geometry>(reader.start().get(1).values());

        tb = new RiverBinaryTree(reader.start().get(0), count);
        CountDownLatch l = new CountDownLatch(count);

        for (int i = 0; i < count; i++)
            executor.submit(new MyRunnable(l));

        try {
            l.await();
        } catch (InterruptedException e) {}


        tb = new Hydrometers(tb, pointList, 500.0);
        tb.buildTree();
        binaryTree = tb.computeNodes();

        sim = new RunSimulations(binaryTree, executor, count);
        sim.run();

        executor.shutdown();

        System.out.println("Exit");
        System.exit(0);

    }

    public static class MyRunnable implements Runnable {

        CountDownLatch l;

        MyRunnable(CountDownLatch l) {
            this.l = l;
        }

        @Override
        public void run() {
            tb.buildTree();
            l.countDown();
        }

    }

}
