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

import net.jcip.annotations.Immutable;

@Immutable
public final class BinaryConnections extends Connections {

    private final Key ID;
    private final Key PARENT;
    private final Key LCHILD;
    private final Key RCHILD;

    public BinaryConnections(final BinaryConnections connection) {
        this.ID = connection.getID();
        this.PARENT = connection.getPARENT();
        this.LCHILD = connection.getLCHILD();
        this.RCHILD = connection.getRCHILD();
        validateStates();
        validateInvariant();
    }

    public BinaryConnections(final Key ID, final Key LCHILD, final Key RCHILD) {
        this.ID = ID;
        this.PARENT = new Key(Math.floor(ID.getDouble() / 2));
        this.LCHILD = LCHILD;
        this.RCHILD = RCHILD;
        validateStates();
        validateInvariant();
    }

    public BinaryConnections(final Key ID) {
        validateKey(ID);
        this.ID = ID;
        this.PARENT = new Key(Math.floor(ID.getDouble() / 2));
        this.LCHILD = new Key(ID.getDouble() * 2);
        this.RCHILD = new Key(ID.getDouble() * 2 + 1);
    }

    public Key getID() {
        return ID;
    }

    public Key getPARENT() {
        return PARENT;
    }

    @Override
    public Key getLCHILD() {
        return LCHILD;
    }

    @Override
    public Key getRCHILD() {
        return RCHILD;
    }

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

        if (LCHILD != null &&
            ID.getDouble() * 2 != LCHILD.getDouble()) {
            String message = "Left child key " + LCHILD.getString();
            message += " is not the twice of the key " + ID.getString();
            throw new IllegalArgumentException(message);
        }

        if (RCHILD != null &&
            (LCHILD.getDouble() + 1) != RCHILD.getDouble()) {
            String message = "Righ child key " + RCHILD.getString();
            message += " is not the the left child key " + LCHILD.getString();
            message += " + 1";
            throw new IllegalArgumentException(message);
        }

    }

    protected void validateKey(final Key key) {
        if (key == null)
            throw new NullPointerException("Key object cannot be null, but the object can be initialized with a null value.");
    }

}
