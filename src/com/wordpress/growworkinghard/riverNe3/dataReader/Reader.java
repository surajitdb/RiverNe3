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
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Reader {
    private final List<DataProcessing> mainList;
    private final List<Worker> workers = new ArrayList<Worker>();
    private final List<Integer> nSplit = new ArrayList<Integer>();
    private CyclicBarrier barrier;

    public Reader(final List<DataProcessing> list) {
        this.mainList = list;
        computeThreadsNumber(); 
    }

    public void start() {
        for (int i = 0; i < nSplit.size(); i++) {

            final int loop = i+1;
            Runnable barrierAction = new Runnable() {
                public void run() {
                    System.out.println("BarrierAction " + loop + " executed.");
                }
            };

            barrier = new CyclicBarrier(nSplit.get(i), barrierAction);
            int endLoop = nSplit.get(i);
            for (int j = 0; j < endLoop; j++) {
                workers.add(new Worker(mainList.get(0)));
                new Thread(workers.get(j)).start();
                mainList.remove(0);
            }
        }
        try {
            barrier.await();
        } catch (InterruptedException ex) {
            return;
        } catch (BrokenBarrierException ex) {
            return;
        }
    }

    private void computeThreadsNumber() {

        int count = Runtime.getRuntime().availableProcessors();
        int files = mainList.size();

        if (files <= count) nSplit.add(files);
        else {
            int nLoops = files / count;
            for (int i = 0; i < nLoops; i++) nSplit.add(count);
            int remainder = files - count * nLoops;
            nSplit.add(remainder);
        }

    }

    private class Worker implements Runnable {
        private final DataProcessing fileProcess;

        public Worker (final DataProcessing fileProcess) {this.fileProcess = fileProcess;}

        public void run() {
            fileProcess.fileProcessing();
            try {
                barrier.await();
            } catch (InterruptedException ex) {
                return;
            } catch (BrokenBarrierException ex) {
                return;
            }
        }
    }

}
