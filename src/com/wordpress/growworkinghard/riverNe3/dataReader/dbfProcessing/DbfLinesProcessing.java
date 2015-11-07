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

import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.geometry.Line;

/**
 *
 * @todo Design a better implementation of the switch-case
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @todo add documentation
 *
 * @author
 */
public class DbfLinesProcessing extends DbfProcessing {

    private volatile static HashMap<Integer, Geometry> inputData;
    private String filePath;
    private String[] colNames;

    public DbfLinesProcessing(final String filePath, final String[] colNames) {
        getInstance(filePath, colNames);
    }

    private synchronized void getInstance(final String filePath, final String[] colNames) {

        if (inputData == null) {
            validateInputData(filePath, colNames);
            inputData = new HashMap<Integer, Geometry>();
            this.filePath = filePath;
            this.colNames = colNames;
        }

    }

    public synchronized HashMap<Integer, Geometry> fileProcessing() {

        try {

            FileInputStream inputFile = new FileInputStream(filePath);
            DbaseFileReader dbfReader = new DbaseFileReader(inputFile.getChannel(), false, Charset.defaultCharset());

            Vector<Integer> colIndices = headerProcessing(dbfReader, colNames);
            bodyProcessing(dbfReader, colIndices); //!< factory method

            dbfReader.close();
            inputFile.close();

        } catch (IOException exception) { new IOException(exception); }

        validateOutputData(inputData); //!< post-conditions
        return inputData;
    }

    protected void bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> colIndices) {

        int hashMapKey = 1;

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
                            if ((fields[index].toString()).compareTo("1") == 0) {
                                tmpLine.setRoot(true);
                                tmpLine.setKey(new Key(1.0));
                                tmpLine.setParentKey(new Key(0.0));
                                tmpLine.setLayer(1);
                            }
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

                inputData.put(hashMapKey, tmpLine);
                hashMapKey++;

            } catch (IOException exception) { new IOException(exception); }

        }

    }

    protected synchronized void validateInputData(final String filePath, final String[] colNames) {

        if (filePath == null)
            throw new NullPointerException("The file path cannot be null");

        if (colNames.length != 5)
            throw new IllegalArgumentException("You must provide 5 columns: Pfafstetter, X_start, Y_start, X_end, Y_end");

    }

}
