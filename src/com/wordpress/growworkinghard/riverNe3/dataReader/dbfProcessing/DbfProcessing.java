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
package com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.dataReader.DataReading;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

import net.jcip.annotations.ThreadSafe;

/**
 * @brief abstract class DbfProcessing
 *
 * @description This class is useful as interface, in order to guide the
 *              developer who wants to write a class which parses a
 *              <code>.dbf</code> file of a <code>.shp</code>.
 *              <p>
 *              The implemented methods are synchronized because the reading of
 *              each file can be done just with one thread, but \f$n-\f$files
 *              can be read simultaneously by \f$n-\f$threads.
 *              </p>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public abstract class DbfProcessing implements DataReading {

    /**
     * {@inheritDoc}
     *
     * @see DataReading#fileProcessing()
     */
    @Override
    abstract public HashMap<Integer, Geometry> fileProcessing() throws IOException;

    /**
     * @brief Processing of each line of the <code>.dbf</code> file
     *
     * @description In this method each line of the <code>.dbf</code> is
     *              retrieved and parsed, in order to create the appropriate
     *              <tt>Geometry</tt> object (<tt>Point</tt>, <tt>Line</tt> or
     *              whatever else)
     *
     * @param[in] dbfReader The object containing the <code>.dbf</code> file
     * @param[in] columnIndices The <tt>Vector</tt> of the indices of the
     *            columns to parse, in order to find the requested informations
     *            to created the appropriate <tt>Geometry</tt> object.
     * @exceptions IOException If it is not possible to open the input file
     */
    abstract protected void bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> conIndices) throws IOException;

    /**
     * @brief Validation of the input data
     *
     * @param[in] filePath The input string of the path of the file
     * @param[in] columnNames The vector of <tt>String</tt> with the names of
     *            the columns to parse
     */
    abstract protected void validateInputData(final String filePath, final String[] columnNames);

    /**
     * @brief The processing of the header of the <code>.dbf</code> file
     *
     * @description The processing of the header file allows to get the indices
     *              of the columns from which retrieving the data to create the
     *              <tt>Geometry</tt> objects
     *
     * @param[in] dbfReader The <code>dbf</code> reader
     * @param[in] columnNames The array of strings with the name of the columns
     *            to match
     * @return A <tt>Vector</tt> of <tt>Integer</tt> with the indices of the
     *         columns from which retrieve the data
     */
    protected synchronized Vector<Integer> headerProcessing(final DbaseFileReader dbfReader, final String[] columnNames) {

        final Vector<Integer> columnIndices = new Vector<Integer>(columnNames.length);
        final DbaseFileHeader dbfHeader = dbfReader.getHeader();

        int index = 0;

        while(index < columnNames.length) // index increases only if a desidered column has been found
            for (int i = 0; i < dbfHeader.getNumFields(); i++) // parsing of all the columns
                if (columnNames[index].compareTo(dbfHeader.getFieldName(i)) == 0) {
                        columnIndices.add(i);
                        index++;
                }

        return columnIndices;

    }

    /**
     * @brief Validation of the output data
     *
     * @param[in] inputData The data read from the <code>.dbf</code> file
     */
    protected synchronized void validateOutputData(final HashMap<Integer, Geometry> inputData) {
        if (inputData.isEmpty()) {
            String message = "The output HashMap is empty.";
            message += " Something was wrong during the computation";
            throw new NullPointerException(message);
        }
    }

}
