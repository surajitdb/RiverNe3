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
package com.wordpress.growworkinghard.riverNe3;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.geometry.Line;

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
 * @todo: The private method <code>fileProcessing</code> contains the call to
 *        the private methods <code>pointsBodyProcessing</code> and
 *        <code>linesBodyProcessing</code>. That call <strong>must</strong> be
 *        replaced with a better design, e.g. <strong>Factory Pattern</strong>,
 *        in order to apply the <strong>DEPENDENCY INVERSION PRINCIPLE</strong>
 *
 * @todo: Commenting the methods
 *        <ul>
 *        <li><code>pointsBodyProcessing</code></li>
 *        <li><code>linesBodyProcessing</code></li>
 *        </ul>
 *
 * @todo: Design a better implementation of the switch-case in
 *        <code>linesBodyProcessing</code>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @author
 * @version 1.0
 * @date October 13, 2015
 * @copyright GNU Public License v3
 */
@ThreadSafe
public class DbfProcessing {

    /**
     * @brief Default constructor
     */
    public DbfProcessing() {}

    /**
     * @brief Getter method which return the list of geometric features
     *
     * @description This is the only one public method. It returns a
     *              synchronizedList of objects of type Geometry.
     *
     * @param filePath
     *            The complete input path of the <code>.dbf</code> file
     * @param type
     *            The type of geometry of the shapefile
     * @param colNames
     *            An array of strings of the column names to search. It has to
     *            be structured in the following way
     *            <ol>
     *            <li><strong>Line</strong>:
     *            <ul>
     *            <li>Column name of the Pfafstetter numbering;</li>
     *            <li>Column name of the X coordinate of the starting point;
     *            </li>
     *            <li>Column name of the Y coordinate of the starting point;
     *            </li>
     *            <li>Column name of the X coordinate of the ending point;</li>
     *            <li>Column name of the Y coordinate fo the ending point.</li>
     *            </ul>
     *            </ol>
     * @return A <code>Collections.synchronizedList</code> of the filled list
     */
    public List<Geometry> get(final String filePath, final String type, final String[] colNames) {

        List<Geometry> list = new Vector<Geometry>();
        fileProcessing(list, filePath, type, colNames);
        return Collections.synchronizedList(list);

    }

    /**
     * @brief The method which runs the parsing of the <code>.dbf</code> file
     *
     * @description This method allocates the two variables required to open the
     *              <code>.dbf</code> file.
     *              <ol>
     *              <li><code>FileInputStream</code>: to open the
     *              <code>.dbf</code> file</li>
     *              <li><code>DbaseFileReader</code>: to get an object which can
     *              be easily parsed with a <code>for</code> loop or through an
     *              <code>iterator</code></li>
     *              </ol>
     *
     * @todo This method contains the call to <code>pointsBodyProcessing</code>
     *       and <code>linesBodyProcessing</code>. Those lines must be replaced
     *       with a better design, e.g. <strong>Factory Pattern</strong>, in
     *       order to apply the <strong>DEPENDENCY INVERSION PRINCIPLE</strong>
     *
     * @param list
     *            The empy <code>List</code> of <code>Geometry</code> which has
     *            to be filled
     * @param filePath
     *            The complete input path of the <code>.dbf</code> file
     * @param type
     *            The type of geometry of the shapefile
     * @param colNames
     *            The array of strings which contains the column names to search
     * @exception IOException
     *                If no file is found
     */
    private void fileProcessing(final List<Geometry> list, final String filePath, final String type, final String[] colNames) {

        try {

            FileInputStream inputFile = new FileInputStream(filePath);
            DbaseFileReader dbfReader = new DbaseFileReader(inputFile.getChannel(), false, Charset.defaultCharset());

            Vector<Integer> colIndices = headerProcessing(dbfReader, colNames);

            if (type.compareTo("points") == 0)
                pointsBodyProcessing(list, dbfReader, colIndices);
            else if (type.compareTo("lines") == 0)
                linesBodyProcessing(list, dbfReader, colIndices);

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
     * @param dbfReader
     *            The <code>dbf</code> reader
     * @param colNames
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

    // TODO: put the following two methods in separated classes, which
    // inheredit from this class that will become an abstract class.
    // Probably the following two methods will have an abstract signature in
    // this class because it's the same for both the methods.
    //
    // The main idea is applying the DEPENDENCY INVERSION PRINCIPLE and at the
    // same time designing an appropriate Factory Pattern

    private void pointsBodyProcessing(final List<Geometry> list, final DbaseFileReader dbfReader, final Vector<Integer> colIndices) {

        new UnsupportedOperationException("Method not implemented yet");

    }

    private void linesBodyProcessing(final List<Geometry> list, final DbaseFileReader dbfReader, final Vector<Integer> colIndices) {

        while(dbfReader.hasNext()) {

                        
            try {

                Object[] fields;
                Geometry tmpLine = new Line();

                double x_start = 0.0, y_start = 0.0, x_end = 0.0, y_end = 0.0;

                fields = dbfReader.readEntry();

                for (int i = 0; i < colIndices.size(); i++) {

                    int index = colIndices.get(i);
                    Double tmp;
                    double tmpVal;

                    switch (i)
                    {
                        case 0:
                            if ((fields[index].toString()).compareTo("1") == 0) tmpLine.setRoot(true);
                            break;
                        case 1:
                            tmp = Double.parseDouble(fields[index].toString());
                            tmpVal = tmp.doubleValue();
                            x_start = tmpVal;
                            break;
                        case 2:
                            tmp = Double.parseDouble(fields[index].toString());
                            tmpVal = tmp.doubleValue();
                            y_start = tmpVal;
                            break;
                        case 3:
                            tmp = Double.parseDouble(fields[index].toString());
                            tmpVal = tmp.doubleValue();
                            x_end = tmpVal;
                            break;
                        case 4:
                            tmp = Double.parseDouble(fields[index].toString());
                            tmpVal = tmp.doubleValue();
                            y_end = tmpVal;
                            break;
                    }

                }

                tmpLine.setStartPoint(x_start, y_start);
                tmpLine.setEndPoint(x_end, y_end);

                list.add(tmpLine);

            } catch (IOException exception) { new IOException(exception); }

        }

    }

    /**
     * @brief This main is just a simple to test.
     *
     * @param args
     */
    public static void main(String[] args) {

        String filePath = "/home/francesco/vcs/git/personal/RiverNe3/data/net.dbf";
        String type = "lines";
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};

        DbfProcessing dfbp = new DbfProcessing();
        List<Geometry> test = dfbp.get(filePath, type, colNames);

        for (int i = 0; i < test.size(); i++) {
            System.out.println(test.get(i).getStartPoint().x + " " + test.get(i).isRoot());
        }

    }

}
