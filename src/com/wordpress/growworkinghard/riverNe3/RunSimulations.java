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

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

public class RunSimulations {

    private volatile static ConcurrentHashMap<Key, Component> binaryTree;
    private volatile static ExecutorService executor;
    private volatile static CountDownLatch latch;
    private volatile static int count;

    public RunSimulations(final HashMap<Key, Component> binaryTree, final ExecutorService exec, final int count) {
        getInstance(binaryTree, exec, count);
    }

    private static void getInstance(final HashMap<Key, Component> inputBinaryTree, final ExecutorService exec, final int counter) {

        if (binaryTree == null) {
            synchronized(RunSimulations.class) {
                if (binaryTree == null) {
                    count = counter;
                    executor = exec;
                    binaryTree
                        = new ConcurrentHashMap<Key, Component>(inputBinaryTree.size(), 0.9f, count);
                    latch = new CountDownLatch(count);
                    binaryTree.putAll(inputBinaryTree);
                }
            }
        }

    }

    public void run() {
        for (int i = 0; i < count; i++)
            executor.submit(new ParallelSimulations(latch));

        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    private void runSim() {

        Component tmpComp = null;
        Component parent = null;
        synchronized(this) {
            Iterator<Key> i = binaryTree.keySet().iterator();
            while(i.hasNext()) {
                Key next = i.next();
                tmpComp = binaryTree.get(next);
                if (tmpComp.isReadyForSimulation()) {
                    binaryTree.remove(next, tmpComp);
                    parent = binaryTree.get(tmpComp.getConnections().getPARENT());
                    break;
                }
            }
        }
        tmpComp.runSimulation(parent);
    }

    private class ParallelSimulations implements Runnable {

        CountDownLatch latch;

        ParallelSimulations(CountDownLatch latch) {
            this.latch = latch;
        }

        public void run() {
            while(!binaryTree.isEmpty()) {
                try {
                    while(!Thread.interrupted()) runSim();
                } finally {
                    latch.countDown();
                }
            }
        }

    }

}
