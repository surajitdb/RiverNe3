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
package com.wordpress.growworkinghard.riverNe3.dataReader;

import java.io.IOException;
import java.util.HashMap;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

/**
 * @brief Interface for data reading classes
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public interface DataReading {

    /**
     * @brief Processing of the input file
     *
     * @description This is the unique class that a developer is obliged to
     * implement in order to make his reader working with the multithreading
     * reader 
     *
     * @return The <tt>HashMap</tt> of the input data to build a tree
     * @exception IOException if it is not possible to open the input file
     */
    public HashMap<Integer, Geometry> fileProcessing() throws IOException;

}
