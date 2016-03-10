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
package com.wordpress.growworkinghard.riverNe3.composite.key;

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.Immutable;

/**
 * @brief Connection class for <strong>Binary Tree</strong>
 *
 * @description This class describes the connections of each node of a
 *              <em>Binary Tree</em>. It allows <tt>null</tt> value for both
 *              left child and right child. For this reason, the
 *              <code>validateState()</code> method checks only the <tt>ID</tt>
 *              and the <tt>PARENT</tt> keys. The
 *              <code>validateInvariant()</code> method checks the connections
 *              thinking about the possibility that children might be null as
 *              well.
 *              <p>
 *              This class is <em>ThreadSafe</em> because it is
 *              <strong>Immutable</strong> as defined in @cite goetz2006:java
 *              </p>
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@Immutable
public final class BinaryConnections extends Connections {

    private final Key ID; //!< the ID of the node
    private final Key PARENT; //!< the key of the parent of the node
    private final Key LCHILD; //!< the key of the left child of the node
    private final Key RCHILD; //!< the key of the right child of the node

    /**
     * @brief Constructor
     *
     * @description Initializes a newly created <tt>BinaryConnections</tt>
     *              object so that it represents the same connections as the
     *              argument: in other words, the newly created binary
     *              connections is a copy of the argument binary connections.
     *              Unless an explicit copy of the <tt>original</tt> is needed,
     *              use of this constructor is unnecessary since
     *              <tt>BinaryConnections</tt> are immutable.
     *
     * @param[in] connection The original binary connection
     */
    public BinaryConnections(final BinaryConnections connection) {

        this.ID = connection.getID();
        this.PARENT = connection.getPARENT();
        this.LCHILD = connection.getLCHILD();
        this.RCHILD = connection.getRCHILD();

        validateStates(); // precondition
        validateInvariant(); // invariant

    }

    /**
     * @brief Constructor
     *
     * @description Having just the <tt>ID</tt> of the node as input argument,
     *              this constructor must be used <strong>ONLY</strong> if the
     *              user is 100% sure that the underlying node has both the
     *              children not null. The latter are computed in this way:
     *              <ul>
     *              <li>\f$LCHILD = NODE * 2\f$;</li>
     *              <li>\f$RCHILD = LCHILD + 1\f$.</li>
     *              </ul>
     *              The PARENT key is computed as \f$PARENT = NODE / 2\f$. No
     *              invariant validation is required at the end of this object
     *              construction
     *
     * @param[in] ID The <tt>Key</tt> of the node
     */
    public BinaryConnections(final Key ID) {

        validateKey(ID); // precondition

        this.ID = ID;
        this.PARENT = new Key(Math.floor(ID.getDouble() / 2));
        this.LCHILD = new Key(ID.getDouble() * 2);
        this.RCHILD = new Key(LCHILD.getDouble() + 1);

    }

    /**
     * @brief Constructor
     *
     * @description This is the most complete constructor, which takes in input
     *              ID, LCHILD and RCHILD. Only the PARENT key is computed. The
     *              invariant is checked at the end of the construction.
     *
     * @param[in] ID The key of the node
     * @param[in] LCHILD The key of the left child of the node
     * @param[in] RCHILD The key of the right child of the node
     */
    public BinaryConnections(final Key ID, final Key LCHILD, final Key RCHILD) {

        this.ID = ID;
        this.PARENT = new Key(Math.floor(ID.getDouble() / 2));
        this.LCHILD = LCHILD;
        this.RCHILD = RCHILD;

        validateStates(); // precondition
        validateInvariant(); // invariant

    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#getID()
     */
    public Key getID() {
        return ID;
    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#getPARENT()
     */
    public Key getPARENT() {
        return PARENT;
    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#getNumberNonNullChildren()
     */
    public int getNumberNonNullChildren() {
        if (LCHILD != null && RCHILD != null) return 2;
        else if (LCHILD == null && RCHILD == null) return 0;
        else return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#getChildren()
     */
    public List<Key> getChildren() {
        List<Key> tmpList = new ArrayList<Key>();

        if (LCHILD != null) tmpList.add(LCHILD);
        if (RCHILD != null) tmpList.add(RCHILD);

        return tmpList;
    }

    /**
     * @return The key of the left child of the node
     */
    @Override
    public Key getLCHILD() {
        return LCHILD;
    }

    /**
     * @return The key of the right child of the node
     */
    @Override
    public Key getRCHILD() {
        return RCHILD;
    }

    /**
     * @return The complete description of the object with all its states
     */
    @Override
    public String toString() {

        String print = " ID = " + ID.getString();
        print += " Parent Key = " + PARENT.getString();

        if (LCHILD != null) print += " Left Child = " + LCHILD.getString();
        else print += " NO Left Child -";

        if (RCHILD != null) print += " Right Child = " + RCHILD.getString();
        else print += " NO Right Child -";

        return print;
    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#validateStates()
     */
    protected void validateStates() {
        validateKey(ID);
        validateKey(PARENT);
        // validateKey(LCHILD); left child might be null
        // validateKey(RCHILD); right child might be null
    }

    /**
     * @brief Validate the <strong>invariant</strong> of the node
     *
     * @exception IllegalArgumentException
     *                the exception is thrown in three cases
     *                <ul>
     *                <li>the parent key is not the half of the key node;</li>
     *                <li>the left child key is not the twice of the key node;
     *                </li>
     *                <li>the right child key is not the left child key + 1.
     *                </li>
     *                </ul>
     */
    protected synchronized void validateInvariant() {

        if (PARENT.getDouble() != Math.floor(ID.getDouble() / 2)) {
            String message = "Parent key " + PARENT.getString();
            message += " is not the half of the key " + ID.getString();
            throw new IllegalArgumentException(message);
        }

        if (LCHILD != null && // validate left child only if it exists
            ID.getDouble() * 2 != LCHILD.getDouble()) {
            String message = "Left child key " + LCHILD.getString();
            message += " is not the twice of the key " + ID.getString();
            throw new IllegalArgumentException(message);
        }

        if (RCHILD != null && // validate right child only if it exists
            (LCHILD.getDouble() + 1) != RCHILD.getDouble()) {
            String message = "Righ child key " + RCHILD.getString();
            message += " is not the the left child key " + LCHILD.getString();
            message += " + 1";
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see Connections#validateKey(final Key)
     */
    protected void validateKey(final Key key) {
        if (key == null)
            throw new NullPointerException("Key object cannot be null, but the object can be initialized with a null value.");
    }

}
