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
package com.wordpress.growworkinghard.riverNe3.junitTests;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

import com.wordpress.growworkinghard.riverNe3.dataReader.DataReading;
import com.wordpress.growworkinghard.riverNe3.dataReader.Reader;
import com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing.DbfLinesProcessing;
import com.wordpress.growworkinghard.riverNe3.dataReader.dbfProcessing.DbfPointsProcessing;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

/**
 * @brief Test of the dbf reader
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @date March 19, 2016
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class TestReader {

    public int availableProcessors = Runtime.getRuntime().availableProcessors();
    public ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
    private Reader reader;

    /**
     * @brief Default constructor
     */
    public TestReader() {}

    @Test
    public void testReadInputData() throws InterruptedException {

        String treeDataPath = getDataPath("net.dbf");
        String hydrometersDataPath = getDataPath("mon_point.dbf");
        String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};
        String[] colNamesPoints = {"X_coord", "Y_coord"};

        DataReading dbLines = new DbfLinesProcessing(treeDataPath, colNames);
        DataReading dbPoints = new DbfPointsProcessing(hydrometersDataPath, colNamesPoints);

        reader = new Reader(executor, dbLines, dbPoints);

        assertTests(reader);

    }

    private void assertTests(final Reader reader) {

        String treeMessage = "The file including data of the tree has not been read";
        assertFalse(treeMessage,reader.getReadData(0).isEmpty());
    }

    private String getDataPath(final String fileName) {

        String workingDirectory = System.getProperty("user.dir");
        String dataDirectory = workingDirectory + "/data/";
        return dataDirectory + fileName;

    }

    public HashMap<Integer, Geometry> getReadData(final int fileIndex) {
        return reader.getReadData(fileIndex);
    }

}
