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

import java.util.HashMap;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.dataReader.DataProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

import net.jcip.annotations.ThreadSafe;

/**
 * @brief class DbfProcessing
 *
 * @description This class parses the dbf of a shapefile and return a
 *              <code>List</code> filled with the desiderd <code>Geometry</code>
 *              objects.
 *              <p>
 *              This class is <em>ThreadSafe</em> because it has been designed
 *              following the <strong>Stack-confinement</strong> principle
 * @cite goetz2006:java
 *       </p>
 *
 * @code{.java}
 * DbfProcessing dbfProc = new DbfProcessing();
 * List<Geometry> test = dbfProc.get(filePath, geomType, colNames);
 * @endcode
 *
 * @todo Add <strong>pre-conditions</strong> and
 *        <strong>post-conditions</strong>
 *
 * @todo add documentation
 *
 * @todo verify if the class is still <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public abstract class DbfProcessing implements DataProcessing {

    abstract public HashMap<Integer, Geometry> fileProcessing();

    abstract protected void bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> conIndices);

    abstract protected void validateInputData(final String filePath, final String[] colNames);

    /**
     * @brief The processing of the header of the <code>.dbf</code> file
     *
     * @description The processing of the header file allows to get the indices
     *              of the columns from which retrieve the data useful to create
     *              the <tt>Geometry</tt> <code>objects</code>
     *
     * @param[in] dbfReader
     *            The <code>dbf</code> reader
     * @param[in] colNames
     *            The array of strings with the name of the columns to match
     * @return A <code>Vector</code> of <code>Integer</code> with the indices of
     *         the columns from which retrieve the data
     */
    protected synchronized Vector<Integer> headerProcessing(final DbaseFileReader dbfReader, final String[] colNames) {

        Vector<Integer> tmpVec = new Vector<Integer>(colNames.length);
        DbaseFileHeader dbfheader = dbfReader.getHeader();

        int index = 0;

        while(index < colNames.length)
            for (int i = 0; i < dbfheader.getNumFields(); i++)
                if (colNames[index].compareTo(dbfheader.getFieldName(i)) == 0) {
                        tmpVec.add(i);
                        index++;
                }

        return tmpVec;

    }

    protected synchronized void validateOutputData(final HashMap<Integer, Geometry> inputData) {

        if (inputData.isEmpty())
            throw new NullPointerException("The output HashMap is empty. Something was wrong during the computation");

    }

}
