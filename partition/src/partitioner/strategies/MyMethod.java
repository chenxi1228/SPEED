// MyMethod.java: class implementing the EPS partitioning algorithm

package partitioner.strategies;

import core.Edge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import partitioner.PartitionState;
import partitioner.coordinated_state.CoordinatedPartitionState;
import partitioner.PartitionStrategy;
import partitioner.Record;
import application.Globals;

public class MyMethod implements PartitionStrategy {

    private final Globals GLOBALS;

    public MyMethod(Globals G) {
        this.GLOBALS = G;
    }

    @Override
    public void performStep(Edge e, List<Integer> top_nodes, HashMap<Integer,Boolean> is_topk, PartitionState state) {

        int P = GLOBALS.P;
        int epsilon = 1;
        int u = e.getU();
        int v = e.getV();
        int seed = GLOBALS.SEED
        double ts = e.getTs();

        Record u_record = state.getRecord(u);
        Record v_record = state.getRecord(v);

        //*** ASK FOR LOCK
        int sleep = 2;
        while (!u_record.getLock()) {
            try {
                Thread.sleep(sleep);
            } catch (Exception ex) {
            }
            sleep = (int) Math.pow(sleep, 2);
        }
        sleep = 2;
        while (!v_record.getLock()) {
            try {
                Thread.sleep(sleep);
            } catch (Exception ex) {
            }
            sleep = (int) Math.pow(sleep, 2);
            if (sleep > GLOBALS.SLEEP_LIMIT) {
                u_record.releaseLock();
                performStep(e, top_nodes,is_topk, state);
                return;
            } //TO AVOID DEADLOCK
        }
        //*** LOCK TAKEN

        int machine_id = -1;

        //*** COMPUTE MAX AND MIN LOAD
        int MIN_LOAD = state.getMinLoad();
        int MAX_LOAD = state.getMaxLoad();

        //*** COMPUTE SCORES, FIND MIN SCORE, AND COMPUTE CANDIDATES PARTITIONS
        LinkedList<Integer> candidates = new LinkedList<Integer>();
        double MAX_SCORE = 0;

        //如果u，v均存在
        if (u_record.getReplicas() > 0 && v_record.getReplicas() > 0) {
            // System.out.println(top_nodes.get(0) +"yessssss\n");
            if (is_topk.get(u)==true && is_topk.get(v)==false) { //其中一个节点属于top_nodes，将边分配给另一个节点所在分区
                for (int m = 0; m < P; m++) {
                    if (v_record.hasReplicaInPartition(m)) {
                        candidates.add(m);
                    }
                }
            } else if (is_topk.get(u)==false && is_topk.get(v)==true) { //其中一个节点属于top_nodes，将边分配给另一个节点所在分区
                for (int m = 0; m < P; m++) {
                    if (u_record.hasReplicaInPartition(m)) {
                        candidates.add(m);
                    }
                }
            } else if (is_topk.get(u)==true && is_topk.get(v)==true) { //两个节点均是top
                state.incrementBigEdges();
                for (int m = 0; m < P; m++) {
                    int degree_u = u_record.getDegree() + 1;
                    int degree_v = v_record.getDegree() + 1;
                    int SUM = degree_u + degree_v;

                    double importance_u = u_record.getImportance() + ts / GLOBALS.maxtime;
                    double importance_v = v_record.getImportance() + ts / GLOBALS.maxtime;
                    double SUM_im = importance_u + importance_v;

                    double fu = 0;
                    double fv = 0;
                    if (u_record.hasReplicaInPartition(m)) {
                        if (GLOBALS.DEGREE_COMPUTE.equalsIgnoreCase("normal")) {
                            fu = degree_u;
                            fu /= SUM;
                        } else {
                            fu = importance_u;
                            fu /= SUM_im;
                        }
                        fu = 1 + (1 - fu);
                    }
                    if (v_record.hasReplicaInPartition(m)) {
                        if (GLOBALS.DEGREE_COMPUTE.equalsIgnoreCase("normal")) {
                            fv = degree_v;
                            fv /= SUM;
                        } else {
                            fv = importance_v;
                            fv /= SUM_im;
                        }
                        fv = 1 + (1 - fv);
                    }
                    int load = state.getMachineLoad(m);
                    double bal = (MAX_LOAD - load);
                    bal /= (epsilon + MAX_LOAD - MIN_LOAD);
                    if (bal < 0) {
                        bal = 0;
                    }
                    double SCORE_m = fu + fv + GLOBALS.LAMBDA * bal;

                    if (SCORE_m > MAX_SCORE) {
                        MAX_SCORE = SCORE_m;
                        candidates.clear();
                        candidates.add(m);
                    } else if (SCORE_m == MAX_SCORE) {
                        candidates.add(m);
                    }
                }
            } else { //两个节点均不是top
                for (int m = 0; m < P; m++) {
                    if (u_record.hasReplicaInPartition(m) && v_record.hasReplicaInPartition(m)) {//两个小节点在一个子图上
                        candidates.add(m);
                    }
                }
            }
        } else {
            for (int m = 0; m < P; m++) {

                int degree_u = u_record.getDegree() + 1;
                int degree_v = v_record.getDegree() + 1;
                int SUM = degree_u + degree_v;

                double importance_u = u_record.getImportance() + ts / GLOBALS.maxtime;
                double importance_v = v_record.getImportance() + ts / GLOBALS.maxtime;
                double SUM_im = importance_u + importance_v;

                double fu = 0;
                double fv = 0;
                if (u_record.hasReplicaInPartition(m)) {
                    if (GLOBALS.DEGREE_COMPUTE.equalsIgnoreCase("normal")) {
                        fu = degree_u;
                        fu /= SUM;
                    } else {
                        fu = importance_u;
                        fu /= SUM_im;
                    }
                    fu = 1 + (1 - fu);
                }
                if (v_record.hasReplicaInPartition(m)) {
                    if (GLOBALS.DEGREE_COMPUTE.equalsIgnoreCase("normal")) {
                        fv = degree_v;
                        fv /= SUM;
                    } else {
                        fv = importance_v;
                        fv /= SUM_im;
                    }
                    fv = 1 + (1 - fv);
                }
                int load = state.getMachineLoad(m);
                double bal = (MAX_LOAD - load);
                bal /= (epsilon + MAX_LOAD - MIN_LOAD);
                if (bal < 0) {
                    bal = 0;
                }
                double SCORE_m = fu + fv + GLOBALS.LAMBDA * bal;
                if (SCORE_m < 0) {
                    System.out.println("ERRORE: SCORE_m<0");
                    System.out.println("fu: " + fu);
                    System.out.println("fv: " + fv);
                    System.out.println("GLOBALS.LAMBDA: " + GLOBALS.LAMBDA);
                    System.out.println("bal: " + bal);
                    System.exit(-1);
                }
                if (SCORE_m > MAX_SCORE) {
                    MAX_SCORE = SCORE_m;
                    candidates.clear();
                    candidates.add(m);
                } else if (SCORE_m == MAX_SCORE)
                    candidates.add(m);
            }
        }

        // drop the edge
        if (candidates.isEmpty()) {
            state.incrementDropNum();
        } else {
            //*** PICK A RANDOM ELEMENT FROM CANDIDATES
            Random r = new Random(seed);
            int choice = r.nextInt(candidates.size());
            machine_id = candidates.get(choice);


            if (state.getClass() == CoordinatedPartitionState.class) {
                CoordinatedPartitionState cord_state = (CoordinatedPartitionState) state;
                //NEW UPDATE RECORDS RULE TO UPDATE THE SIZE OF THE PARTITIONS EXPRESSED AS THE NUMBER OF VERTICES THEY CONTAINS
                if (!u_record.hasReplicaInPartition(machine_id)) {
                    u_record.addPartition(machine_id);
                    cord_state.incrementMachineLoadVertices(machine_id);
                }
                if (!v_record.hasReplicaInPartition(machine_id)) {
                    v_record.addPartition(machine_id);
                    cord_state.incrementMachineLoadVertices(machine_id);
                }
            } else {
                //1-UPDATE RECORDS
                if (!u_record.hasReplicaInPartition(machine_id)) {
                    u_record.addPartition(machine_id);
                }
                if (!v_record.hasReplicaInPartition(machine_id)) {
                    v_record.addPartition(machine_id);
                }
            }

            //2-UPDATE EDGES
            if (!(is_topk.get(u)==true && is_topk.get(v)==true)){
                state.incrementMachineLoad(machine_id, e);
            }


            //3-UPDATE DEGREES
            u_record.incrementDegree();

            v_record.incrementDegree();

            //4-UPDATE IMPORTANCE
            u_record.incrementImportance(ts, GLOBALS.maxtime);
            v_record.incrementImportance(ts, GLOBALS.maxtime);

        }
        //*** RELEASE LOCK
        u_record.releaseLock();
        v_record.releaseLock();
    }
}
