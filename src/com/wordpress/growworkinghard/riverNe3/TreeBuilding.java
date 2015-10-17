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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.wordpress.growworkinghard.riverNe3.composite.*;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfLinesProcessing;
import com.wordpress.growworkinghard.riverNe3.dbfProcessing.DbfProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

/**
 * @brief Building the binary tree of the input river net
 *
 * @description This class parses a <code>List</code> of <tt>Geometry</tt>
 *              objects, finds the <strong>root node</strong> of a sub-tree,
 *              adds it to the <code>ConcurrentHashMap</code> structure after
 *              having computed its <em>left child</em> and <em>right child</em>
 *              . Then, the latter two become the new root nodes of a sub-tree
 *              and when a thread, which is parsing the <code>List</code>
 *              structure recognizes that, it executes the complete procedure.
 *              <p>
 *              This algorithm is able to recognize 3 type of <tt>Component</tt>
 *              (from the <strong>Composite Pattern</strong> implemented in the
 *              <code>composite</code> package):
 *              <ul>
 *              <li><tt>Leaf</tt>: node without children;</li>
 *              <li><tt>Node</tt>: node with children;</li>
 *              <li><tt>Ghost Node</tt>: node created when a river intersection
 *              is composed by 3 or more rivers. In a <tt>binary tree</tt>
 *              structure a node cannot have three children, so that a ghost
 *              node is necessary to gather the flow of two rivers and then
 *              releases it with the flow of the remaining river into the root
 *              node of the sub-tree. At the end it does nothing special, it is
 *              just a virtual structure required to build a binary tree.</li>
 *              </ul>
 *              </p>
 *
 * @code{.java}
 * TreeBuilding treeBuilding = new TreeBuilding();
 * ConcurrentHashMap<Integer, Component> tree = treeBuilding.get(listGeometries);
 * @endcode
 *
 * @todo make this class <em>ThreadSafe</em>
 * 
 * @todo add <strong>pre-conditions</strong> and
 *       <strong>post-conditions</strong>
 *
 * @todo design a better implementation for the method <code>computeNewNode</code>
 *
 * @todo complete the documentation
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class TreeBuilding {

    private volatile static ConcurrentHashMap<Integer, Component> binaryTree;

    /**
     * @brief Default Constructor
     */
    public TreeBuilding() {}

    /**
     * @brief Getter method which returns the tree structure
     *
     * @param[in] list
     *            The <code>List</code> of <tt>Geometry</tt> objects
     * @return The tree structure in a <code>ConcurrentHashMap</code>
     */
    public ConcurrentHashMap<Integer, Component> getTree(List<Geometry> list) {

        getInstance(list.size());
        while(!list.isEmpty()) findRoot(list); // find each root of the sub-tree

        return new ConcurrentHashMap<Integer, Component>(binaryTree);

    }

    /**
     * @brief Singleton Pattern
     *
     * @description This method has been designed following the
     *              <strong>Singleton Pattern</strong> in order to get a unique
     *              <code>ConcurrentHashMap</code> where different thread can
     *              work simultaneously.
     *
     * @param[in] size
     *            The number of <tt>Geometry</tt> objects in the list
     */
    private void getInstance(final int size) {

        if (binaryTree == null) {
            synchronized (this) {
                if (binaryTree == null) {
                    binaryTree = new ConcurrentHashMap<Integer, Component>(size);
                }            
            }
        }

    }

    /**
     * @brief Find the first root node in the <code>List</code>
     *
     * @description The algorithm to build the tree is based on the following
     *              idea: I can identify a <strong>sub-tree</strong> with a
     *              <tt>root</tt> and <tt>two children</tt> (doesn't matter if
     *              they are null or more than two in the reality). So when
     *              parsing the <code>List</code> structure I find a
     *              <tt>root</tt>, I have just found the starting point of a new
     *              <tt>sub-tree</tt>. Then I have to look for the two children:
     *              <ul>
     *              <li>if only two children are identified, the sub-tree is
     *              complete and the two children become the roots of the
     *              following sub-tree;</li>
     *              <li>if more than two children are identified, the sub-tree
     *              is completed by the real left child and a fake right child
     *              (the <tt>ghost node</tt>) which is added to the
     *              <code>List</code> structure. The real left child and the
     *              ghost node become the roots of the following sub-tree;</li>
     *              <li>if no children are identified, the sub-tree is completed
     *              by two <code>null</code> children.
     *              </ul>
     *              The root node of the <tt>sub-tree</tt> is removed from the
     *              <code>List</code> structure at the end of each parsing loop.
     *
     * @param[in] list
     *            The <code>List</code> structure with all the <tt>Geometry</tt>
     *            objects
     */
    private void findRoot(List<Geometry> list) {

        for (int i = 0; i < list.size(); i++) {

            Geometry tmpGeom = list.get(i);

           //!< if the <code>tmpGeom</code> is root a new <tt>sub-tree</tt> has
           //just been identified
           if (tmpGeom.isRoot()) { 

                list.remove(i); //!< the root node is removed from the list
                Component newNode = computeNewNode(list, tmpGeom);
                binaryTree.put(tmpGeom.getKey(), newNode);
                break;

            }

        }

    }

    /**
     * @brief The core of the algorithm
     *
     * @todo Create two more methods to encapsulate the identification of the 
     * left and right child and another one to set the new sub-roots
     *
     * @param list
     * @param root
     * @param layer
     * @return
     */
    private Component computeNewNode(List<Geometry> list, Geometry root) {

        boolean ghostNode = false;
        int leftIndex = -1;
        int rightIndex = -1;
        Geometry leftChild = null;
        Geometry rightChild = null;

        for (int i = 0; i < list.size(); i++) {

            Geometry tmpChild = list.get(i);

            if (tmpChildConnectedToRoot(tmpChild, root)) {

                if (leftChild == null) { // if no left child yet, assign it first
                    leftChild = tmpChild;
                    boolean isLeft = true;
                    setNewRoot(leftChild, root, isLeft);
                    leftIndex = i;
                } else if (rightChild == null) { // if no right child, then assign it
                    rightChild = tmpChild;
                    boolean isLeft = false;
                    setNewRoot(rightChild, root, isLeft);
                    rightIndex = i;
                } else {
                    ghostNode = true; //if more than two children are identified ghost + exit
                    break;
                }

            }

        }

        if (ghostNode) {

            ricomputeRightChild(rightChild); //!< right child becomes ghost node
            list.set(leftIndex, leftChild); //!< left child is updated in the list
            list.add(rightChild); //!< ghost node is added to the list
            if (isGhost(root)) return new GhostNode(root.getKey() / 2, leftChild.getKey(), rightChild.getKey(), root.getLayer());
            else return new Node(root.getKey() / 2, leftChild.getKey(), rightChild.getKey(), root.getLayer());

        } else if (leftChild != null && rightChild != null) {

            list.set(leftIndex, leftChild);
            list.set(rightIndex, rightChild);
            if (isGhost(root)) return new GhostNode(root.getKey() / 2, leftChild.getKey(), rightChild.getKey(), root.getLayer());
            else return new Node(root.getKey() / 2, leftChild.getKey(), rightChild.getKey(), root.getLayer());

        } else 
            return new Leaf(root.getKey() / 2, root.getLayer());

    }

    /**
     * @brief Verify if the temporary child is connected to the root
     *
     * @param[in] tmpChild
     *            The temporary <tt>Geometry</tt> object analyzed
     * @param[in] root
     *            The root of the sub-tree
     * @return If the child is connected
     * @retval TRUE The child is connected to the root
     * @retval FALSE The child is not connected to the root
     */
    private boolean tmpChildConnectedToRoot(final Geometry tmpChild, final Geometry root) {

        // Coordinates of the ending point of the temporary child
        double x_tmpChild = tmpChild.getEndPoint().x;
        double y_tmpChild = tmpChild.getEndPoint().y;

        // Coordinates of the starting point of the root
        double x_root = root.getStartPoint().x;
        double y_root = root.getStartPoint().y;

        if (x_tmpChild == x_root && y_tmpChild == y_root) return true;
        else return false;

    }

    /**
     * @brief Verify if a root node is a ghost one at the same time
     *
     * @description A ghost node has starting point equal to the ending point
     *
     * @param[in] root
     *            The root node of the sub-tree
     * @return If the root is ghost or not
     * @retval TRUE The root is ghost
     * @retval FALSE The root is not ghost
     */
    private boolean isGhost(final Geometry root) {

        double xStart = root.getStartPoint().x;
        double yStart = root.getStartPoint().y;
        double xEnd = root.getEndPoint().x;
        double yEnd = root.getEndPoint().y;

        if (xStart == xEnd && yStart == yEnd) return true;
        else return false;

    }

    /**
     * @brief Set the root for the following subtree
     *
     * @param[out] child
     *            The child which has to become the new root
     * @param[in] parent
     *            The actual current
     * @param[in] leftChild
     *            In order to give a proper key and layer to the child, knowing
     *            if the child is a left or a right one is required
     */
    private void setNewRoot(Geometry child, final Geometry parent, final boolean leftChild) {

        child.setRoot(true);
        child.setParentKey(parent.getKey());
        child.setLayer(parent.getLayer()+1);

        if (leftChild) { //!< processing a left child
            int key = parent.getKey() * 2;
            child.setKey(key);
        } else { //!< processing a right child
            int key = parent.getKey() * 2 + 1;
            child.setKey(key);
        }

    }

    /**
     * @brief Right child becomes a ghost node
     *
     * @description If at least a ghost node is required to build the binary
     *              structure, the first right child identified becomes a ghost
     *              node.
     *              <p>
     *              To easily identify the ghost node in the following parsing
     *              of the <code>List</code> structure, the coordinates of the
     *              starting point are set equal to the coordinate of the ending
     *              point.
     *              </p>
     *
     * @param[out] right
     *            The right child
     */
    private void ricomputeRightChild(final Geometry right) {
        right.setStartPoint(right.getEndPoint());
    }

    /**
     * @brief A simple test of the class
     *
     * @param args
     */
    public static void main(String[] args) {

        String filePath = "/home/francesco/vcs/git/personal/RiverNe3/data/net.dbf";
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};

        DbfProcessing dfbp = new DbfLinesProcessing();
        List<Geometry> test = dfbp.get(filePath, colNames);

        TreeBuilding tb = new TreeBuilding();
        ConcurrentHashMap<Integer, Component> binaryTree = tb.getTree(test);

        System.out.println(binaryTree);

    }

}