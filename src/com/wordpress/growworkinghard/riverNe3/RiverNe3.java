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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.tree.building.Tree;
import com.wordpress.growworkinghard.riverNe3.tree.building.binaryTree.RiverBinaryTree;
import com.wordpress.growworkinghard.riverNe3.tree.building.decorator.Hydrometers;

/**
 * @mainpage On-line Documentation
 *
 * @image html digitalBasin.jpg "The digital basin"
 *
 * @section Description Description
 *
 * This component (which is not part of OMS @cite david2013:software yet) is an
 * abstract schematization of a river network through a tree data structure, in
 * order to parallel process independent HRUs. The structure is flexible and
 * extendable, so each developer can implement her/his own tree, with
 * connections and decorators. At the moment, just the <b>binary tree</b> is
 * implemented.
 *
 * @subsection Implementation Implementation
 *
 * The structure was thought as more flexible and extendable as possible. To
 * reach this goal, several patterns were implemented and the framework is
 * completely decoupled from the type of tree and connections. The three main patterns are:
 * <ul>
 * <li>Composite Pattern;</li>
 * <li>Decorator Pattern;</li>
 * <li>Factory Pattern.</li>
 * </ul>
 *
 * @subsubsection Composite Composite Pattern
 *
 * The list of nodes of the tree is represented by a <tt>ConcurrentHashMap</tt>
 * of composite#key#Key and composite#Component to make it parsable by multi-threadings. No
 * nested structure has been developed, because it is hard to make netsted calls
 * work in parallel. Thus, the <strong>composite pattern</strong>
 * is similar to a strategy pattern, where each type of node (the already
 * implemented ones are composite#GhostNode, composite#Leaf, composite#LocalNode
 * and composite#Node but you can easily add a new one) extends the abstract
 * class composite#Component.
 *
 * However, each node has its own
 * <strong>TreeTraverser</strong> as in the composite pattern theory. At the
 * moment the available traversers are:
 * <ul>
 * <li>composite#Component#preOrderTraversal;</li>
 * <li>composite#Component#postOrderTraversal.</li>
 * </ul>
 * To get the list of nodes in the desidered order you have to
 * <ol>
 * <li>initialize a new traverser (e.g. traverser#RiverBinaryTreeTraverser) with
 * a tree <tt>ConcurrentHashMap</tt> structure;</li>
 * <li>set the traverser on the desired composite#Component node with the traverser object just
 * allocated;</li>
 * <li>call the getter which returns an <code>immutable</code> list of
 * composite#Component, i.e. the subtree in the desired order starting from the pointed node.</li>
 * </ol>
 *
 * @code
 * // step 1: traverser initialization
 * TreeTraverser traverser = new RiverBinaryTreeTraverser(tree);
 *
 * // step 2: setting of the traverser in the desired node
 * node.setTraverser(traverser);
 *
 * // step 3: subtree retrieving
 * List<Component> immutablePreOrderList = node.preOrderTraversal();
 * List<Component> immutablePostOrderList = node.postOrderTraversal();
 * @endcode
 *
 * The connections between nodes is represented by the
 * composite#key#Connections
 * object which is the abstract class of a <strong>strategy pattern</strong> in
 * order to allow each user to develop the type of connection required by the
 * tree. For the <strong>Binary Tree</strong>, the
 * composite#key#BinaryConnections was implemented.
 *
 * Each node has its own <strong>ID</strong>, which is stored in the
 * composite#key#Key object.
 *
 * @subsubsection Decorator Decorator Pattern
 *
 * The <strong>decorator pattern</strong> was implemented to allow each
 * developer to write her/his own plain tree (e.g.
 * treeBuilding#binaryTree#RiverBinaryTree useful to abstract a simple river
 * network (@cite wang2011:common), implementing the interface
 * treeBuilding#Tree.
 *
 * Once you have your own plain tree, you can decorate it writing a
 * <strong>decorator</strong> (e.g. treeBuilding#decorator#Hydrometers),
 * extending the treeBuilding#decorator#BinaryTreeDecorator abstract class.
 *
 * To use a decorator after having built your tree data structure, you have to
 * follow these simple steps:
 * <ol>
 * <li>allocate a new object treeBuilding#Tree of the desired tree structure (e.g. treeBuilding#RiverBinaryTree) from the plain network;</li>
 * <li>build the tree from the plain network calling the method
 * treeBuilding#Tree#buildTree();</li>
 * <li>allocate the same object but this time of the desired decorator type
 * (e.g. treeBuilding#decorator#Hydrometers), with the tree structure and the
 * input data as minimum required by the constructor of the decorator;</li>
 * <li>then call treeBuilding#Tree#buildTree() again, in order to add the
 * decoration to the plain tree.</li>
 * </ol>
 *
 * Repeating the last two steps, you can add all the decorators you need. In the
 * following example, the treeBuilding#decorator#Hydrometers decorator is add to
 * a treeBuilding#RiverBinaryTree plain network.
 *
 * @code
 * // step 1: plain tree allocation
 * Tree tb = new RiverBinaryTree(netInputData, availableThreads);
 *
 * // step 2: plain tree building
 * tb.buildTree();
 *
 * // step 3: decorator allocation
 * tb = new Hydrometers(tb, hydrometersInputData, radiusTolerance);
 *
 * // step 4: adding the decoration to the plain tree
 * tb.buildTree();
 * @endcode
 *
 * @subsubsection Factory Simple Factory - Factory Method Pattern
 *
 * <strong>Factory patterns</strong> were implemented without having to specify
 * the exact class of the object that will be created.
 *
 * @section AddTree How add a tree
 *
 * To add a new tree at this flexible structure, you have to
 * <code>implement</code> the treeBuilding#Tree abstract class. The most
 * important things, where pay attention, are the only two <code>public</code>
 * methods:
 * <ul>
 * <li>treeBuilding#Tree#buildTree(), which must run the building of the tree;</li>
 * <li>treeBuilding#Tree#computeNodes(), which must return the structure of the
 * tree.</li>
 * </ul>
 * Implementing the previous methods as <strong>strongly suggested</strong>
 * allows to implement corrently part of the decorator pattern.
 *
 * @section AddDecorator How add a decorator
 *
 * To add a decorator at this flexible structure, in order to enrich a plain
 * tree structure, you have to <code>implements</code> the appropriate tree
 * decorator abstract class (e.g. treeBuilding#decorator#BinaryTreeDecorator in
 * case of binary tree).
 *
 * The trick is that the decorator object (e.g. the plain class
 * treeBuilding#decorator#Template) <code><strong>has-a</strong></code> tree object
 * inside. In this way, during the construction of the decorator, the plain tree
 * in input is copied in the internal tree object. The decoration algorithm, in
 * this sense, works wrapping the plain tree structure. As reported in the
 * previous section, treeBuilding#Tree#buildTree() is the method that run the
 * wrapping, treeBuilding#Tree#computeNodes() returns the wrapped structure.
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class RiverNe3 {
 
    static Tree tb;
    static HashMap<Key, Component> binaryTree;

    private void readInputData(final int availableProcessors, final ExecutorService executor)
        throws InterruptedException
    {

        TestReader reader = new TestReader();

        reader.testReadInputData();

        tb = new RiverBinaryTree(reader.getReadData(0), availableProcessors, executor);
        tb = new Hydrometers(tb, reader.getReadData(1), 500.0);

    }

    @Test
    public void TestRun() throws InterruptedException {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);

        readInputData(availableProcessors, executor);

        binaryTree = tb.computeNodes();

        RunSimulations sim = new RunSimulations(binaryTree, executor, availableProcessors);
        sim.run();

        executor.shutdown();

        assertEquals(0,0);
    }

}
