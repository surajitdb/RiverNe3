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

import net.jcip.annotations.ThreadSafe;

/**
 * @brief Run multithreading simulations from an executor pool
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class RunSimulations {

    private volatile ConcurrentHashMap<Key, Component> tree; //!< tree structure
    private final CountDownLatch latch; //!< latch to synchronize the progress of threads
    private final ExecutorService executor; //!< executor to process tasks asynchronously
    private final int concurrencyLevel; //!< the running threads

    /**
     * @brief Constructor
     *
     * @description The <tt>ExecutorService</tt> is passed to the constructor in
     *              order to use only one executor for all the tree process.
     *              Quoting @cite goetz2006:java <blockquote>Reusing an existing
     *              thread instead of creating a new one amortizes thread
     *              creation and teardown costs over multiple requests. As an
     *              added bonus, since the worker thread often already exists at
     *              the time the request arrives, the latency associated with
     *              thread creation does not delay task execution, this
     *              improving responsiveness.</blockquote>
     *
     * @param[in] tree The tree structure
     * @param[in] executor The main executor service
     * @param[in] threadsNumber The number of threads available
     */
    public RunSimulations(final HashMap<Key, Component> tree, final ExecutorService executor, final int threadsNumber) {
        this.concurrencyLevel = threadsNumber; // the running threads available
        this.executor = executor;
        int size = tree.size(); // the initial size
        float loadFactor = 0.9f; // dense packaging which will optimize memory use
        this.tree
            = new ConcurrentHashMap<Key, Component>(size,
                                                    loadFactor,
                                                    this.concurrencyLevel);
        this.latch = new CountDownLatch(threadsNumber);
        this.tree.putAll(tree);
    }

    /**
     * @brief Submit tasks to the executor
     */
    public void run() throws InterruptedException {
        for (int i = 0; i < concurrencyLevel; i++)
            executor.submit(new ParallelSimulations(latch));

        latch.await();
    }

    /**
     * @brief Run simulations
     *
     * @description 
     */
    private void runSim() {

        Component tmpComp = null;
        Component parent = null;
        synchronized(this) {
            Iterator<Key> iterator = tree.keySet().iterator();
            while(iterator.hasNext()) {
                Key next = iterator.next();
                tmpComp = tree.get(next);
                if (tmpComp.isReadyForSimulation()) {
                    tree.remove(next, tmpComp);
                    parent = tree.get(tmpComp.getConnections().getPARENT());
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
            while(!tree.isEmpty()) {
                try {
                    while(!Thread.interrupted()) runSim();
                } finally {
                    latch.countDown();
                }
            }
        }

    }

}
