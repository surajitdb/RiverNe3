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
package com.wordpress.growworkinghard.riverNe3.dbfProcessing;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

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
public abstract class DbfProcessing {

    private volatile static ConcurrentHashMap<Integer, Geometry> inputData;

    public ConcurrentHashMap<Integer, Geometry> get() {
        validateOutputData(); //!< post-conditions
        return new ConcurrentHashMap<Integer, Geometry>(inputData);
    }

    /**
     * @brief Getter method which return the list of geometric features
     *
     * @description This is the only one public method. It returns a
     *              synchronizedList of objects of type Geometry.
     *
     * @param[in] filePath
     *            The complete input path of the <code>.dbf</code> file
     * @param[in] colNames
     *            An array of strings of the column names to search. It has to
     *            be structured in the following way
     *            <ol>
     *                <li><strong>Line</strong>:
     *                <ul>
     *                    <li>Column name of the Pfafstetter numbering;</li>
     *                    <li>Column name of the X coordinate of the starting
     *                        point;</li>
     *                    <li>Column name of the Y coordinate of the starting
     *                        point;</li>
     *                    <li>Column name of the X coordinate of the ending
     *                        point;</li>
     *                    <li>Column name of the Y coordinate fo the ending
     *                        point.</li>
     *                </ul>
     *            </ol>
     * @return A <code>Collections.synchronizedList</code> of the filled list
     */
    public void process(final String filePath, final String[] colNames) {

        validateInputData(filePath, colNames);
        getInstance();
        fileProcessing(filePath, colNames);

    }

    private void getInstance() {

        if (inputData == null) {
            synchronized(this) {
                if (inputData == null) {
                    inputData = new ConcurrentHashMap<Integer, Geometry>();
                }
            }
        }

    }

    /**
     * @brief The method which runs the parsing of the <code>.dbf</code> file
     *
     * @description This method allocates the two variables required to open the
     *              <code>.dbf</code> file.
     *              <ol>
     *                  <li><code>FileInputStream</code>: to open the
     *                      <code>.dbf</code> file</li>
     *                  <li><code>DbaseFileReader</code>: to get an object
     *                      which can be easily parsed with a <code>for</code>
     *                      loop or through an <code>iterator</code></li>
     *              </ol>
     *              The choice of reading a <code>.dbf</code> file of points
     *              rather then lines is decided by the user in the <tt>main
     *              class</tt> and thanks to the <strong>Factory Method Pattern
     *              </strong> there is a unique call to <code>bodyProcessing
     *              </code>.
     *
     * @param[in] filePath
     *            The complete input path of the <code>.dbf</code> file
     * @param[in] colNames
     *            The array of strings which contains the column names to search
     * @exception IOException
     *                If no file is found
     */
    private void fileProcessing(final String filePath, final String[] colNames) {

        try {

            FileInputStream inputFile = new FileInputStream(filePath);
            DbaseFileReader dbfReader = new DbaseFileReader(inputFile.getChannel(), false, Charset.defaultCharset());

            Vector<Integer> colIndices = headerProcessing(dbfReader, colNames);
            inputData = bodyProcessing(dbfReader, colIndices); //!< factory method

            dbfReader.close();
            inputFile.close();

        } catch (IOException exception) { new IOException(exception); }

    }

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
    private Vector<Integer> headerProcessing(final DbaseFileReader dbfReader, final String[] colNames) {

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

    private void validateOutputData() {

        if (inputData.isEmpty())
            throw new NullPointerException("The output HashMap is empty. Something was wrong during the computation");

    }

    abstract protected ConcurrentHashMap<Integer, Geometry> bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> conIndices);

    abstract protected void validateInputData(final String filePath, final String[] colNames);

}
