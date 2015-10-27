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

import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

/**
 * @brief class Component
 *
 * @description This is the abstract class of the <strong>Composite
 *              Pattern</strong>, which has been used to realize the structure
 *              for the binary tree
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Component {

    abstract public void put();

    abstract public void delete();

    abstract public void setNewKey(final Key key);

    abstract public Key getKey();

    abstract public Key getLeftChildKey();

    abstract public Key getRightChildKey();

    abstract public Key getParentKey();

    /**
     * @brief Setter method to set the layer of the node
     *
     * @param[in] layer The layer of the node in the tree
     */
    abstract public void setLayer(final int layer);

    /**
     * @brief Getter method to get the layer of the node
     *
     * @return The layer of the node in the tree
     */
    abstract public Integer getLayer();

    abstract protected void validateState();

    protected void validateKey(final Key key) {

        if (key == null || key.getString() == null)
            throw new NullPointerException("Component keys cannot be null");
    }

    protected void validateLayer(final int layer) {

        if (layer < 0)
            throw new NullPointerException("Layer cannot be null or less then zero");

    }

    protected Key computeParentKey(final Key key) {
        return new Key(Math.floor(key.getDouble() / 2));
    }

}
