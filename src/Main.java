import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("=== Árbol B+ ===");
        int orden;
        do {
            orden = leerEntero(entrada, "Ingrese el orden del árbol (mínimo 3): ");
        } while(orden < 3);

        ArbolBMas arbol = new ArbolBMas(orden);
        menu(entrada, arbol);
        entrada.close();
    }

    private static void menu(Scanner entrada, ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("------ MENÚ ------");
            System.out.println("1. Insertar una clave con su dato");
            System.out.println("2. Buscar una clave");
            System.out.println("3. Eliminar una clave");
            System.out.println("4. Recorrer un rango de elementos");
            System.out.println("5. Imprimir el árbol");
            System.out.println("0. Salir");
            opcion = leerEntero(entrada, "Seleccione una opción: ");
            System.out.println();

            switch(opcion) {
                case 1 -> {
                    int clave = leerEntero(entrada, "Clave (número entero): ");
                    System.out.print("Dato asociado: ");
                    String dato = entrada.nextLine();
                    if(arbol.insertar(clave, dato)) {
                        System.out.println("Clave " + clave + " insertada correctamente.");
                    } else {
                        System.out.println("La clave " + clave + " ya existe en el árbol.");
                    }
                }
                case 2 -> {
                    int clave = leerEntero(entrada, "Clave a buscar: ");
                    System.out.println(arbol.buscar(clave)
                            ? "La clave " + clave + " existe en el árbol."
                            : "La clave " + clave + " no existe en el árbol.");
                }
                case 3 -> {
                    int clave = leerEntero(entrada, "Clave a eliminar: ");
                    System.out.println(arbol.eliminar(clave)
                            ? "Clave " + clave + " eliminada correctamente."
                            : "La clave " + clave + " no existe en el árbol.");
                }
                case 4 -> {
                    int clave = leerEntero(entrada, "Clave inicial: ");
                    int n = leerEntero(entrada, "Cantidad de elementos a mostrar: ");
                    if(n <= 0) {
                        System.out.println("La cantidad debe ser mayor que cero.");
                    } else {
                        arbol.recorrerRango(clave, n);
                    }
                }
                case 5 -> arbol.imprimirArbol();
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while(opcion != 0);
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        while(true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(entrada.nextLine().trim());
            } catch(NumberFormatException e) {
                System.out.println("Entrada no válida. Debe ingresar un número entero.");
            }
        }
    }
}
