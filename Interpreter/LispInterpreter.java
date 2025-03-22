/**
 * Clase principal para el intérprete LISP.
 * Proporciona un menú interactivo que integra el lexer y el parser.
 */
import java.util.*;

public class LispInterpreter {
    private static final Scanner scanner = new Scanner(System.in);
    
    /**
     * Método principal del intérprete.
     * @param args A.
     */
    public static void main(String[] args) {
        boolean exit = false;
        String code = " "; // Código predeterminado
        
        System.out.println("=== Intérprete LISP ===");
        
        while (!exit) {
            printMenu();
            int option = readOption();
            
            switch (option) {
                case 1:
                    code = readCode();
                    System.out.println("Código actualizado.");
                    break;
                case 2:
                    System.out.println("\nCódigo actual:");
                    System.out.println(code);
                    System.out.println();
                    break;
                case 3:
                    runLexer(code);
                    break;
                case 4:
                    checkParentheses(code);
                    break;
                case 5:
                    runParser(code);
                    break;
                case 6:
                    runFullAnalysis(code);
                    break;
                case 0:
                    exit = true;
                    System.out.println("¡Orale!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Imprime el menú de opciones.
     */
    private static void printMenu() {
        System.out.println("\nMenú de opciones:");
        System.out.println("1. Ingresar nuevo código LISP");
        System.out.println("2. Mostrar código actual");
        System.out.println("3. Ejecutar análisis léxico (Lexer)");
        System.out.println("4. Verificar paréntesis balanceados");
        System.out.println("5. Ejecutar análisis sintáctico (Parser)");
        System.out.println("6. Ejecutar análisis completo (Lexer + Parser)");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    /**
     * Lee una opción del menú desde la entrada estándar.
     * @return Opción seleccionada.
     */
    private static int readOption() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Lee código LISP desde la entrada estándar.
     * @return Código LISP ingresado.
     */
    private static String readCode() {
        System.out.println("\nIngrese el código LISP (puede usar múltiples líneas).");
        System.out.println("Para terminar, ingrese una línea con solo 'fin':");
        
        StringBuilder codeBuilder = new StringBuilder();
        String line;
        
        while (true) {
            line = scanner.nextLine().toLowerCase();
            if (line.equals("fin")) {
                break;
            }
            codeBuilder.append(line).append("\n");
        }
        
        return codeBuilder.toString().trim();
    }
    
    /**
     * Ejecuta el análisis léxico en el código dado.
     * @param code Código LISP a analizar.
     */
    private static void runLexer(String code) {
        System.out.println("\n=== Análisis Léxico ===");
        
        LispLexer lexer = new LispLexer(code);
        List<Token> tokens = lexer.tokenize();
        
        System.out.println("Lista de tokens parseados:");
        System.out.println("---------------------------");
        for (Token token : tokens) {
            System.out.printf("Tipo: %-15s Valor: \"%s\"%n", token.getType(), token.getValue());
        }
        System.out.println("---------------------------");
        System.out.println("Total de tokens: " + tokens.size());
    }
    
    /**
     * Verifica si los paréntesis en el código están balanceados.
     * @param code Código LISP a verificar.
     */
    private static void checkParentheses(String code) {
        System.out.println("\n=== Verificación de Paréntesis ===");
        
        LispLexer lexer = new LispLexer(code);
        
        if (lexer.checkParentheses()) {
            System.out.println("Expresión válida: Paréntesis balanceados correctamente.");
        } else {
            System.out.println("Expresión inválida: Paréntesis desbalanceados.");
            System.out.println("Error en la posición: " + lexer.getErrorPosition());
        }
    }
    
    /**
     * Ejecuta el análisis sintáctico en el código dado.
     * @param code Código LISP a analizar.
     */
    private static void runParser(String code) {
        System.out.println("\n=== Análisis Sintáctico ===");
        
        try {
            LispLexer lexer = new LispLexer(code);
            List<Token> tokens = lexer.tokenize();
            
            LispParser parser = new LispParser(tokens);
            LispNode ast = parser.parse();
            
            System.out.println("Árbol de Sintaxis Abstracta (AST):");
            printAST(ast, 0);
        } catch (Exception e) {
            System.err.println("Error en el análisis: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta el análisis completo (léxico + sintáctico) en el código dado.
     * @param code Código LISP a analizar.
     */
    private static void runFullAnalysis(String code) {
        System.out.println("\n=== Análisis Completo (Léxico + Sintáctico) ===");
        
        try {
            // Paso 1: Análisis Léxico
            System.out.println("\n--- Paso 1: Análisis Léxico ---");
            LispLexer lexer = new LispLexer(code);
            List<Token> tokens = lexer.tokenize();
            
            System.out.println("Lista de tokens parseados:");
            System.out.println("---------------------------");
            for (Token token : tokens) {
                System.out.printf("Tipo: %-15s Valor: \"%s\"%n", token.getType(), token.getValue());
            }
            System.out.println("---------------------------");
            
            // Verificación de paréntesis
            if (!lexer.checkParentheses()) {
                System.out.println("¡Advertencia! Paréntesis desbalanceados.");
                System.out.println("Error en la posición: " + lexer.getErrorPosition());
                return;
            }
            
            // Paso 2: Análisis Sintáctico
            System.out.println("\n--- Paso 2: Análisis Sintáctico ---");
            LispParser parser = new LispParser(tokens);
            LispNode ast = parser.parse();
            
            System.out.println("Árbol de Sintaxis Abstracta (AST):");
            printAST(ast, 0);
            
            System.out.println("\nAnálisis completo finalizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error en el análisis: " + e.getMessage());
        }
    }
    
    /**
     * Imprime el AST con formato de indentación.
     * @param node Nodo a imprimir.
     * @param level Nivel de indentación.
     */
    private static void printAST(LispNode node, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        
        System.out.print(indent);
        System.out.print(node.getType());
        
        if (node.getValue() != null) {
            System.out.print(": " + node.getValue());
        }
        
        System.out.println();
        
        for (LispNode child : node.getChildren()) {
            printAST(child, level + 1);
        }
    }
}
