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
package com.wordpress.growworkinghard.riverNe3.simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Results {

    private List<HashMap<Integer, double[]>> results;

    public Results() {
        results = new ArrayList<HashMap<Integer, double[]>>();
    }

    public void add(final HashMap<Integer, double[]> result) {
        results.add(result);
    }

    public HashMap<Integer, double[]> getResult(final int index) {
        return results.get(index);
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

}
