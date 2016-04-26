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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Entity;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.simulations.Results;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import waterBudget.WaterBudget;

/**
 * @brief class Leaf
 *
 * @description The main purpose of this class is the representation of
 *              subbasins that don't have children.
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
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class Leaf extends Component {

    @GuardedBy("this") private Connections connKeys; //!< connections of the node
    @GuardedBy("this") private Integer layer; //!< layer in the tree in which this node is located
    @GuardedBy("this") private Entity entity; //!<
    @GuardedBy("this") private TreeTraverser<Component> traverser; //!< traverser object
    private WaterBudget waterBudget;
    private Results results = new Results();

    /**
     * @brief Constructor
     *
     * @param[in] connKeys The connection of the node
     * @param[in] layer The layer of the node in the tree
     * @param[in] startPoint The starting point of the stream in the sub-basin
     * @param[in] endPoint The closure point of the sub-basin
     */
    public Leaf(final Connections connKeys, final Integer layer, final Entity entity) {
        getInstance(connKeys, layer, entity);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#isReadyForSimulation()
     */
    public synchronized boolean isReadyForSimulation() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#runSimulation(final Component)
     */
    public synchronized void runSimulation(final Component parent) {

        try {
            waterBudget = new WaterBudget(entity);
            waterBudget.process();
            retrieveResults();
            printMessage();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        //  Thread.sleep(5000); // lock is hold
        //} catch (InterruptedException e) {}

        parent.notify(connKeys.getID(), results);

    }

    private void printMessage() {

        String className = this.getClass().getSimpleName();
        Double nodeID = connKeys.getID().getDouble();
        String threadName = Thread.currentThread().getName();
        Double parentID = connKeys.getPARENT().getDouble();
        HashMap<Integer, double[]> discharge = results.getResult(3); // quick

        super.simulationMessage(className, nodeID, threadName, parentID, discharge, null, null);

    }

    private void retrieveResults() {

        results.add(waterBudget.getStorage());
        results.add(waterBudget.getDischarge());
        results.add(waterBudget.getEvapotranspiration());
        results.add(waterBudget.getQuick());
        results.add(waterBudget.getR());

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setNewConnections(final Connections)
     */
    public synchronized void setNewConnections(final Connections connKeys) {
        validateConnections(connKeys); // precondition
        this.connKeys = connKeys;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getConnections()
     */
    public synchronized Connections getConnections() {
        return connKeys;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setLayer(final int)
     */
    public synchronized void setLayer(final int layer) {
        validateLayer(layer); // precondition
        this.layer = layer;
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getLayer()
     */
    public synchronized Integer getLayer() {
        return new Integer(layer);
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getStartPoint()
     */
    public synchronized Coordinate2D getStartPoint() {
        return entity.getStartPoint();
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#getEndPoint()
     */
    public synchronized Coordinate2D getEndPoint() {
        return entity.getEndPoint();
    }

    /**
     * {@inheritDoc}
     *
     * @see Component#setTraverser(final BinaryTreeTraverser<Component>)
     */
    public synchronized void setTraverser(final TreeTraverser<Component> traverser) {
        if (traverser == null) throw new NullPointerException("Traverser cannot be null."); // precondition
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
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = this.getClass().getSimpleName();
        tmp += "       ==> ";
        tmp += connKeys.toString();
        tmp += " - Layer = " + layer;

        return tmp;

    }

    /**
     * @brief Allocation of the states of the class
     *
     * @param[in] connKeys The connections of the node
     * @param[in] layer The layer of the node in the tree
     * @param[in] startPoint The starting point of the stream in the sub-basin
     * @param[in] endPoint The closure point of the sub-basin
     */
    private void getInstance(final Connections connKeys, final Integer layer, final Entity entity) {

        if (statesAreNull()) {
            synchronized(this) {
                if (statesAreNull()) {
                    this.connKeys = connKeys;
                    this.layer = new Integer(layer);
                    this.entity = entity;

                    validateState(); // precondition
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

        if (this.connKeys == null &&
            this.layer == null &&
            this.entity == null) return true;

        return false;

    }

    /**
     * {@inheritDoc}
     *
     * @see Component#validateState()
     */
    protected void validateState() {

        validateConnections(connKeys);
        validateLayer(layer);

    }

    protected void allocateSimulationFlags() {
        // nothing to implement here. Leaf has no simulation flags
    }

}
