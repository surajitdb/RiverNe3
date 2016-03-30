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
package com.wordpress.growworkinghard.riverNe3.tree.building;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

/**
 * @brief Main interface of the <strong>Decorator Pattern</strong> for the tree
 *        building
 *
 * @description In order to apply the design principle for which <blockquote>-
 *              Classes should be open for extension, but closed for
 *              modification -</blockquote>, the <strong>Decorator
 *              Pattern</strong> @cite freeman2004:head has been implemented for
 *              solving the issue of building the tree structure.
 *              <blockquote> <strong>The Decorator Pattern</strong> attaches
 *              additional responsibilities to an object dynamically. Decorators
 *              provide a flexible alternative to subclassing for extending
 *              functionality. </blockquote> In this way, each developer can
 *              easily implement the building of its own tree type of
 *              <tt>Tree</tt> implementing this interface. The decorations,
 *              always of type <tt>Tree</tt> but extending an abstract class
 *              (e.g. BinaryTreeDecorator), are features added to the tree as
 *              new nodes (e.g. dams, monitoring points, lakes, intakes,
 *              outakes) or as new states for a node.
 *              <p>
 *              The main idea is:
 *              <ol>
 *              <li>after having parsed the input data, build the <tt>Tree</tt>
 *              object. This is the starting point: an <tt>HashMap</tt> with the
 *              <tt>Tree</tt> structure.</li>
 *              <li>then, the user can add further feauters wrapping the tree
 *              and eventually each decorator already applied to the tree.</li>
 *              </ol>
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Tree {

    /**
     * @brief Method which returns the tree structure
     *
     * @description This method must be implemented both on a class which build
     *              the tree (e.g. binaryTree.RiverBinaryTree) and on decorator
     *              classes (e.g. decorator.Hydrometers) because it returns the
     *              structure of the tree. It works as <strong>wrapper</strong>
     *              once it is called from a decorator class, adding features to
     *              a plain tree or adding more features to an already wrapped
     *              tree, following the idea of the <strong>Decorator
     *              Pattern</strong>.
     *              <p>
     *              To reduce the amount of memory allocated, this class returns
     *              a simple <tt>HashMap</tt> and not the Concurrent version.
     *              Based on the user necessities, she/he has to convert it in
     *              concurrent one if it is going to use it on a multi-threading
     *              code.
     *              </p>
     *
     * @return The <tt>HashMap</tt> with the structure of the tree
     */
    abstract public HashMap<Key, Component> computeNodes();

    abstract protected void buildTree();

    protected void parallelBuildTree(final ExecutorService executor, final int threadsNumber) {

        CountDownLatch l = new CountDownLatch(threadsNumber);

        for (int i = 0; i < threadsNumber; i++)
            executor.submit(new MyRunnable(l));

        try {
            l.await();
        } catch (InterruptedException e) {}

    }

    protected class MyRunnable implements Runnable {

        CountDownLatch l;

        MyRunnable(CountDownLatch l) {
            this.l = l;
        }

        @Override
        public void run() {
            buildTree();
            l.countDown();
        }

    }

}
