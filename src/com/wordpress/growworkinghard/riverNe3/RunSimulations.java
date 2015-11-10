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

    private volatile ConcurrentHashMap<Key, Component> binaryTree;
    private final CountDownLatch latch;
    private final ExecutorService executor;
    private final int concurrencyLevel;

    public RunSimulations(final HashMap<Key, Component> binaryTree, final ExecutorService executor, final int threadsNumber) {
        this.concurrencyLevel = threadsNumber;
        this.executor = executor;
        this.binaryTree
            = new ConcurrentHashMap<Key, Component>(binaryTree.size(),
                                                    0.9f,
                                                    this.concurrencyLevel);
        this.latch = new CountDownLatch(threadsNumber);
        this.binaryTree.putAll(binaryTree);
    }

    public void run() {
        for (int i = 0; i < concurrencyLevel; i++)
            executor.submit(new ParallelSimulations(latch));

        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    private void runSim() {

        Component tmpComp = null;
        Component parent = null;
        synchronized(this) {
            Iterator<Key> iterator = binaryTree.keySet().iterator();
            while(iterator.hasNext()) {
                Key next = iterator.next();
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

        private final CountDownLatch latch;

        ParallelSimulations(CountDownLatch latch) { this.latch = latch; }

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
