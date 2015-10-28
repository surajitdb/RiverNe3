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

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.GhostNode;
import com.wordpress.growworkinghard.riverNe3.composite.LocalNode;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.geometry.Point;
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
            retrievePoint(point);
            retrieveRootNode(point, root);
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

    private void retrievePoint(Geometry point) {
        point = data.remove(0);
    }

    private void retrieveRootNode(final Geometry point, Component root) {
        Key next;
        Component tmpComp;

        Iterator<Key> i = tree.keySet().iterator();
        while(i.hasNext()) {
            next = i.next();
            tmpComp = tree.get(next);
            if (pointIsClosure(point, tmpComp)) {
                root = tmpComp;
                break;
            }
        }

    }

    private void updateBinaryTree(final Geometry point, final Component root) {
        HashMap<Key, Component> tmpTree = new HashMap<Key, Component>();

        root.setTraverser(new RiverBinaryTreeTraverser(tree));
        List<Component> list = root.preOrderTraversal();
        if (!list.remove(root))
            throw new NullPointerException("Root not present in list");

        substituteRoot(point, root, tmpTree);
        if (point.getKey() != root.getKey()) {
            processList(list, tmpTree);
            updateTree(tmpTree);
        }

    }

    private void substituteRoot(final Geometry point, final Component root, HashMap<Key, Component> tmpTree) {

        point.setKey(root.getKey());
        point.setLayer(root.getLayer());
        point.setParentKey(root.getParentKey());

        if (root.getClass() == GhostNode.class)
            tree.replace(point.getKey(), new LocalNode((Point) point, root.getLeftChildKey(), root.getRightChildKey()));
        else {
            root.setNewKey(new Key(point.getKey().getDouble() * 2));
            root.setLayer(point.getLayer() + 1);

            tree.replace(point.getKey(), new LocalNode((Point) point, root.getKey()));
            tmpTree.put(root.getKey(), root);
        }

    }

    private void processList(List<Component> list, HashMap<Key, Component> tmpTree) {

        Iterator<Component> it = list.iterator();
        while(it.hasNext()) {
            Component tmp = it.next();
            tree.remove(tmp.getKey(), tmp);
            Component tmpParent = tmpTree.get(tmp.getParentKey());
            Key newKey;

            if (tmpParent.getRightChildKey().getDouble() == (tmp.getKey().getDouble() * 2))
                newKey = new Key(tmpParent.getRightChildKey());
            else
                newKey = new Key(tmpParent.getLeftChildKey());

            tmp.setNewKey(newKey);
            tmp.setLayer(tmp.getLayer() + 1);
            tmpTree.put(tmp.getKey(), tmp);
        }

    }

    private void updateTree(final HashMap<Key, Component> tmpTree) {

        Key next;

        Iterator<Key> i = tmpTree.keySet().iterator();
        while(i.hasNext()) {
            next = i.next();
            tree.putIfAbsent(next, tmpTree.get(next));
            // add control if put is fine
        }

    }

    private boolean pointIsClosure(final Geometry point, final Component tmpComp) {
        return (computeDistance(point, tmpComp) < tolerance) ? true : false;
    }

    private double computeDistance(final Geometry point, final Component tmpComp) {
        final double pointX = point.getPoint().x;
        final double pointY = point.getPoint().y;

        final double tmpCompX = tmpComp.getStartPoint().x;
        final double tmpCompY = tmpComp.getStartPoint().y;

        return Math.sqrt(Math.abs(Math.pow(pointX, 2) - Math.pow(tmpCompX, 2)) + Math.abs(Math.pow(pointY, 2) - Math.pow(tmpCompY, 2)));

    }

    private HashMap<Key, Component> deepCopy(final HashMap<Key, Component> tree) {

        return tree;

    }

}
