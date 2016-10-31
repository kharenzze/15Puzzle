
package puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase implementa el problema del n-Puzzle, normalmente n=8 o n=15.
 * 
 * @author fjgc@decsai.ugr.es
 */

public class NPuzzle {

    /**
     * Tamaño del problema, el número de casillas es n+1
     */
    int n;

    /**
     * Tablero de juego
     */
    ArrayList<Integer> tablero;

    /**
     * Movimiento para llegar al nodo padre, si es ! de 1,2,3,4 no lo sabemos
     */
    int padre;

    /**
     * Valor de la función costo para el nodo (profundidad)
     */
    int g;

    /**
     * Valor de la función heurística para el nodo
     */
    int h;

    /**
     * Raiz de n. Estatico, siempre el mismo valor en un mismo juego
     */
    static int raiz;

    /**
     * Posición del hueco
     */
    int posicionHueco;

    /**
     * CTes. para las direcciones del hueco
     */
    public static final int ARRIBA = 1;
    public static final int ABAJO = 2;
    public static final int DERECHA = 3;
    public static final int IZQUIERDA = 4;
    
    
    
   /*---------------------------------------------------------------------------*/

    /**
     * Constructor para crear un n-puzzle a partir de un fichero.
     *
     * @param fichero nombre del fichero con el n-puzzle
     * @param n       indica el tamaño del tablero. Lo rormal 8 ó 15 (8-puzzle/15puzzle)
     */
    public NPuzzle(String fichero, int n) {

        NPuzzle.raiz = (int)Math.sqrt(n+1);

        //Guardamos el n si es correcto
        if (Math.sqrt(n + 1) != Math.round(Math.sqrt(n + 1))) {
            System.out.println("ERROR: Imposible usar n=" + n + " Usando n=8");
            this.n = 8;
        } else
            this.n = n;

        //Ahora obtenemos el tablero
        tablero = new ArrayList<>();

        //Leemos el tablero de un fichero y si no podemos lo generamos 
        //aleatoriamente
        try {
            int number,i=0;
            Scanner scanner = new Scanner(new File(fichero));
            while(scanner.hasNextInt()){
                number = scanner.nextInt();
                tablero.add(number);
                if (number == 0) this.posicionHueco = i;
                i++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NPuzzle.class.getName()).log(Level.SEVERE, null, ex);
            //Si ha falado la lectura del fichero,
            //creamos un tablero con posiciones aleatorias
            tablero = generaPermutacion(n + 1);
        }

        //Iniciamos el padre del nodo a 0
        this.padre = 0;
        //Iniciamos el costo del nodo a 0
        this.g = 0;
        //Iniciamos la heuristica del nodo
        this.h = heuristica();

    }
    /*---------------------------------------------------------------------------*/

    /**
     * Constructor para un tablero aleatorio de un n-puzzle.
     *
     * @param n indica el tamaño del tablero. Lo rormal 8 ó 15 (8-puzzle/15puzzle)
     */
    public NPuzzle(int n) {

        NPuzzle.raiz = (int)Math.sqrt(n+1);

        //Guardamos el n si es correcto
        if (Math.sqrt(n + 1) != Math.round(Math.sqrt(n + 1))) {
            System.out.println("ERROR: Imposible usar n=" + n + " Usando n=8");
            this.n = 8;
        } else
            this.n = n;
        //Creamos un tablero con posiciones aleatorias
        tablero = generaPermutacion(n + 1);

        //Iniciamos el padre del nodo a 0
        this.padre = 0;
        //Iniciamos el costo del nodo a 0
        this.g = 0;
        //Iniciamos la heuristica del nodo
        this.h = heuristica();
    }
 /*---------------------------------------------------------------------------*/

