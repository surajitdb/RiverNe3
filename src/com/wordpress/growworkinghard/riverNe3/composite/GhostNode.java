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
package com.wordpress.growworkinghard.riverNe3.composite;

import java.util.HashMap;
import java.util.List;

import org.geotools.graph.util.geom.Coordinate2D;

import com.google.common.collect.BinaryTreeTraverser;
import com.google.common.collect.FluentIterable;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Line;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * @brief class Ghost Node
 *
 * @description The main purpose of this class is the representation of
 *              <tt>ghost nodes</tt> inside the binary tree designed with the
 *              <strong>Composite Pattern</strong>. <tt>ghost node</tt> are
 *              necessary because a river network might have a intersection with
 *              more than two streams and using <strong>binary tree</strong> as
 *              schema doesn't allow that. In an intersection with 3 streams, a
 *              <tt>ghost node</tt> enables outline it with a stream and the
 *              <tt>ghost node</tt> where the other two streams flows
 *              <p>
 *              This class is <em>ThreadSafe</em> because:
 *              <ul>
 *              <li>Each state is guarded by the <strong>intrinsic lock</strong>
 *              ;</li>
 *              <li>Each method is <strong>synchronized</strong> in order to
 *              deny stale data if a two threads simultaneously call setter and
 *              getter methods;</li>
 *              <li>The <strong>invariant</strong> is ensured by the method
 *              GhostNode#setNewKey(final Key) and checked by the method
 *              Component#validateInvariant(final Key, final Key, final Key, final Key).</li>
 *              </ul>
 *              </p>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class GhostNode extends Component {

    @GuardedBy("this") private Key key; //!< key of the node
    @GuardedBy("this") private Key parentKey; //!< key of the parent
    @GuardedBy("this") private Integer layer; //!< layer in the tree in which this node is located
    @GuardedBy("this") private Key leftChildKey; //!< key of the left child
    @GuardedBy("this") private Key rightChildKey; //!< key of the right child
    @GuardedBy("this") private Coordinate2D startPoint; //!< starting point of the sub-basin
    @GuardedBy("this") private Coordinate2D endPoint; //!< ending point of the sub-basin
    @GuardedBy("this") private BinaryTreeTraverser<Component> traverser; //!< traverser object
    @GuardedBy("this") private final HashMap<Key, Boolean> readyForSim
        = new HashMap<Key, Boolean>(); //!< <code>HashMap</code> of flags for start sim

    /**
     * @brief Constructor
     *
     * @param[in] root The root of the sub-tree.
     * @param[in] leftChildKey The key of the left child
     * @param[in] rightChildKey The key of the right child
     */
    public GhostNode(final Line root, final Key leftChildKey, final Key rightChildKey) {
        getInstance(root, leftChildKey, rightChildKey);
    }

    /**
     * @brief <tt>notify</tt> method from <strong>Observer Pattern</strong>
     *
     * @description This method is used by children to notify to the parent that
     *              the computation of their simulation is finished.
     *
     * @param[in] child The key of the child whose computation is finished
     */
    @Override
    public synchronized void notify(final Key child) {
        readyForSim.replace(child, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#isReadyForSimulation()
     */
    public synchronized boolean isReadyForSimulation() {
        return (!readyForSim.values().contains(false)) ? true : false;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#runSimulation(final Component)
     */
    public synchronized void runSimulation(final Component parent) {
        if (!parent.getKey().equals(parentKey))
            throw new IllegalArgumentException("Node not connected with parent");

        try {
            String message = "Ghost Node " + key.getDouble();
            message += " ==> " + Thread.currentThread();
            message += " Computing..." + " PARENT = ";
            message += parent.getKey().getDouble();
            System.out.println(message);
            Thread.sleep(5000); // lock is hold
        } catch (InterruptedException e) {}
        parent.notify(key);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setNewKey(final Key)
     */
    public synchronized void setNewKey(final Key key) {

        validateKey(key);
        this.key = key;
        this.parentKey = computeParentKey(key);
        if (leftChildKey != null) this.leftChildKey = new Key(key.getDouble() * 2);
        if (rightChildKey != null) this.rightChildKey = new Key(key.getDouble() * 2 + 1);

        validateInvariant(key, parentKey, leftChildKey, rightChildKey);
        allocateSimulationFlags();
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getKey()
     */
    public synchronized Key getKey() {
        validateKey(key);
        return key;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getLeftChildKey()
     */
    public synchronized Key getLeftChildKey() {
        validateKey(leftChildKey);
        return leftChildKey;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getRightChildKey()
     */
    public synchronized Key getRightChildKey() {
        // validateKey(rightChildKey); this key might be null
        return rightChildKey;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getParentKey()
     */
    public synchronized Key getParentKey() {
        validateKey(parentKey);
        return parentKey;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setLayer(final int)
     */
    public synchronized void setLayer(final int layer) {
        validateLayer(layer);
        this.layer = layer;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getLayer()
     */
    public synchronized Integer getLayer() {
        validateLayer(layer);
        return new Integer(layer);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getStartPoint()
     */
    public synchronized Coordinate2D getStartPoint() {
        validateCoordinate(startPoint);
        return new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getEndPoint()
     */
    public synchronized Coordinate2D getEndPoint() {
        validateCoordinate(endPoint);
        return new Coordinate2D(endPoint.x, endPoint.y);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setTraverser(final BinaryTreeTraverser<Component>)
     */
    public synchronized void setTraverser(final BinaryTreeTraverser<Component> traverser) {
        this.traverser = traverser;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#preOrderTraversal()
     */
    public synchronized List<Component> preOrderTraversal() {
        FluentIterable<Component> iterator = traverser.preOrderTraversal(this);
        return iterator.toList();
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#postOrderTraversal()
     */
    public synchronized List<Component> postOrderTraversal() {
        FluentIterable<Component> iterator = traverser.postOrderTraversal(this);
        return iterator.toList();
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#inOrderTraversal()
     */
    public synchronized List<Component> inOrderTraversal() {
        FluentIterable<Component> iterator = traverser.inOrderTraversal(this);
        return iterator.toList();
    }

    /**
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = "Ghost";
        tmp += " - Key = " + key.getString();
        tmp += " Parent Key = " + parentKey.getString();

        if (leftChildKey != null)
            tmp += " Left Child = " + leftChildKey.getString();
        if (rightChildKey != null)
            tmp += " Right Child = " + rightChildKey.getString();

        tmp += " Layer = " + layer;

        return tmp;

    }

    /**
     * @brief Method that follows the rules of the <strong>Singleton
     * Pattern</strong> @cite freeman2004:head
     *
     * @description Double-checked locking
     *
     * @param[in] root The root of the sub-tree
     * @param[in] leftChildKey The key of the left child
     * @param[in] rightChildKey The key of the right child
     */
    private void getInstance(final Line root, final Key leftChildKey, final Key rightChildKey) {

        if (statesAreNull()) {
            synchronized(this) {
                if (statesAreNull()) {
                    this.key = root.getKey();
                    this.leftChildKey = leftChildKey;
                    this.rightChildKey = rightChildKey;
                    this.layer = new Integer(root.getLayer());
                    this.parentKey = root.getParentKey();
                    this.startPoint = new Coordinate2D(root.getStartPoint().x,
                                                       root.getStartPoint().y);
                    this.endPoint = new Coordinate2D(root.getEndPoint().x,
                                                     root.getEndPoint().y);

                    validateState();
                    allocateSimulationFlags();
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#statesAreNull()
     */
    protected boolean statesAreNull() {

        if (this.key == null &&
            this.parentKey == null &&
            this.layer == null &&
            this.leftChildKey == null &&
            this.rightChildKey == null &&
            this.startPoint == null &&
            this.endPoint == null) return true;

        return false;

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#validateState()
     */
    protected void validateState() {

        validateKey(key);
        validateLayer(layer);
        validateKey(leftChildKey);
        // validateKey(rightChildKey); this key might be null
        validateKey(parentKey);
        validateInvariant(key, parentKey, leftChildKey, rightChildKey);
        validateCoordinate(startPoint);
        validateCoordinate(endPoint);

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#allocateSimulationFlags()
     */
    protected void allocateSimulationFlags() {
        readyForSim.clear();

        if (leftChildKey != null && rightChildKey != null) {
            readyForSim.put(leftChildKey, false);
            readyForSim.put(rightChildKey, false);
        } else readyForSim.put(leftChildKey, false);

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#computeParentKey(final Key)
     */
    protected Key computeParentKey(final Key key) {
        return new Key(Math.floor(key.getDouble() / 2));
    }

}
