// Clase que toqueniza el texto ingresado en el interprete de Lisp

import java.util.*;
import java.util.regex.*;


public class LispLexer {
    private static final List<Map.Entry<Pattern, TokenType>> TOKEN_PATTERNS = List.of(
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
        "defun", "define", "lambda", "if", "cond", "let", "setq", "quote",
        "progn", "loop", "return", "car", "cdr", "cons", "list", "eval"
    );

    private final String input;
    private int position;

    public LispLexer(String input) {
        this.input = input;
        this.position = 0;
    }

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

    public static void main(String[] args) {
        String code = "(define x 42) (lambda (y) (* y y))";
        LispLexer LispLexer = new LispLexer(code);
        List<Token> tokens = LispLexer.tokenize();
        tokens.forEach(System.out::println);
    }
}