    /**
     * Constructor copiar para un tablero n-puzzle.
     *
     * @param puzzle a copiar.
     */
    public NPuzzle(NPuzzle puzzle) {
        this.n = puzzle.n;
        this.tablero = new ArrayList<>(this.n + 1);
        for (int i = 0; i < this.n + 1; i++)
            this.tablero.add(puzzle.tablero.get(i));

        this.posicionHueco = puzzle.posicionHueco;
        //Iniciamos el padre del nodo 
        this.padre = puzzle.padre;
        //Iniciamos el costo del nodo 
        this.g = puzzle.g;
        //Iniciamos la heuristica del nodo
        this.h = puzzle.h;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Este procedimiento genera una permutación aleatorio de tamaño n y lo
     * devuelve en un ArrayList.
     *
     * @param n tamaño de la permutación
     * @return ArrayList donde se alamacena la permutación
     */
    final ArrayList<Integer> generaPermutacion(int n) {
        ArrayList<Integer> permutacion = new ArrayList<>(n);
        //Generamos los números de la permutación
        for (int i = 0; i < n; i++)
            permutacion.add(i);
        //Los mezclamos y los devolvemos
        java.util.Collections.shuffle(permutacion);
        int hueco=0;
        while (permutacion.get(hueco)!=0) hueco++;
        this.posicionHueco = hueco;
        return permutacion;
    }
    /*---------------------------------------------------------------------------*/
    /**
     * Busca el hueco dentro de un array y devuelve la posicioón
     * @return posicion del hueco
     */
    private int  buscarHueco(){
        int hueco=0;
        while (this.tablero.get(hueco)!=0) hueco++;
        return hueco;
    }
    /*---------------------------------------------------------------------------*/
    /**
     * Esta función calcula el valor f del nodo como la suma del coste (g) y la
     * herutistica (h)
     *
     * @return f(node)=g(node)+h(node)
     */
    public int f() {
        return this.g + this.h;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Esta función devuelve la profundida nodo
     *
     * @return la profundidad enla qeu está el nodo en el árbol de búsqueda
     */
    public int profundidad() {
        return this.g;
    }
   
    /*---------------------------------------------------------------------------*/

    /**
     * Esta función devuelve un valor heurítico para el tablero dado basado en la
     * distancia de Hamming, que nos da el número de casillas mal colocadas. Si todas
     * están mal colocadas devolverá n y si están todas bien colocadas devolverá 0.
     *
     * @param puzzle del cual queremos calcular la heurística.
     * @return distancia de Hamming del puzzle
     */
    public int heuristica2() {
        int i, distancia;

        for (i = 0, distancia = 0; i < this.n + 1; i++)
            //si encontramos una ficha mal aumentamos la distancia.
            if (this.tablero.get(i) != i)
                distancia++;
        return distancia;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Esta función devuelve un valor heurístico para el tablero dado basado en la
     * distancia Manhattan, que nos da la suma de las distancias desde la posición
     * actual de cada ficha hasta su posición original.
     *
     * @param puzzle del cual queremos calcular la heurística.
     * @return distancia de Hamming del puzzle
     */
    public int heuristica() {
        int i, distancia;
        int fila_actual = 1, columna_actual = 1; /*Posición de la casilla que estamos viendo*/
        int fila = 1, columna = 1; /*Posición donde debería estar la casilla*/
        int c; /*Casilla actual*/
        int ancho = (int) Math.sqrt(this.n + 1);/*Ancho del tablero*/

        for (i = 0, distancia = 0; i < this.n + 1; i++) {
            c = this.tablero.get(i);

            if (c != 0) {
                /*Buscamos cual es la casilla que debería ocupar*/
                fila = 1;
                while (!(c >= ancho * (fila - 1) && c < ancho * fila)) fila++;
                columna = (c % ancho) + 1;

                /*Añadimos la distancia*/
                distancia += Math.abs(fila - fila_actual) + Math.abs(columna - columna_actual);
            }

            /*Nos vamos a la siguiente casilla*/
            columna_actual++;
            if (columna_actual > ancho) {
                columna_actual = 1;
                fila_actual++;
            }
        }

        return distancia;
    }

    /*---------------------------------------------------------------------------*/

    /**
     * Convierte el objeto NPuzzle a cadena para porder imprimirlo
     */
    @Override
    public String toString() {
        int filas = (int) Math.sqrt(n + 1);
        String out = n + "-Puzzle g=" + this.g + " h=" + this.h + "{\n"; //Mostramos el n
        //Mostramos el tablero
        for (int i = 0; i <= n; i++) {
            int x = tablero.get(i);
            if (x != 0) out += (x + " ");
            else out += ("_ ");
            if ((i + 1) % filas == 0) out += ("\n");
        }
        out += "}\n";
        return out;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Miramos si este n-puzzle está en su estado objetivo
     *
     * @return si está o no en su estado objetivo
     */
    public boolean objetivo() {
        for (int i = 0; i < n; i++)
            //si encontramos una ficha mal colocada no está terminado
            if (this.tablero.get(i) != i) {
                return false;
            }
        return true;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Esta función nos dice si el n-puzzle tiene solución o no. Un tablero será
     * resoluble si :
     * -Si el ancho del tablero es impar (p.e. 8-puzzle) si el número de
     * inversiones es par el tablero es resoluble.
     * -Si el ancho del tablero es par (p.e. 15-puzzle) si el número de
     * inversiones + la fila donde está el hueco tiene la misma paridad
     * que el tablero solución (que tiene 0 inversiones + fila donde está
     * el hueco), entonces el tablero es resoluble.
     * Nota: Se cuenta el número de fila desde arriba. Una inversión es cuando
     * a está antes que b y a>b. Para calcular las inversiones se descargta el
     * hueco y se disponen todas las piezas consecutivamente (como en un vector).
     *
     * @param puzzle a comprobar
     * @return si tiene solución o no.
     */
    boolean resoluble() {
        int i, j;
        int inversiones = 0;
        int pos0 = 0, fila = 1;
        int ancho = (int) Math.sqrt(this.n + 1);/*Ancho del tablero*/

 
	/*Calculamos el número de inversiones del puzzle, una inversión es
	  cuando a está antes que b y a>b (descartamos el hueco)*/
        for (i = 0; i < this.n; i++)
            if (tablero.get(i) != 0)
                for (j = i + 1; j < this.n + 1; j++) {
                    if (tablero.get(i) > tablero.get(j) && tablero.get(j) != 0)
                        inversiones++;
                }

        /*Si el ancho es impar, entonces el número de inversiones en un tablero
	  resoluble es par*/
        if (ancho % 2 != 0) {
            return inversiones % 2 == 0;
        } else {
            /*Caso de tablero impar*/

            /*Buscamos la posición del hueco*/
            for (pos0 = 0; tablero.get(pos0) != 0 && pos0 < n + 1; pos0++) ;
	    /*Buscamos la fila en la que está el hueco*/
            fila = 1;
            while (!(pos0 >= ancho * (fila - 1) && pos0 < ancho * fila)) fila++;
            /*Nuestro tablero solución tiene paridad impar, pues el 0 lo tiene
            en la primera fila, por tanto si las inversiones+ la fila del hueco es
            impar tiene solución*/
            return (inversiones + fila) % 2 != 0;
        }
    }

    /*---------------------------------------------------------------------------*/
    /*Método para comparar dos tableros del n-Puzzle*/
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NPuzzle other = (NPuzzle) obj;

        //Distinto n, distintos seguro
        if (this.n != other.n) {
            return false;
        }

        //Distinta heurística, distintos seguro
        if (this.h != other.h) {
            return false;
        }

        //Finalemente, comparamos tablero
        for (int i = 0; i < n; i++)
            //si encontramos una ficha distinta, son distintos
            if (!Objects.equals(this.tablero.get(i), other.tablero.get(i)))
                return false;

        return true;
    }

    /*---------------------------------------------------------------------------*/
    public void swap(int i, int j) {
        Integer aux = tablero.get(i);
        tablero.set(i, tablero.get(j));
        tablero.set(j, aux);
    }
    /*---------------------------------------------------------------------------*/
    private void moveHoleTo(int dest){
        tablero.set(this.posicionHueco, tablero.get(dest));
        tablero.set(dest,0);
        this.posicionHueco = dest;
    }
    /*---------------------------------------------------------------------------*/
    /**
     * Obtenemos el movimiento inversodel que le pasamos como parámetro
     *
     * @param movimiento dirección del movimiento del hueco
     * @return movimiento inverso
     */
    public int inverso(int movimiento) {
        if (movimiento == IZQUIERDA) return DERECHA;
        if (movimiento == DERECHA) return IZQUIERDA;
        if (movimiento == ARRIBA) return ABAJO;
        if (movimiento == ABAJO) return ARRIBA;
        return -1;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Movemos el espacio en blanco, si podemos.
     *
     * @param movimiento dirección del movimiento del hueco
     * @return si está o no en su estado objetivo
     */
    public boolean mueve(int movimiento) {

        //Cogemos la raiz de n+1 pues nos hará falta para mirar movimientos válidos.

        if (movimiento==IZQUIERDA) {
            if (this.posicionHueco==0 || this.posicionHueco%raiz==0)
                return false;
            else {
//                this.swap(this.posicionHueco-1,this.posicionHueco);
                this.moveHoleTo(this.posicionHueco-1);
                this.g++; //actualizamos función costo
                this.h=heuristica(); //actualizamos función heurística
                this.padre=inverso(movimiento);
                return true;
            }
        } else if (movimiento==DERECHA) {
            if (this.posicionHueco==n || (this.posicionHueco+1)%raiz==0)
                return false;
            else {
//                this.swap(this.posicionHueco+1,this.posicionHueco);
                this.moveHoleTo(this.posicionHueco+1);
                this.g++; //actualizamos función costo
                this.h=heuristica(); //actualizamos función heurística
                this.padre=inverso(movimiento);
                return true;
            }

        } else if (movimiento==ARRIBA) {
            if (this.posicionHueco>=0 && this.posicionHueco<raiz)
                return false;
            else {
//                this.swap(this.posicionHueco-raiz,this.posicionHueco);
                this.moveHoleTo(this.posicionHueco-raiz);
                this.g++; //actualizamos función costo
                this.h=heuristica(); //actualizamos función heurística
                this.padre=inverso(movimiento);
                return true;
            }

        } else if (movimiento==ABAJO) {
            if (this.posicionHueco<=n && this.posicionHueco>=raiz*(raiz-1))
                return false;
            else {
//                this.swap(this.posicionHueco+raiz,this.posicionHueco);
                this.moveHoleTo(this.posicionHueco+raiz);
                this.g++; //actualizamos función costo
                this.h=heuristica(); //actualizamos función heurística
                this.padre=inverso(movimiento);
                return true;
            }

        }

        //Movimiento erróneo
        return false;

    }
    /*---------------------------------------------------------------------------*/

    /**
     * Este método realiza una búsqueda aleatoria de la solución de un
     * n-puzzle, durante TMAX segundos.
     *
     * @return la lista de movimiento realizados para llegar al estado objetivo, si
     * está vacía es que no se ha encontrado ninguna solución o el tablero pasado era
     * el objetivo.
     */
    public ArrayList<NPuzzle> busquedaAleatoria() {

        int movimiento;
        int movPadre = -1;/*Movimiento para llegar al padre*/
        ArrayList<NPuzzle> vistos = new ArrayList<>(); /*Lista de nodos vistos*/

        long tiempo_inicial = System.currentTimeMillis();
        while (!this.objetivo()) {
            movimiento = (int) (Math.random() * 5);
            if (movimiento != movPadre && this.mueve(movimiento)) {
                vistos.add(0, new NPuzzle(this));
                movPadre = inverso(movimiento);
            }

            /*Si llevamos más de TMAX segundos, no hemos encontrado solución*/
            double tiempo_total = (System.currentTimeMillis() - tiempo_inicial) / 1000.;
            if (tiempo_total > 60/*TMAX*/) {
                System.out.println("Vistos=" + vistos.size());
                return new ArrayList<>();
            }
        }
        return vistos;
    }
    /*---------------------------------------------------------------------------*/

    /**
     * Este método devuelve el plan de movimientos seguidos desde el origen hasta el
     * objetivo (que debería ser el primero de la lista).
     *
     * @param lista  listado de nodos
     * @param origen nodo original
     * @return la lista con los movimientos.
     */
    public ArrayList<Integer> plan(ArrayList<NPuzzle> lista, NPuzzle origen) {
        ArrayList<Integer> movimientos = new ArrayList<>(lista.size()); /*Lista de nodos vistos*/
        int tam = lista.size();
        NPuzzle actual = new NPuzzle(lista.get(0));
        while (!actual.equals(origen)) {
            movimientos.add(0, inverso(actual.padre));
            actual.mueve(actual.padre);
            int pos = lista.indexOf(actual);
            if (pos == -1) {
                System.out.println("ERROR en plan");
                System.exit(-1);
            } else if (movimientos.size() > tam + 10) {

                System.out.println("ERROR 2 en plan.");// Movs:"+movimientos);
                System.exit(-1);
            }
            actual = new NPuzzle(lista.get(pos));
            lista.remove(pos);
        }
        return movimientos;
    }
}
