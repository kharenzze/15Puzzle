package puzzle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    /**
     * CTes. para indicar los segundos máximos de espera
     */
    public static final int TMAX = 180;

    public static void main(String[] args) {
        int n;
        NPuzzle puzzle;
        String salida=null;
        //Si el número de parámetros es incorrecto salimos
        if (args.length < 2){
            System.out.println("npuzzle "+"<fich_puzzle> 8/15 [<fich_salida>]\n");
            System.out.println(" Usando n=8. Genero puzzle aleatorio.  ");
            n=8;
            puzzle= new NPuzzle(n);

            while(!puzzle.resoluble())puzzle = new NPuzzle(n);

        } else {
            n=Integer.valueOf(args[1]);
            puzzle= new NPuzzle(args[0],n);
            if (args.length>2) salida=args[2];
        }

        System.out.println("Puzzle Inicial:\n"+puzzle);

        //Miramos si el puzzle tiene solución
        if (!puzzle.resoluble()){
            System.out.println("EL puzzle NO tiene solución\n");
            System.exit(0);
        } else  System.out.println("EL puzzle SÍ tiene solución\n");

        //Lanzamos un algoritmo de búsqueda aleatoria y miramos el tiempo que tarda
        System.out.println("\nComienza la búsqueda aleatoria ("+TMAX+"segundos max)");
        long tiempo_inicial=System.currentTimeMillis();
        NPuzzle copia=new NPuzzle(puzzle);
        ArrayList<Integer> movs=null;
        ArrayList<NPuzzle> nodos=copia.busquedaAleatoria();
        if (nodos.size()<=0)
            System.out.println("Solución NO encontrada.");
        else {
            System.out.println("Solución encontrada. Nodos vistos:"+nodos.size());//+"\n ->"+nodos)
        }
        System.out.println("Tiempo ="+(System.currentTimeMillis()-tiempo_inicial)/1000.+"seg");

        /*Guardamos la salida, si nos han dado un fichero para guardarlo*/
        if (salida!=null && movs!=null) {
            try {
                Writer out = new FileWriter(salida);
                for (Integer i: movs)
                    out.write(i+" ");
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(NPuzzle.class.getName()).log(Level.SEVERE, "Fallo escribiendo fichero salida", ex);
            }
        }
    }
}
