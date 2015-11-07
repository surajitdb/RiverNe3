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
package com.wordpress.growworkinghard.riverNe3.treeBuilding.binaryTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.Tree;

import net.jcip.annotations.GuardedBy;

public class RiverBinaryTree extends Tree {

    @GuardedBy("this") private volatile static ConcurrentHashMap<Key, Component> binaryTree;
    @GuardedBy("this") private volatile static ConcurrentHashMap<Integer, Geometry> data;
    private static SimpleNodeFactory factory = new SimpleNodeFactory();

    /**
     * @brief Default Constructor
     */
    public RiverBinaryTree(final HashMap<Integer, Geometry> inputData, final int threadsNumber) {
 
        getInstance(inputData, threadsNumber);

    }

    public synchronized HashMap<Key, Component> computeNodes() {

        validateOutputData(); //!< post-condition
        return deepCopy(binaryTree);

    }

    public void buildTree() {
        while(!data.isEmpty()) findRoot(); // find each root of the sub-tree
    }

    private static HashMap<Key, Component> deepCopy(final ConcurrentHashMap<Key, Component> computeTree) {

        HashMap<Key, Component> result = new HashMap<Key, Component>();
        result.putAll(computeTree);
        return result;

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
    private static void getInstance(final HashMap<Integer, Geometry> inputData, final int threadsNumber) {

        if (binaryTree == null || data == null) {
            synchronized (RiverBinaryTree.class) {
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

        Geometry tmpGeom = null; //!< stack confinement: this object escapes because it is going to be passed to aliens constructors. However that is not a problem, because this object is a reference to an object already removed from the ConcurrenHashMap
        Integer next = null; //!< stack confinement: this object must not escape. This is ensured by copying it in the emptyKey variable before passing it to findChildren method
        boolean rootRemoved = false; //!< stack confinement: primitive variables cannot escape

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
            Component newNode = findChildren(tmpGeom, emptyKey);
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
    private Component findChildren(Geometry root, int emptyKey) {

        boolean ghostNode = false;
        int leftIndex = -1;
        int rightIndex = -1;
        Geometry leftChild = null; //!< stack confinement: this object must not escape. This rule must be followed in the following methods as well
        Geometry rightChild = null; //!< stack confinement: this object must not escape. This rule must be followed in the following methods as well
        Component node = null;

        // iterators in ConcurrentHashMap are designed to be used by only one thread at a time
        synchronized(this) {
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
        }

        updateData(ghostNode, root, leftIndex, rightIndex, emptyKey, leftChild, rightChild);
        node = factory.createNewNode(root, leftChild, rightChild);
        return node;

    }

    private void updateData(final boolean ghostNode, final Geometry root, final int leftIndex, final int rightIndex, final int emptyKey, Geometry leftChild, Geometry rightChild) {

        if (ghostNode) {

            ricomputeRightChild(rightChild); //!< right child becomes ghost node
            // in replacing leftChild in data shouldn't be problems because:
            // 1. data is ConcurrentHashMap, so it allows concurrency access
            // 2. it's impossible that two threads are going to replace the same
            // leftChild because the @ThreadSafe findRoot method ensures that
            // each thread is processing a different root.
            //
            // Doesn't matter if the other threads don't get immediately the new
            // value of leftChild (that can appen if they are iterating on an
            // older iterator, otherwise both the ConcurrentHashMaps have been
            // declared volatile). They simply are going to process other roots
            // node. Stale data are not a problem, because the external loop go
            // on until the last element of the data ConcurrentHashMap has been
            // deleted.
            data.replace(leftIndex, leftChild); //!< left child is updated in the list
            // for the same reason, it's almost impossible that a thread put the
            // same ghost node with the same key (this is really impossible
            // because each thread manages its own different root node) in the
            // data ConcurrentHashMap
            data.put(emptyKey, rightChild); //!< ghost node is added to the list

        } else if (leftChild != null && rightChild != null) {

            data.replace(leftIndex, leftChild); //!< lefth child is updated in the HashMap
            data.replace(rightIndex, rightChild); //!< right child is updated in the HashMap

        }

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
            double tmp = parent.getKey().getDouble() * 2;
            Key key = new Key(tmp);
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

    private static void validateInputData(final HashMap<Integer, Geometry> inputData) {

        if (inputData == null)
            throw new NullPointerException("The input HashMap cannot be null");

    }

    private void validateOutputData() {

        if (binaryTree.isEmpty())
            throw new NullPointerException("The output HashMap is empty. Something was wrong during the computation");

    }

}