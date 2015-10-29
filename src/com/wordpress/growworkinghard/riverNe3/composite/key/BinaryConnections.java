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
    }

    public BinaryConnections(final Key ID, final Key PARENT, final Key LCHILD, final Key RCHILD) {
        this.ID = ID;
        this.PARENT = PARENT;
        this.LCHILD = LCHILD;
        this.RCHILD = RCHILD;
        validateStates();
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
        validateKey(LCHILD);
        validateKey(RCHILD);
    }

    protected void validateKey(final Key key) {
        if (key == null)
            throw new NullPointerException("Key object cannot be null, but the object can be initialized with a null value.");
    }

}
