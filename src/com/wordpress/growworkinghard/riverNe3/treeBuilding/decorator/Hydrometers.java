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
import com.wordpress.growworkinghard.riverNe3.composite.key.BinaryConnections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.traverser.RiverBinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.BinaryTree;

public class Hydrometers extends BinaryTreeDecorator {

    private List<Geometry> data;
    private HashMap<Key, Component> tree;
    private BinaryTree binaryTree;
    private Double tolerance;

    public Hydrometers(final BinaryTree binaryTree, final List<Geometry> data, final double tolerance) {

        getInstance(binaryTree, data, tolerance);

    }

    public synchronized void buildTree() {

        Geometry point = null;
        Component root = null;

        while(!data.isEmpty()) {
            point = retrievePoint();
            root = retrieveRootNode(point);
            if (root != null) updateBinaryTree(point, root);
        }

    }

    public synchronized HashMap<Key, Component> computeNodes() {

        return deepCopy(tree);

    }

    private void getInstance(final BinaryTree binaryTree, final List<Geometry> data, final double tolerance) {

        if (statesAreNull()) {
            synchronized(this) {
                if (statesAreNull()) {
                    this.data = new ArrayList<Geometry>(data);
                    this.tree = new HashMap<Key, Component>();
                    this.binaryTree = binaryTree;
                    this.tolerance = tolerance;

                    validateState();

                    tree.putAll(binaryTree.computeNodes());
                }
            }
        }

    }

    private boolean statesAreNull() {

        if (this.data == null &&
            this.tree == null &&
            this.binaryTree == null &&
            this.tolerance == null) return true;

        return false;

    }

    private Geometry retrievePoint() {
        return data.remove(0);
    }

    private Component retrieveRootNode(final Geometry point) {
        Key next;
        Component tmpComp;

        Iterator<Key> i = tree.keySet().iterator();
        while(i.hasNext()) {
            next = i.next();
            tmpComp = tree.get(next);
            if (pointIsClosure(point, tmpComp)) {
                return tmpComp;
            }
        }

        return null;
    }

    private void updateBinaryTree(final Geometry point, final Component root) {
        HashMap<Key, Component> tmpTree = new HashMap<Key, Component>();
        root.setTraverser(new RiverBinaryTreeTraverser(tree));
        List<Component> tmpList = root.preOrderTraversal();
        List<Component> list = new ArrayList<Component>();
        list.addAll(tmpList);
        if (!list.remove(root))
            throw new NullPointerException("Root not present in list");

        substituteRoot(point, root, tmpTree);
        if (point.getKey() != root.getConnections().getID()) {
            processList(list, tmpTree);
            updateTree(tmpTree);
        }

    }

    private void substituteRoot(final Geometry point, final Component root, HashMap<Key, Component> tmpTree) {

        final int pointLayer = root.getLayer();
        final Coordinate2D coor = root.getEndPoint(); // startPoint and endPoint are equal in Ghost node

        if (root.getClass() == GhostNode.class) {
            final Connections conn = root.getConnections();
            tree.replace(root.getConnections().getID(), new LocalNode(conn, pointLayer, coor));
        } else {
            Key oldRootKey = root.getConnections().getID();
            Key newRootKey = new Key(oldRootKey.getDouble() * 2);

            Connections pointConn = newConnection(oldRootKey, newRootKey, null);
            tree.replace(oldRootKey, new LocalNode(pointConn, pointLayer, coor));

            root.setNewConnections(newConnection(root, newRootKey));
            root.setLayer(root.getLayer() + 1);

            tmpTree.put(oldRootKey, root);
        }

    }

    private void processList(List<Component> list, HashMap<Key, Component> tmpTree) {

        Iterator<Component> it = list.iterator();
        while(it.hasNext()) {
            Component tmp = it.next();
            Key oldTmpKey = tmp.getConnections().getID();
            if(!tree.remove(oldTmpKey, tmp))
                throw new NullPointerException("object not deleted from the tree");

            Component tmpParent = tmpTree.get(tmp.getConnections().getPARENT());
            if (tmpParent != null) {
                Key newKey;

                if (tmp.getConnections().getID().isEven())
                    newKey = tmpParent.getConnections().getLCHILD();
                else
                    newKey = tmpParent.getConnections().getRCHILD();

                tmp.setNewConnections(newConnection(tmp, newKey));
                tmp.setLayer(tmp.getLayer() + 1);
                tmpTree.put(oldTmpKey, tmp);
            }
        }

    }

    private Connections newConnection(final Key id, final Key lChild, final Key rChild) {
        return new BinaryConnections(id, lChild, rChild);
    }

    private Connections newConnection(final Component node, final Key newID) {
        Key lChild = null;
        Key rChild = null;

        if (node.getConnections().getLCHILD() != null)
            lChild = new Key(newID.getDouble() * 2);
        if (node.getConnections().getRCHILD() != null)
            rChild = new Key(newID.getDouble() * 2 + 1);

        return new BinaryConnections(newID, lChild, rChild);
    }

    private void updateTree(final HashMap<Key, Component> tmpTree) {

        Key next;

        Iterator<Key> i = tmpTree.keySet().iterator();
        while(i.hasNext()) {
            next = i.next();
            Component tmp = tmpTree.get(next);
            tree.putIfAbsent(tmp.getConnections().getID(), tmp);
            // add control if put is fine
        }

    }

    private boolean pointIsClosure(final Geometry point, final Component tmpComp) {
        return (computeDistance(point, tmpComp) < tolerance) ? true : false;
    }

    private double computeDistance(final Geometry point, final Component tmpComp) {
        double pointX = point.getPoint().x;
        double pointY = point.getPoint().y;
        double tmpCompX = tmpComp.getEndPoint().x;
        double tmpCompY = tmpComp.getEndPoint().y;

        return Math.sqrt(Math.pow(pointX - tmpCompX, 2) + Math.pow(pointY- tmpCompY, 2));

    }

    protected void validateState() {

        validateBinaryTree(binaryTree);
        validateInputData(data);
        validateTolerance(tolerance);

    }

    private void validateInputData(final List<Geometry> data) {
        if (data == null || data.size() == 0)
            throw new NullPointerException("List of input data cannot be null or empty");
    }

    private void validateTolerance(final double tolerance) {
        if (tolerance < 0)
            throw new NullPointerException("Tolerance must be positive");
    }

}
