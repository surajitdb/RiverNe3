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
package com.wordpress.growworkinghard.riverNe3.dataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

/**
 * @todo Replace CyclicBarrier with CompletionServices ==> JCIP p134
 *
 * @author
 */
public class Reader {
    private final ExecutorService exec;
    private final List<DataProcessing> mainList;

    public Reader(final List<DataProcessing> list, final ExecutorService exec) {
        this.mainList = list;
        this.exec = exec;
    }

    public List<HashMap<Integer, Geometry>> start() throws InterruptedException {
        List<ReaderTask> tasks = new ArrayList<ReaderTask>();

        for (DataProcessing dataFile : mainList)
            tasks.add(new ReaderTask(dataFile));

        List<Future<HashMap<Integer, Geometry>>> futures = exec.invokeAll(tasks);

        List<HashMap<Integer, Geometry>> data = new ArrayList<HashMap<Integer, Geometry>>(tasks.size());
        Iterator<ReaderTask> taskIter = tasks.iterator();

        for (Future<HashMap<Integer, Geometry>> f : futures) {
            ReaderTask task = taskIter.next();

            try {
                data.add(f.get());
            } catch (ExecutionException e) {
            } catch (CancellationException e) {

            }
        }

        return data;

    }

    private class ReaderTask implements Callable<HashMap<Integer, Geometry>> {
        private final DataProcessing fileProcess;

        public ReaderTask (final DataProcessing fileProcess) {this.fileProcess = fileProcess;}

        public HashMap<Integer, Geometry> call() throws Exception {
            return fileProcess.fileProcessing();
        }
    }

}
