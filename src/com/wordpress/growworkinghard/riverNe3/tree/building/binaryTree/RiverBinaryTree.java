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
package com.wordpress.growworkinghard.riverNe3.tree.building.binaryTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.tree.building.Tree;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * @brief The building of a Binary Tree for a River Net
 *
 * @description This class parses a <tt>ConcurrentHashMap</tt> of input data,
 *              building the tree structure. The algorithm is rather simple but
 *              it allows the multithreaded parsing of the data structure.
 *              <p>
 *              It is based on the idea that each node of the tree is the root
 *              of a sub-tree. At the first loop, only the main root of the tree
 *              (the stream with number 1 in the PfafStetter numbering) has the
 *              boolean flag <code>root</code> set to <code>true</code>. Parsing
 *              the <code>data</code> structure, the first thread that find that
 *              node analizes and adds it on <code>binaryTree</code>. During the
 *              computation, the children of the root become the roots of them
 *              sub-tree and will have now the boolean flag <code>root</code>
 *              set to <code>true</code>. Parsing the <code>data</code>
 *              structure, the first threads that find the new roots (this time
 *              the new roots are 2, so two threads can work at the same time)
 *              analize them and make their children the new roots of them
 *              sub-tree (this time the new roots will be 4, so then four
 *              threads can work at the same time). The process ends when the
 *              <code>data</code> structure is empty.
 *              </p>
 *              <p>
 *              This class is <em>ThreadSafe</em> because:
 *              <ol>
 *              <li>the data structures <code>binaryTree</code> and
 *              <code>data</code>, which are concurrently accessed, are
 *              implemented as <tt>ConcurrentHashMap</tt>. Citing @cite
 *              goetz2006:java <blockquote><tt>ConcurrentHashMap</code> is a
 *              hash-based <code>Map</code> like <code>HashMap</code>, but it
 *              uses an entirely different locking strategy that offers a better
 *              concurrency and scalability. [...] it uses a finer-grained
 *              locking mechanism called <em>lock striping</em> to allow a
 *              greater degree of shared access. Arbitrarily many reading
 *              threads can access the map concurrently, readers can access the
 *              map concurrently with writers, and a limited number of writers
 *              can modify the map concurrently.</blockquote></li>
 *              <li>in order to make each and every change immediately available
 *              to the working threads, the data structures have been declared
 *              <code>volatile</code>. That ensures that updates to a variable
 *              are propagated predictably to other threads.
 *              <ul>
 *              <li><code>volatile</code> fields and operations on them are not
 *              reordered with other memory operations by the compilar and
 *              runtime;</li>
 *              <li><code>volatile</code> variables are not cached in registers
 *              or in caches where they are hidden from other processors, so a
 *              read of a volatile variable always returns the most recent write
 *              by any thread @cite goetz2006:java;</li>
 *              </ul>
 *              </li>
 *              <li>the constructor follows the principles of double-locking of
 *              the <strong>Singleton Pattern</strong>;</li>
 *              <li>atomic operations and iterators in method
 *              RiverBinaryTree#findRoot() and
 *              RiverBinaryTree#findChildren(Geometry, int) are synchronized by
 *              the <em>intrinsic lock</em>;</li>
 *              <li>in order to avoid the <code>binaryTree</code> escaping from
 *              this class, the RiverBinaryTree#computeNodes() method returns a
 *              copy of it;</li>
 *              <li>when possible, the principle of <strong>Stack
 *              Confinement</strong> has been used.</li>
 *              </ol>
 *              </p>
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class RiverBinaryTree extends Tree {

    @GuardedBy("this") private volatile static ConcurrentHashMap<Key, Component> binaryTree; //!< structure of the binary tree
    @GuardedBy("this") private volatile static ConcurrentHashMap<Integer, Geometry> data; //!< input data
    @GuardedBy("this") private static SimpleNodeFactory factory = new SimpleNodeFactory(); //!< simple factory in order to instantiate the proper type of node for each <tt>Geometry</tt> data
    @GuardedBy("this") private ExecutorService executor;
    @GuardedBy("this") private int threadsNumber;

    /**
     * @brief Constructor
     *
     * @description This constructor calls the <code>static</code> method
     *              RiverBinaryTree#getInstance(final HashMap<Integer, Geometry>, final int)
     *              in order to apply the
     *              <strong>Singleton Pattern</strong> with double-locking
     *
     * @param[in] inputData The input data after parsing a conversion in
     *            <tt>Geometry</tt> objects
     * @param[in] threadsNumber The number of threads that will possibly work
     *            concurrently on the same <tt>ConcurrentHashMap</tt>
     */
    public RiverBinaryTree(final HashMap<Integer, Geometry> inputData, final int threadsNumber, final ExecutorService executor) {
        this.executor = executor;
        this.threadsNumber = threadsNumber;
        getInstance(inputData, threadsNumber);
    }

    /**
     * {@inheritDoc}
     *
     * @see Tree#computeNodes()
     */
    @Override
    public HashMap<Key, Component> computeNodes() {
        parallelBuildTree(executor, threadsNumber);
        validateOutputData(); //!< postcondition
        return deepCopy(binaryTree);
    }

    protected void buildTree() {
        while(!data.isEmpty()) {
            findRoot();
        }
    }

    /**
     * @brief The copy of the structure of the binary tree
     *
     * @param[in] computedTree The binary tree just computed 
     * @return The copy of the binary tree
     */
    private static HashMap<Key, Component> deepCopy(final ConcurrentHashMap<Key, Component> computedTree) {
        HashMap<Key, Component> result = new HashMap<Key, Component>();
        result.putAll(computedTree);
        return result;
    }

    /**
     * @brief Allocate the state variables of the class
     *
     * @description In order to allocate only the useful memory for the
     *              <tt>ConcurrentHashMap</tt> structures,
     *              <code>loadFactor</code> and <code>concurrencyLevel</code>
     *              have to be specified. Indeed, the default constructor of the
     *              <tt>ConcurrentHashMap</tt> allocates many objects in order
     *              to ensure the concurrent access of its data structure by
     *              different threads (the default <code>concurrencyLevel</code>
     *              is 16). A <code>loadFactory</code> equals to 0.9f ensures a
     *              dense packaging which will optimize the memory use.
     *
     * @param[in] inputData The input data
     * @param[in] threadsNumber The number of threads
     */
    private static void getInstance(final HashMap<Integer, Geometry> inputData, final int threadsNumber) {

        if (statesAreNull()) {
            synchronized (RiverBinaryTree.class) {
                if (statesAreNull()) {
                    validateInputData(inputData); // precondition

                    int size = inputData.size(); // an initial size
                    float loadFactor = 0.9f; // dense packaging which will optimize memory use
                    int concurrencyLevel = threadsNumber; // the running threads

                    RiverBinaryTree.binaryTree
                        = new ConcurrentHashMap<Key, Component>(size,
                                                                loadFactor,
                                                                concurrencyLevel);
                    RiverBinaryTree.data
                        = new ConcurrentHashMap<Integer, Geometry>(size,
                                                                   loadFactor,
                                                                   concurrencyLevel);
                    RiverBinaryTree.data.putAll(inputData);
                }
            }
        }

    }

    /**
     * @brief Check if the states variables are null
     *
     * @retval TRUE if both states are null
     * @retval FALSE otherwise
     */
    private static boolean statesAreNull() {
        return
            (RiverBinaryTree.binaryTree == null &&
             RiverBinaryTree.data == null) ? true : false;
    }

    /**
     * @brief Find the first root node in the <code>data</code> structure
     *
     * @description The algorithm to build the tree is based on the following
     *              idea: I can identify a <strong>sub-tree</strong> with a
     *              <tt>root</tt> and <tt>two children</tt> (doesn't matter if
     *              they are null or more than two in the reality). So when
     *              parsing the <code>data</code> structure I find a
     *              <tt>root</tt>, I have just found the starting point of a new
     *              <tt>sub-tree</tt>. Then I have to look for the two children:
     *              <ul>
     *              <li>if only two children are identified, the sub-tree is
     *              complete and the two children become the roots of the
     *              following sub-trees;</li>
     *              <li>if more than two children are identified, the sub-tree
     *              is completed by the real left child and a fake right child
     *              (the <tt>ghost node</tt>). The real left child and the ghost
     *              node become the roots of the following sub-tree;</li>
     *              <li>if no children are identified, the sub-tree is completed
     *              by two <code>null</code> children.
     *              </ul>
     *              The root node of the <tt>sub-tree</tt> is removed from the
     *              <code>data</code> structure at the end of each parsing loop.
     *              <p>
     *              As written in the <em>Java 8 Documentation</em> for the
     *              <tt>ConcurrentHashMap</tt> class, iterators are designed to
     *              be used by only <strong>one thread</strong> at a time. So
     *              for ensuring a good concurrency, a good policy may be
     *              synchronize the iteration until a root node of a sub-tree
     *              has been found and deleted from the
     *              <tt>ConcurrentHashMap</tt>. Then release the lock (so
     *              another thread can look for a different root of a sub-tree)
     *              and process the root just found.
     *              </p>
     */
    private void findRoot() {

        // stack confinement: this object escapes because it is going to be
        // passed to aliens constructors. However that is not a problem, because
        // this object is a reference to an object already removed from the
        // ConcurrenHashMap
        Geometry tmpGeom = null;
        // stack confinement: this object must not escape. This is ensured by
        // copying it in the emptyKey variable before passing it to findChildren
        // method
        Integer next = null;
        // stack confinement: primitive variables cannot escape
        boolean rootRemoved = false;

        synchronized(this) {
            Iterator<Integer> iterator = data.keySet().iterator();
            while (iterator.hasNext()){

                next = iterator.next();
                tmpGeom = data.get(next);

                // if the tmpGeom is root a new sub-tree has just been identified
                if (tmpGeom != null && tmpGeom.isRoot()) {
                    rootRemoved = data.remove(next, tmpGeom);
                    break;
                }

            }

        }

        // The if rootRemoved and !data.containsKey(next) are a further double
        // check to ensure to process the right node. No synchronization is
        // required for that check because both the variable are local variable
        // stored in the stack memory of each thread.
        if (rootRemoved && !data.containsKey(next)) {

            // node removed, so its key in data structure is now not connected
            // with another node. This key can be used to add a ghost node to
            // the data structure if identified
            int emptyKey = next;
            Component newNode = findChildren(tmpGeom, emptyKey);
            binaryTree.putIfAbsent(newNode.getConnections().getID(), newNode);

        }

    }

    /**
     * @brief Find the children of the root node
     *
     * @description Children of the root node are identified when coordinates of
     *              the ending point of the children are equal to the
     *              coordinates of the starting point of the root node. Once
     *              identified they become the new roots for the following
     *              loops.
     *              <p>
     *              As written in the <em>Java 8 Documentation</em> for the
     *              <tt>ConcurrentHashMap</tt> class, iterators are designed to
     *              be used by only <strong>one thread</strong> at a time. So
     *              for ensuring a good concurrency, a good policy may be
     *              synchronize the iteration until two or three children (in
     *              case of ghost node) have been found. Then it is possible to
     *              release the lock (so another thread can look for the
     *              children of the root node it is processing), processing
     *              children and ghost node, update the <code>data</code>
     *              structure and compute the new node of the net from the
     *              factory method implemented.
     *              </p>
     *              <p>
     *              The factory method implemented is the <strong>Simple
     *              Factory</strong>.
     *              </p>
     *
     * @param[in] root The root of the temporary sub-tree
     * @param[in] emptyKey The key of the root, which can be used by a ghost
     *            node, if identified
     * @return The new node of the binary tree, built on its connections
     *         (children, parent and eventually ghost node)
     */
    private Component findChildren(final Geometry root, final int emptyKey) {

        // stack confinement: primitive variables cannot escape
        boolean ghostNode = false;
        // stack confinement: primitive variables cannot escape
        int leftIndex = -1;
        // stack confinement: primitive variables cannot escape
        int rightIndex = -1;
        // stack confinement: this object must not escape.
        // This rule must be followed in the following methods as well
        Geometry leftChild = null;
        // stack confinement: this object must not escape.
        // This rule must be followed in the following methods as well
        Geometry rightChild = null;

        synchronized(this) {
            Iterator<Integer> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {

                Integer next;
                Geometry tmpChild = null;

                next = iterator.next();
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
        return factory.createNewNode(root, leftChild, rightChild);

    }

    /**
     * @brief Update the data structure with the new roots
     *
     * @description There are three cases:
     *              <ul>
     *              <li>GHOST NODE: if a ghost node has been identified, left
     *              child is normally updated, while the right child is not
     *              actually the next root of a sub-tree which is the in fact
     *              the ghost node. Thus right child is not modified; ghost node
     *              is added to the data structure with the boolean flag root
     *              set on <code>true</code> (it is actually going to be the
     *              next root of a sub-tree);</li>
     *              <li>RCHILD and LCHILD non <code>null</code>: in this case
     *              both the children are updated in the <code>data</code>
     *              structure and are going to be the next roots of the
     *              sub-tree;</li>
     *              <li>LCHILD non <code>null</code> and RCHILD
     *              <code>null</code>: LCHILD is the only child of the root,
     *              thus it is going to be the only root for the next loop.</li>
     *              </ul>
     *              <p>
     *              Replacing children in <code>data</code> shouldn't be a
     *              problem because:
     *              <ol>
     *              <li><code>data</code> is <tt>ConcurrentHashMap</tt>, so it
     *              allows concurrent access;</li>
     *              <li>it's impossible that two threads are going to replace
     *              the same child because RiverBinaryTree#findRoot() method
     *              ensures that each thread processes a different root and then
     *              different children.</li>
     *              </ol>
     *              Doesn't matter if other threads don't see immediately the
     *              updated value of a child (that can appen if they have
     *              already got the iterator in RiverBinaryTree#findRoot() or in
     *              RiverBinaryTree#findChildren(), otherwise both the
     *              <tt>ConcurrentHashMap</tt> have been declared
     *              <strong>volatile</strong>). They see stale values, but it is
     *              not important because they are simply going to process other
     *              roots node. <strong>Stale Data</strong> are not a problem
     *              because the external loop in RiverBINAryTree#buildTree()
     *              goes on until the last element of <code>data</code> has been
     *              deleted.
     *              </p>
     *              <p>
     *              For the same reason, it's almost impossible that a thread
     *              put the same ghost node with the same key in the
     *              <code>data</code> <tt>ConcurrentHashMap</tt> (this is really
     *              impossible because each thread manages its own different
     *              root node).
     *              </p>
     *
     * @param[in] ghostNode <code>true</code> if a ghost node has been
     *            identified
     * @param[in] root The actual root node
     * @param[in] leftIndex The index of <code>leftChild</code> in
     *            <code>data</code> structure
     * @param[in] rightIndex The index of <code>rightChild</code> in
     *            <code>data</code> structure
     * @param[in] emptyKey The key of the root node in <code>data</code>
     *            structure
     * @param[in] leftChild The left child
     * @param[in] rightChild The right child
     */
    private void updateData(final boolean ghostNode, final Geometry root, final int leftIndex, final int rightIndex, final int emptyKey, final Geometry leftChild, final Geometry rightChild) {

        if (ghostNode) { // CASE 1: ghost node identified

            // right child becomes ghost node (starting point = ending point)
            ricomputeRightChild(rightChild);
            // ghost node is added to the data structure (data structure is
            // going to have one more node with root flag set on true)
            // the official right child is not replaced by ghost node in data
            // and the boolean flag root is still set on false
            data.put(emptyKey, rightChild);
            data.replace(leftIndex, leftChild); // left child updated

        } else if (leftChild != null &&
                   rightChild != null) { // CASE 2: both children are present

            data.replace(leftIndex, leftChild); // lefth child updated
            data.replace(rightIndex, rightChild); // right child updated

        } else if (leftChild != null &&
                   rightChild == null) { // CASE 3: only child
            data.replace(leftIndex, leftChild);
        }

    }

    /**
     * @brief Verify if the temporary child is connected to the root
     *
     * @param[in] tmpChild The temporary <tt>Geometry</tt> object analyzed
     * @param[in] root The root of the sub-tree
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

        return (x_tmpChild == x_root && y_tmpChild == y_root) ? true : false;

    }

    /**
     * @brief Set the root for the following subtrees
     *
     * @description Set the flag <code>root</code> on <code>true</code> and at
     *              the mean time set the <code>parentKey</code>, the
     *              <code>layer</code> and the <code>leftChildKey</code> or the
     *              <code>rightChildKey</code> on the basis of the type of
     *              child.
     *
     * @param[out] child The child which is going to become the new root
     * @param[in] parent The actual root
     * @param[in] leftChild In order to give a proper key to the child, knowing
     *            if the child is a left or a right one is required
     */
    private void setNewRoot(final Geometry child, final Geometry parent, final boolean leftChild) {

        child.setRoot(true);
        child.setParentKey(parent.getKey());
        child.setLayer(parent.getLayer()+1);

        if (leftChild) { // processing a left child
            Key key = new Key(parent.getKey().getDouble() * 2);
            child.setKey(key);
        } else { // processing a right child
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
     *              of the <code>data</code> structure, the coordinates of the
     *              starting point are set equal to the coordinate of the ending
     *              point.
     *              </p>
     *
     * @param[out] right The right child
     */
    private void ricomputeRightChild(final Geometry right) {
        right.setStartPoint(right.getEndPoint());
    }

    /**
     * @brief Validation of the input data
     *
     * @param inputData The parsed data from the input files
     */
    private static void validateInputData(final HashMap<Integer, Geometry> inputData) {
        if (inputData == null) {
            String message = "The input HashMap cannot be null.";
            throw new NullPointerException(message);
        }
    }

    /**
     * @brief Validation of the output data
     */
    private void validateOutputData() {
        if (binaryTree.isEmpty()) {
            String message = "The output HashMap is empty.";
            message += " Something was wrong during the computation";
            throw new NullPointerException(message);
        }

        if (!data.isEmpty()) { // the computation is finished only if data is empty
            String message = "Computation not finished yet.";
            message += " Check threads work.";
            throw new UnsupportedOperationException(message);
        }
    }

}
