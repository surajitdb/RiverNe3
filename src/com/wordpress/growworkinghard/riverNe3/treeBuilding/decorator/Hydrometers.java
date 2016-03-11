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
package com.wordpress.growworkinghard.riverNe3.treeBuilding.decorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.geotools.graph.util.geom.Coordinate2D;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.GhostNode;
import com.wordpress.growworkinghard.riverNe3.composite.LocalNode;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Hydrometer;
import com.wordpress.growworkinghard.riverNe3.composite.key.BinaryConnections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.traverser.RiverBinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.Tree;

/**
 * @brief Add Hydrometers node to a Binary Tree structure
 *
 * @description This class parses and input <tt>List</tt> of
 *              <code>hydrometers</code> and add them to a binary tree
 *              structure.
 *              <p>
 *              The algorithm implemented doesn't work in multithreading,
 *              because at the moment it is not able to manage the case in which
 *              two hydrometers have to be added in different position but of
 *              the same sub-tree.
 *              </p>
 *
 * @todo Think about an algorithm that allows to apply decorator in
 *       multithreading
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Hydrometers extends BinaryTreeDecorator {

    private List<Geometry> data; //!< <tt>List</tt> of hydrometers
    private HashMap<Key, Component> tree; //!< the binary tree
    private Tree binaryTree; //!< the tree on which apply the decorations
    private Double tolerance; //!< tolerance in searching the node on which apply the hydrometer

    /**
     * @brief Constructor
     *
     * @param[in] binaryTree The tree on which apply the decorations
     * @param[in] data The <tt>List</tt> of hydrometers
     * @param[in] tolerance The tolerance in searching the node on which apply
     *            the hydrometer
     */
    public Hydrometers(final Tree binaryTree, final List<Geometry> data, final double tolerance) {
        this.data = new ArrayList<Geometry>(data);
        this.tree = new HashMap<Key, Component>();
        this.binaryTree = binaryTree;
        this.tolerance = tolerance;

        validateState(); // precondition

        tree.putAll(binaryTree.computeNodes()); // wrapper
    }

    /**
     * {@inheritDoc}
     *
     * @see Tree#buildTree()
     */
    public synchronized void buildTree() {

        Geometry point = null;
        Component root = null;

        while(!data.isEmpty()) {
            point = retrievePoint();
            root = retrieveRootNode(point);
            if (root != null) updateBinaryTree(point, root);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see Tree#computeNodes()
     */
    public synchronized HashMap<Key, Component> computeNodes() {
        return deepCopy(tree);
    }

    /**
     * @brief Retrieve the first element of the <tt>List</tt> at each loop
     *
     * @return The first hydrometer of the <tt>List</tt>
     */
    private Geometry retrievePoint() {
        return data.remove(0);
    }

    /**
     * @brief Retrieve the corresponding node where the hydrometer have to be
     *        inserted
     *
     * @description An hydrometer can be only a closure point of a sub-basin.
     *              For this reason, in order to insert it in the tree, the
     *              corresponding sub-basin of which is the closure point have
     *              to be found. The corresponding sub-basin is going to be root
     *              node of the sub-tree that is going to be modified, changing
     *              the connections of each node with a new numbering.
     *
     * @param[in] point The hydrometer
     * @return The node of the corrisponding sub-basin
     */
    private Component retrieveRootNode(final Geometry point) {
        Key next;
        Component tmpComp;

        Iterator<Key> iterator = tree.keySet().iterator();
        while(iterator.hasNext()) {
            next = iterator.next();
            tmpComp = tree.get(next);
            if (pointIsClosure(point, tmpComp)) { // root found
                return tmpComp;
            }
        }

        return null;
    }

    /**
     * @brief Renumbering of the sub-tree
     *
     * @description To renumber the connections of each node of the sub-tree,
     *              the <code>traverser</code> of the root node is set and then
     *              the <tt>List</tt> of the node in the sub-tree is retrieved
     *              in <strong>Pre-Order</strong> format. That is necessary
     *              because the numbering must be modified branch by branch of
     *              the sub-tree starting from the children of the root node.
     *              <p>
     *              First of all, the root node is removed from the
     *              <tt>Pre-Ordered List</tt> because it is going to be modified
     *              in Hydrometers#substituteRoot(final Geometry, final Component, final HashMap<Key, Component>).
     *              Then, if the root
     *              node is a <tt>Ghost Node</tt> it is substitute by the
     *              hydrometer, thus no renumbering of the sub-tree is required.
     *              Otherwise start the renumbering of the connections of each
     *              node of the sub-tree.
     *              </p>
     *              <p>
     *              The renumeration is done on a temporary structure. At the
     *              end each node is replaced by the new one in the main
     *              structure.
     *              </p>
     *
     * @param[in] point The processed hydrometer
     * @param[in] root The root node of the sub-tree
     */
    private void updateBinaryTree(final Geometry hydrometerGeometry, final Component root) {
        HashMap<Key, Component> tmpTree = new HashMap<Key, Component>(); // temporary structure

        root.setTraverser(new RiverBinaryTreeTraverser(tree));
        List<Component> immutableList = root.preOrderTraversal();

        // the retrieved list is immutable, but I need to delete each node after processing
        List<Component> mutableList = new ArrayList<Component>();
        mutableList.addAll(immutableList);
        if (!mutableList.remove(root))
            throw new NullPointerException("Root not present in list");

        substituteRoot(root, tmpTree);
        if (hydrometerGeometry.getKey() != root.getConnections().getID()) { // when root is not a ghost node
            nodeConnectionRenumbering(mutableList, tmpTree);
            updateTree(tmpTree);
        }

    }

    /**
     * @brief The hydrometer becomes the new root of the sub-tree
     *
     * @description The hydrometer is defined as <tt>LocalNode</tt>. Two main
     *              cases are considered:
     *              <ul>
     *              <li>root node is a ghost node<br>
     *              in this case the hydrometer substitutes the root node of the
     *              sub-tree and this action is directly applied to the main
     *              tree</li>
     *              <li>otherwise<br>
     *              <ul>
     *              <li>the hydrometer gets ID a PARENT of the root node and the
     *              root node as ONLY CHILD. This action is directly applied to
     *              the main tree;</li>
     *              <li>the root node gets new connections numbering based on
     *              the ID of the hydrometer, which is actually the PARENT of
     *              the root node. The renumbered root node is added to the
     *              temporary structure. The latter is an <tt>HashMap</tt> of
     *              <tt>Key</tt>, <tt>Component</tt> value pair, where the
     *              <tt>Key</tt> of each node is the old ID. This is necessary
     *              in order to recompute all the connections of the other
     *              nodes.</li>
     *              </ul>
     *              </ul>
     *
     * @param[in] root The root node
     * @param[out] tmpTree The temporary structure
     */
    private void substituteRoot(final Component root, HashMap<Key, Component> tmpTree) {

        final int hydrometerLayer = root.getLayer();
        final Coordinate2D coor = root.getEndPoint(); // startPoint and endPoint are equal in Ghost node
        final Hydrometer hydrometer = new Hydrometer(coor);

        if (root.getClass() == GhostNode.class) { // CASE 1: root is ghost node
            final Connections conn = root.getConnections();
            tree.replace(root.getConnections().getID(), new LocalNode(conn, hydrometerLayer, hydrometer));
        } else { // CASE 2: otherwise
            Key oldRootKey = root.getConnections().getID();
            Key newRootKey = new Key(oldRootKey.getDouble() * 2);

            Connections hydrometerConnections = newConnection(oldRootKey, newRootKey, null);
            tree.replace(oldRootKey, new LocalNode(hydrometerConnections, hydrometerLayer, hydrometer));

            root.setNewConnections(newConnection(root, newRootKey));
            root.setLayer(root.getLayer() + 1);

            tmpTree.put(oldRootKey, root);
        }

    }

    /**
     * @brief Compute the renumbering of each node of the sub-tree
     *
     * @description Description of the algorithm (it works only in
     *              <strong>Pre-Order</strong> format):
     *              <ol>
     *              <li>get the node to process;</li>
     *              <li>get the old ID of the node;</li>
     *              <li>get the PARENT node from the temporary structure using
     *              the old numbering. The PARENT node has already the new
     *              composite#key#BinaryConnections;</li>
     *              <li>set the new connections, increase the layer by 1 and put
     *              the object in the temporary structure.</li>
     *              </ol>
     *
     * @param[in] list The list of the node to process in
     *            <strong>Pre-Order</strong> format
     * @param[out] tmpTree The temporary structure which is going to be filled
     */
    private void nodeConnectionRenumbering(final List<Component> list, HashMap<Key, Component> tmpTree) {

        Iterator<Component> iterator = list.iterator();
        while(iterator.hasNext()) {
            Component tmp = iterator.next();
            Key oldTmpKey = tmp.getConnections().getID();
            if(!tree.remove(oldTmpKey, tmp))
                throw new NullPointerException("object not deleted from the tree");

            Component tmpParent = tmpTree.get(tmp.getConnections().getPARENT());
            if (tmpParent != null) {
                Key newKey;

                if (tmp.getConnections().getID().isEven()) // it is the LCHILD
                    newKey = tmpParent.getConnections().getLCHILD();
                else // it is the RCHILD
                    newKey = tmpParent.getConnections().getRCHILD();

                tmp.setNewConnections(newConnection(tmp, newKey)); // lazy computing of new BinaryConnections
                tmp.setLayer(tmp.getLayer() + 1);
                tmpTree.put(oldTmpKey, tmp);
            }
        }

    }

    /**
     * @brief A new composite#key#BinaryConnections is computed providing each argument
     * required
     *
     * @param[in] id The new ID
     * @param[in] lChild The key of the new LCHILD
     * @param[in] rChild The key of the new RCHILD
     * @return The new composite#key#BinaryConnections
     */
    private Connections newConnection(final Key id, final Key lChild, final Key rChild) {
        return new BinaryConnections(id, lChild, rChild);
    }

    /**
     * @brief A new composite#key#BinaryConnections is computed providing only
     * the node and new ID
     *
     * @param[in] node The actual node which requires the new connection
     * @param[in] newID The new ID of the actual node
     * @return The new composite#key#BinaryConnections
     */
    private Connections newConnection(final Component node, final Key newID) {
        Key lChild = null;
        Key rChild = null;

        if (node.getConnections().getLCHILD() != null) // computed only if LCHILD is not null
            lChild = new Key(newID.getDouble() * 2);
        if (node.getConnections().getRCHILD() != null) // computed only if RCHILD is not null
            rChild = new Key(newID.getDouble() * 2 + 1);

        return new BinaryConnections(newID, lChild, rChild);
    }

    /**
     * @brief The temporary structure is copied in the main structure
     *
     * @param[in] tmpTree The temporary structure
     */
    private void updateTree(final HashMap<Key, Component> tmpTree) {

        Key next;

        Iterator<Key> iterator = tmpTree.keySet().iterator();
        while(iterator.hasNext()) {
            next = iterator.next();

            Component tmp = tmpTree.get(next);
            Component result = tree.putIfAbsent(tmp.getConnections().getID(), tmp);

            if (result != null) {
                String message = "An old Component has been replaced by the new one.";
                message += " This is not allowed.";
                throw new IllegalArgumentException(message);
            }
        }

    }

    /**
     * @brief Check if the hydrometer is close to the processed node
     *
     * @param[in] hydrometer The processed hydrometer
     * @param[in] tmpComp The processed node
     * @retval TRUE if the distance between the hydrometer and the node is less
     *         than the tolerance
     * @retval FALSE otherwise
     */
    private boolean pointIsClosure(final Geometry hydrometer, final Component tmpComp) {
        return (computeDistance(hydrometer, tmpComp) < tolerance) ? true : false;
    }

    /**
     * @brief Compute the distance between two points in the space
     *
     * @param[in] hydrometer The processed hydrometer
     * @param[in] tmpComp The processed node
     * @return The distance between the two points
     */
    private double computeDistance(final Geometry hydrometer, final Component tmpComp) {

        double hydrometerX = hydrometer.getPoint().x;
        double hydrometerY = hydrometer.getPoint().y;
        double tmpCompX = tmpComp.getEndPoint().x;
        double tmpCompY = tmpComp.getEndPoint().y;

        return Math.sqrt(Math.pow(hydrometerX - tmpCompX, 2) + Math.pow(hydrometerY- tmpCompY, 2));

    }

    /**
     * {@inheritDoc}
     *
     * @see BinaryTreeDecorator#validateState()
     */
    protected void validateState() {
        validateBinaryTree(binaryTree);
        validateInputData(data);
        validateTolerance(tolerance);
    }

    /**
     * @brief Validation of the input data
     *
     * @param[in] data The input data
     */
    private void validateInputData(final List<Geometry> data) {
        if (data == null || data.size() == 0) {
            String message = "List of input data cannot be null or empty";
            throw new NullPointerException(message);
        }
    }

    /**
     * @brief Validation of the tolerance value
     *
     * @param[in] tolerance The tolerance to apply in computing the distance
     *            between two points
     */
    private void validateTolerance(final double tolerance) {
        if (tolerance < 0)
            throw new NullPointerException("Tolerance must be positive");
    }

}
