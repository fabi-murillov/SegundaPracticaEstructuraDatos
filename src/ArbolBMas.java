import java.util.ArrayList;

public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    public ArbolBMas(int orden) {
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    // Inserción de una llave en el árbol B+
    // Retorna false si la llave ya existe (no se permiten llaves repetidas)
    public boolean insertar(int clave, String dato) {
        if(buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó
        if(this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una llave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int llave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if(nodo.esHoja()) {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            nodo.getClaves().add(i + 1, llave);
            // El dato se guarda en la misma posición que su clave
            nodo.getDatos().add(i + 1, dato);
        } else {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la llave primero en el hijo correspondiente
            insertarRecursivo(hijo, llave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if(hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            // En nodos hoja, se mantiene la llave media en ambos lados
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Los datos se dividen igual que las claves
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, nodoLleno.getDatos().size())));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // Se enlaza el nuevo nodo en la lista de hojas
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador la primera llave del nuevo nodo
            int llaveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, llaveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
        else {
            // En nodos internos, la llave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for(int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Búsqueda de un nodo como parte de la interfaz pública del árbol
    public boolean buscar(int llave) {
        return buscarNodo(raiz, llave);
    }

    // Búsqueda recursiva privada de un nodo a partir de uno inicial
    private boolean buscarNodo(NodoArbolBMas nodo, int llave) {
        int i = 0;
        while(i < nodo.getClaves().size() && llave > nodo.getClaves().get(i)) {
            i++;
        }
        if(nodo.esHoja()) {
            // Los datos solo están en las hojas, por lo que solo allí se confirma que la llave existe
            return i < nodo.getClaves().size() && llave == nodo.getClaves().get(i);
        } else {
            // Una llave igual a un separador se encuentra en el hijo derecho
            if(i < nodo.getClaves().size() && llave == nodo.getClaves().get(i)) {
                i++;
            }
            return buscarNodo(nodo.getHijos().get(i), llave);
        }
    }

    // Eliminación
    // Elimina la llave (y su dato). Retorna false si la llave no existe
    public boolean eliminar(int clave) {
        if(!buscar(clave)) {
            return false;
        }

        eliminarRecursivo(raiz, clave);

        // Si la raíz interna quedó sin claves, su único hijo pasa a ser la nueva raíz
        if(!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().getFirst();
        }
        return true;
    }

    // Se elimina en la hoja y se rebalancea al regresar de la recursión
    private void eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if(nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            nodo.getClaves().remove(posicion);
            nodo.getDatos().remove(posicion);
            return;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);
        eliminarRecursivo(hijo, clave);

        // Evaluación de subdesbordamiento al retornar de la recursividad
        if(hijo.getClaves().size() < minimoClaves(hijo)) {
            rebalancear(nodo, i);
        }
    }

    // Índice del hijo por el que se debe descender para una llave dada
    private int indiceHijo(NodoArbolBMas nodo, int llave) {
        int i = 0;
        while(i < nodo.getClaves().size() && llave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Cantidad mínima de claves de un nodo
    private int minimoClaves(NodoArbolBMas nodo) {
        return nodo.esHoja() ? orden / 2 : (orden - 1) / 2;
    }

    // Rebalancea al hijo en la posición indice
    private void rebalancear(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = indice > 0 ? padre.getHijos().get(indice - 1) : null;
        NodoArbolBMas derecho = indice < padre.getHijos().size() - 1 ? padre.getHijos().get(indice + 1) : null;

        if(izquierdo != null && izquierdo.getClaves().size() > minimoClaves(izquierdo)) {
            prestarDeIzquierdo(padre, indice);
        } else if(derecho != null && derecho.getClaves().size() > minimoClaves(derecho)) {
            prestarDeDerecho(padre, indice);
        } else if(izquierdo != null) {
            fusionar(padre, indice - 1);
        } else {
            fusionar(padre, indice);
        }
    }

    // El hijo toma prestada una clave del hermano izquierdo
    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = padre.getHijos().get(indice - 1);
        int ultima = izquierdo.getClaves().size() - 1;

        if(hijo.esHoja()) {
            // Se mueve la última clave (con su dato) del hermano y el separador se actualiza
            hijo.getClaves().add(0, izquierdo.getClaves().remove(ultima));
            hijo.getDatos().add(0, izquierdo.getDatos().remove(ultima));
            padre.getClaves().set(indice - 1, hijo.getClaves().getFirst());
        } else {
            // Rotación: el separador baja al hijo y la última clave del hermano sube al padre
            hijo.getClaves().add(0, padre.getClaves().get(indice - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().remove(ultima));
            hijo.getHijos().add(0, izquierdo.getHijos().remove(izquierdo.getHijos().size() - 1));
        }
    }

    // El hijo toma prestada una clave del hermano derecho
    private void prestarDeDerecho(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if(hijo.esHoja()) {
            // Se mueve la primera clave (con su dato) del hermano y el separador se actualiza
            hijo.getClaves().add(derecho.getClaves().remove(0));
            hijo.getDatos().add(derecho.getDatos().remove(0));
            padre.getClaves().set(indice, derecho.getClaves().getFirst());
        } else {
            // Rotación: el separador baja al hijo y la primera clave del hermano sube al padre
            hijo.getClaves().add(padre.getClaves().get(indice));
            padre.getClaves().set(indice, derecho.getClaves().remove(0));
            hijo.getHijos().add(derecho.getHijos().remove(0));
        }
    }

    // Fusiona el hijo en la posición indice + 1 dentro del hijo en la posición indice
    private void fusionar(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if(izquierdo.esHoja()) {
            // El separador no baja: solo se concatenan claves y datos, y se salta la hoja eliminada en el enlace
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos, el separador baja del padre para unir ambos nodos
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        // El padre pierde el separador y el hijo derecho
        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }

    // Descenso desde la raíz hasta la hoja donde está la llave
    private NodoArbolBMas buscarHoja(int llave) {
        NodoArbolBMas nodo = raiz;
        while(!nodo.esHoja()) {
            nodo = nodo.getHijos().get(indiceHijo(nodo, llave));
        }
        return nodo;
    }

    // Recorrido por rango (Debe mostrar los datos correspondientes al rango recuperado.)
    public void recorrerRango(int clave, int n) {
        NodoArbolBMas hoja = buscarHoja(clave);

        // Posición de la primera clave >= clave dentro de la hoja encontrada
        int posicion = 0;
        while(posicion < hoja.getClaves().size() && hoja.getClaves().get(posicion) < clave) {
            posicion++;
        }

        int impresos = 0;
        while(hoja != null && impresos < n) {
            if(posicion >= hoja.getClaves().size()) {
                // Se terminó la hoja actual y se pasa a la siguiente
                hoja = hoja.getSiguiente();
                posicion = 0;
                continue;
            }
            System.out.println("  Clave " + hoja.getClaves().get(posicion) + " -> " + hoja.getDatos().get(posicion));
            posicion++;
            impresos++;
        }

        if(impresos == 0) {
            System.out.println("  No hay elementos con clave mayor o igual a " + clave + ".");
        }
    }
}