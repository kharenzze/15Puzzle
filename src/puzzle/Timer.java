package puzzle;

/**
 * Created by Paolo on 31/10/2016.
 */
public class Timer {
    private long startTime;

    public Timer (){
        this.start();
    }

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public long duration(){
        return System.currentTimeMillis() - startTime;
    }
}
