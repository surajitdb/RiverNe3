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

import com.wordpress.growworkinghard.riverNe3.composite.*;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
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

    private volatile static ConcurrentHashMap<Key, Component> binaryTree;
    private volatile static ConcurrentHashMap<Integer, Geometry> data;

    /**
     * @brief Default Constructor
     */
    public TreeBuilding() {}

    // TODO: do I have to sychronize the get??
    public HashMap<Key, Component> get() {

        validateOutputData(); //!< post-condition
        return new HashMap<Key, Component>(binaryTree);

    }

    /**
     * @brief Getter method which returns the tree structure
     *
     * @param[in] list
     *            The <code>List</code> of <tt>Geometry</tt> objects
     * @return The tree structure in a <code>ConcurrentHashMap</code>
     */
    public void buildTree(final HashMap<Integer, Geometry> inputData, final int threadsNumber) {

        getInstance(inputData, threadsNumber);
        while(!data.isEmpty()) findRoot(); // find each root of the sub-tree

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
    private void getInstance(final HashMap<Integer, Geometry> inputData, final int threadsNumber) {

        if (binaryTree == null || data == null) {
            synchronized (this) {
                if (binaryTree == null || data == null) {
                    validateInputData(inputData); //!< pre-condition

                    // the following parameters are necessary in order to
                    // allocate only the useful memory. If a loadFactor and
                    // above all the concurrencyLevel are not specified, the
                    // default constructor of the ConcurrentHashMap allocates
                    // many objects in order to ensure the concurrent access of
                    // its data structure by different threads. The default
                    // concurrencyLevel is 16.
                    //
                    // To reduce the amount of memory allocated, this class
                    // return a simple HashMap and not the Concurrent version.
                    // Based on the user necessities, it has to convert it in
                    // concurrent if it is going to use it on a multi-threading
                    // code
                    int size = inputData.size();
                    float loadFactor = 0.9f; // dense packaging which will optimize memory use
                    int concurrencyLevel = threadsNumber;
                    binaryTree = new ConcurrentHashMap<Key, Component>(size, loadFactor, concurrencyLevel);
                    data = new ConcurrentHashMap<Integer, Geometry>(size, loadFactor, concurrencyLevel);
                    data.putAll(inputData);
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
    private void findRoot() {

        Geometry tmpGeom = null;
        Integer next = null;
        boolean rootRemoved = false;

        // OLDER WAY TO IMPLEMENT THE ITERATOR
        //
        // The following loop is surely not thread safe in the sense that
        // threads starting the loop after the first, are not iterating the most
        // up-to-date map. I was thinking that each thread has
        // just to find a root node of a sub-tree. Once processes the root, the
        // thread exits the loop and starts looking again for another root.
        //
        // The double check (if tmpGeom != null and if rootRemoved) should have
        // been enough to avoid a double processing of the same root.
        // Maybe there is still a window of vulnerability due the fact the two
        // thread may delete the same object, but this should avoided by the
        // @ThreadSafe class ConcurrentHashMap
        //
        // Iterator<Integer> i = data.keySet().iterator();
        // while (i.hasNext()){

        //     next = i.next();
        //     tmpGeom = data.get(next);

        //     //!< if the <code>tmpGeom</code> is root a new <tt>sub-tree</tt> has
        //     //just been identified
        //     if (tmpGeom != null && tmpGeom.isRoot()) {

        //         rootRemoved = data.remove(next, tmpGeom); //!< root node removed from the list
        //         if (rootRemoved && !data.containsKey(next)) { // enter only if a thread has removed the
        //                            // root under processing otherwise the root
        //                            // has been removed by a previous thread, so
        //                            // this thread has to search another root
        //             int emptyKey = next;
        //             Component newNode = computeNewNode(tmpGeom, emptyKey);
        //             binaryTree.putIfAbsent(tmpGeom.getKey(), newNode);

        //         }
        //         break;

        //     }

        // }

        // NEW WAY TO IMPLEMENT THE ITERATOR
        //
        // As written in the Java 8 Documentation for the ConcurrentHashMap
        // class, iterators are designed to be used by only one thread at a
        // time. So for ensuring a good concurrency, a good policy may be
        // synchronize the iteration until a root node of a sub-tree has been
        // found and deleted from the ConcurrentHashMap. Then release the lock
        // (so another thread can look for a different root of a sub-tree) and
        // process the root just found.
        //
        // The if rootRemoved and !data.containsKey(next) are a further double
        // check to ensure to process the right node. No synchronization is
        // required for that check because both the variable are local variable
        // stored in the stack memory of each thread.
        synchronized(this) {
            Iterator<Integer> i = data.keySet().iterator();
            while (i.hasNext()){

                next = i.next();
                tmpGeom = data.get(next);

                //!< if the <code>tmpGeom</code> is root a new <tt>sub-tree</tt> has
                //just been identified
                if (tmpGeom != null && tmpGeom.isRoot()) {

                    rootRemoved = data.remove(next, tmpGeom); //!< root node removed from the list
                    break;

                }

            }

        }

        if (rootRemoved && !data.containsKey(next)) { // enter only if a thread has removed the

            int emptyKey = next; //!< node removed, so its key is now not connected with another node
            Component newNode = computeNewNode(tmpGeom, emptyKey);
            binaryTree.putIfAbsent(tmpGeom.getKey(), newNode);

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
    private Component computeNewNode(Geometry root, int emptyKey) {

        boolean ghostNode = false;
        int leftIndex = -1;
        int rightIndex = -1;
        Geometry leftChild = null;
        Geometry rightChild = null;

        Iterator<Integer> i = data.keySet().iterator();
        while (i.hasNext()) {

            Integer next;
            Geometry tmpChild = null;

            next = i.next();
            tmpChild = data.get(next);

            if (tmpChild != null && tmpChildConnectedToRoot(tmpChild, root)) {

                if (leftChild == null) { // if no left child yet, assign it first
                    leftChild = tmpChild;
                    boolean isLeft = true;
                    setNewRoot(leftChild, root, isLeft);
                    leftIndex = next;
                } else if (rightChild == null) { // if no right child, then assign it
                    rightChild = tmpChild;
                    boolean isLeft = false;
                    setNewRoot(rightChild, root, isLeft);
                    rightIndex = next;
                } else {
                    ghostNode = true; //if more than two children are identified ghost + exit
                    break;
                }

            }

        }

        if (ghostNode) {

            ricomputeRightChild(rightChild); //!< right child becomes ghost node
            data.replace(leftIndex, leftChild); //!< left child is updated in the list
            data.put(emptyKey, rightChild); //!< ghost node is added to the list
            return returnNode(root, leftChild, rightChild);

        } else if (leftChild != null && rightChild != null) {

            data.replace(leftIndex, leftChild); //!< lefth child is updated in the HashMap
            data.replace(rightIndex, rightChild); //!< right child is updated in the HashMap
            return returnNode(root, leftChild, rightChild);

        } else 
            return new Leaf(new Key(Math.floor(root.getKey().getDouble() / 2)), root.getLayer());

    }

    private Component returnNode(final Geometry root, final Geometry leftChild, final Geometry rightChild) {

        Key parentKey = new Key(Math.floor(root.getKey().getDouble() / 2));
        int layer = root.getLayer();
        Key leftChildKey = leftChild.getKey();
        Key rightChildKey = rightChild.getKey();

        if (isGhost(root))
            return new GhostNode(parentKey , leftChildKey, rightChildKey, layer);
        else
            return new Node(parentKey, leftChildKey, rightChildKey, layer);

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
            Key key = new Key(parent.getKey().getDouble() * 2);
            child.setKey(key);
        } else { //!< processing a right child
            Key key = new Key(parent.getKey().getDouble() * 2 + 1);
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

    private void validateInputData(final HashMap<Integer, Geometry> inputData) {

        if (inputData == null)
            throw new NullPointerException("The input HashMap cannot be null");

    }

    private void validateOutputData() {

        if (binaryTree.isEmpty())
            throw new NullPointerException("The output HashMap is empty. Something was wrong during the computation");

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
        dfbp.process(filePath, colNames);
        HashMap<Integer, Geometry> test = dfbp.get();

        TreeBuilding tb = new TreeBuilding();
        tb.buildTree(test, 1);
        HashMap<Key, Component> binaryTree = tb.get();

        System.out.println(binaryTree);

    }

}
