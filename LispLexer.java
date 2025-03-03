
/**
 * Analizador léxico (Lexer) para lenguaje LISP.
 * Convierte una cadena de entrada en una lista de tokens identificables, 
 * esto conforma el primer paso para nuestro intérprete completo.
 */
import java.util.*;
import java.util.regex.*;


public class LispLexer {
    private static final List<Map.Entry<Pattern, TokenType>> TOKEN_PATTERNS = List.of(
        /** Lista de patrones de tokens posibles junto con sus tipos correspondientes. */
        Map.entry(Pattern.compile("\\("), TokenType.INITPAREN),
        Map.entry(Pattern.compile("\\)"), TokenType.FINPAREN),
        Map.entry(Pattern.compile("[-+]?\\d+(\\.\\d+)?"), TokenType.NUMBER),
        Map.entry(Pattern.compile("\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\""), TokenType.STRING),
        Map.entry(Pattern.compile("[A-Za-z?!*-][A-Za-z0-9?!*-]*"), TokenType.SYMBOL),
        Map.entry(Pattern.compile("[-+*/]"), TokenType.OPERATOR),
        Map.entry(Pattern.compile(";.*"), TokenType.COMMENT),
        Map.entry(Pattern.compile("\\s+"), TokenType.WHITESPACE)
    );

    private static final Set<String> RESERVEDWORDSS = Set.of(
        /** Conjunto de palabras reservadas en LISP. Si son detectadas, se les dará un trato especial. */
        "defun", "define", "lambda", "if", "cond", "let", "setq", "quote",
        "progn", "loop", "return", "car", "cdr", "cons", "list", "eval"
    );

    /** Código fuente de entrada. */
    private final String input;
    /** Posición actual en la entrada. */
    private int position;

    /**
    * Constructor del lexer.
    * @param input Código LISP de entrada.
    */
    public LispLexer(String input) {
        this.input = input;
        this.position = 0;
    }

    /**
    * Tokeniza el código de entrada en una lista de tokens.
    * @return Lista de tokens extraídos del código.
    */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < input.length()) {
            boolean matched = false;
            for (var entry : TOKEN_PATTERNS) {
                Matcher matcher = entry.getKey().matcher(input);
                matcher.region(position, input.length());
                if (matcher.lookingAt()) {
                    String tokenText = matcher.group();
                    position += tokenText.length();
                    TokenType type = entry.getValue();

                    // Si es un símbolo, verificamos si es palabra clave
                    if (type == TokenType.SYMBOL && RESERVEDWORDSS.contains(tokenText)) {
                        type = TokenType.RESERVEDWORDS;
                    }

                    if (type != TokenType.WHITESPACE && type != TokenType.COMMENT) {
                        tokens.add(new Token(type, tokenText));
                    }
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                throw new RuntimeException("Error léxico en: " + input.substring(position));
            }
        }
        return tokens;
    }

    /**
     * Verifica si los paréntesis en el código están balanceados.
     * @return {@code true} si los paréntesis están balanceados, {@code false} en caso contrario.
     */
    public boolean checkParentheses() {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            }
            if (count < 0) {
                return false;
            }
        }
        return count == 0;
    }

    /**
     * Obtiene la posición del último token analizado en caso de error.
     * @return Posición del error en la entrada.
     */
    public int getErrorPosition() {
        return position;
    }
    // Métodos de prueba para evaluar el código de 
    /**
    * Método principal para probar el lexer.
    * @param args Argumentos de la línea de comandos (no utilizados).
    */

    public static void main(String[] args) {
        String code = "(define x 42) (lambda (y) (* y y))"; //Modificar a gusto
        LispLexer lispLexer = new LispLexer(code);
        List<Token> tokens = lispLexer.tokenize();
    
        // Imprime todos los tokens parseados con formato más estructurado
        System.out.println("Lista de tokens parseados:");
        System.out.println("---------------------------");
        for (Token token : tokens) {
            System.out.printf("Tipo: %-15s Valor: \"%s\"%n", token.getType(), token.getValue());
        }
        System.out.println("---------------------------");
    
        // Verificación de paréntesis balanceados
        if (lispLexer.checkParentheses()) {
            System.out.println("Expresión válida :D ");
        } else {
            System.out.println("Expresión inválida D: ");
            System.out.println("Error en la posición: " + lispLexer.getErrorPosition());
        }
    }
    
}
