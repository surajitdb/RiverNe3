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
import java.util.HashMap;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.geometry.Point;

public class DbfPointsProcessing extends DbfProcessing {

    private static HashMap<Integer, Geometry> inputData;
    private String filePath;
    private String[] colNames;

    public DbfPointsProcessing(final String filePath, final String[] colNames) {
        validateInputData(filePath, colNames);
        inputData = new HashMap<Integer, Geometry>();
        this.filePath = filePath;
        this.colNames = colNames;
    }

    public HashMap<Integer, Geometry> get() {
        validateOutputData(inputData); //!< post-conditions
        return inputData;
    }

    public void fileProcessing() {

        try {

            FileInputStream inputFile = new FileInputStream(filePath);
            DbaseFileReader dbfReader = new DbaseFileReader(inputFile.getChannel(), false, Charset.defaultCharset());

            Vector<Integer> colIndices = headerProcessing(dbfReader, colNames);
            bodyProcessing(dbfReader, colIndices); //!< factory method

            dbfReader.close();
            inputFile.close();

        } catch (IOException exception) { new IOException(exception); }

    }

    protected void bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> colIndices) {

        int hashMapKey = 1;

        while(dbfReader.hasNext()) {

            try {

                Object[] fields;
                Point tmpPoint = new Point();

                double x = 0.0, y = 0.0;

                fields = dbfReader.readEntry();

                for (int i = 0; i < colIndices.size(); i++) {

                    int index = colIndices.get(i);
                    Double tmp;
                    double tmpVal;

                    tmp = Double.parseDouble(fields[index].toString());
                    tmpVal = tmp.doubleValue();

                    switch (i)
                    {
                        case 0:
                            x = tmpVal;
                            break;
                        case 1:
                            y = tmpVal;
                            break;
                    }
                }

                tmpPoint.setPoint(x, y);

                inputData.put(hashMapKey, tmpPoint);
                hashMapKey++;

            } catch (IOException exception) { new IOException(exception); }

        }

    }

    protected synchronized void validateInputData(final String filePath, final String[] colNames) {

        if (filePath == null)
            throw new NullPointerException("The file path cannot be null");

        if (colNames.length != 2)
            throw new IllegalArgumentException("You must provide 2 columns: X, Y");

    }

}
