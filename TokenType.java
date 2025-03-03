
/**
 * Enum que define los tipos de tokens posibles en la entrada LISP.
 */

public enum TokenType {
    INITPAREN,    // (
    FINPAREN,    // )
    SYMBOL,    // Símbolos
    RESERVEDWORDS,    // Palabras reservadas (defun, define, lambda, etc.)
    NUMBER,    // Números
    STRING,    // Cadenas de texto
    OPERATOR,  // +, -, *, /
    COMMENT,   // Comentarios
    WHITESPACE // Espacios en blanco (se ignoran)
}
