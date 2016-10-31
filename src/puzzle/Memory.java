package puzzle;

import java.util.HashMap;

/**
 * Created by Paolo on 25/10/16.
 */
public class Memory {
    HashMap<Integer, Boolean> table;
    long count;

    public Memory() {
        this.table = new HashMap<Integer,Boolean>();
        this.count = 0;
    }

    public boolean isViewed(NPuzzle puzzle){
        int hash =puzzle.tablero.hashCode();

        boolean status = this.table.getOrDefault(hash, false);
        if (status){
            return true;
        }else{
            this.table.put(hash,true);
            count++;
            return false;
        }
    }
}
