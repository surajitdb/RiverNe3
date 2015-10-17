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

import java.util.List;
import java.util.Vector;

import org.geotools.data.shapefile.dbf.DbaseFileReader;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

public class DbfPointsProcessing extends DbfProcessing {

    protected List<Geometry> bodyProcessing(final DbaseFileReader dbfReader, final Vector<Integer> colIndices) {

        new UnsupportedOperationException("Method not implemented yet");
        return new Vector<Geometry>();

    }

    /**
     * @brief This main is just a simple to test.
     *
     * @param args
     */
    // public static void main(String[] args) {

    //     String filePath = "/home/francesco/vcs/git/personal/RiverNe3/data/net.dbf";
    //     String type = "lines";
    //     String[] colNames = {"pfaf", "X_start", "Y_start", "X_end", "Y_end"};

    //     DbfProcessing dfbp = new DbfProcessing();
    //     List<Geometry> test = dfbp.get(filePath, type, colNames);

    //     for (int i = 0; i < test.size(); i++) {
    //         System.out.println(test.get(i).getStartPoint().x + " " + test.get(i).isRoot());
    //     }

    // }

}
