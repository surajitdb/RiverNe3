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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.geometry.Point;

/**
 * @brief Parser for <code>.dbf</code> files of <tt>Point</tt> type
 *
 * @description This class parses the <code>.dbf</code> file of a
 *              <code>.shp</code> of type point. In this way each element of the
 *              shapefile is converted in a <tt>Geometry</tt> object of type
 *              <tt>Line</tt>.
 *              <p>
 *              This class can be accessed from just one thread per time.
 *              Parsing a file in multithreading would required the
 *              implementation of a cache where temporary store the complete
 *              file and the parsing that structure with many threads.
 *              </p>
 *
 * @todo Implement a cache, in order to parse the file in multithreading
 * @todo Verify if it is possibile to change from HashMap to List for inputData
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class DbfPointsProcessing extends DbfProcessing {

    private final HashMap<Integer, Geometry> inputData; //!< parsed data of the <code>.dbf</code>
    private final String filePath; //!< path of the file to parse
    private final String[] columnNames; //!< names of the columns to parse

    /**
     * @brief Constructor
     *
     * @param[in] filePath The path of the file to parse
     * @param[in] columnNames The names of the columns to parse, in order to
     *            create the <tt>Line-Geometry</tt> object
     */
    public DbfPointsProcessing(final String filePath, final String[] columnNames) {
        validateInputData(filePath, columnNames); // precondition

        this.inputData = new HashMap<Integer, Geometry>();
        this.filePath = filePath;
        this.columnNames = columnNames;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.wordpress.growworkinghard.riverNe3.dataReader.DataReading#fileProcessing()
     */
    public HashMap<Integer, Geometry> fileProcessing() throws IOException {

        try {

            FileInputStream inputFile = new FileInputStream(filePath);
            DbaseFileReader dbfReader = new DbaseFileReader(inputFile.getChannel(), // input file
                                                            false, // memory mapped buffer
                                                            Charset.defaultCharset()); // charset

            Vector<Integer> columnIndices = headerProcessing(dbfReader, columnNames);
            bodyProcessing(dbfReader, columnIndices);

            dbfReader.close();
            inputFile.close();

        } catch (IOException exception) {
            throw new IOException(exception.getCause());
        }

        validateOutputData(inputData); // postcondition
        return inputData;
    }

    /**
     * {@inheritDoc}
     *
     * @see DbfProcessing#bodyProcessing(DbaseFileReader,Vector<Integer>)
     */
    protected void bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> columnIndices) throws IOException {

        int hashMapKey = 1;

        while(dbfReader.hasNext()) {

            try {

                Point tmpPoint = new Point();
                double x = 0.0, y = 0.0;

                Object[] fields = dbfReader.readEntry();

                for (int i = 0; i < columnIndices.size(); i++) {

                    int index = columnIndices.get(i);

                    switch (i)
                    {
                        case 0: // x coordinate of the point
                            x = parseDoubleField(fields[index]);
                            break;
                        case 1: // y coordinate of the point
                            y = parseDoubleField(fields[index]);
                            break;
                    }
                }

                tmpPoint.setPoint(x, y);

                inputData.put(hashMapKey, tmpPoint);
                hashMapKey++;

            } catch (IOException exception) {
                throw new IOException(exception.getCause());
            }

        }

    }

    /**
     * {@inheritDoc}
     *
     * @see DbfProcessing#validateInputData(String,String[])
     */
    protected void validateInputData(final String filePath, final String[] colNames) {

        if (filePath == null)
            throw new NullPointerException("The file path cannot be null");

        if (colNames.length != 2)
            throw new IllegalArgumentException("You must provide 2 columns: X, Y");

    }

    /**
     * @brief Parsing of the field which is a double
     *
     * @param[in] field The field of the row to parse
     * @return The field converted in double format
     */
    private double parseDoubleField(final Object field) {
        final Double parsedDouble = Double.parseDouble(field.toString());
        return parsedDouble.doubleValue();
    }

}
