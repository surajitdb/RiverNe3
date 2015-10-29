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

public abstract class Connections {
    public abstract Key getID();
    public abstract Key getPARENT();

    public Key getRCHILD() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public Key getLCHILD() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    protected abstract void validateStates();
    protected abstract void validateKey(final Key key);
}
