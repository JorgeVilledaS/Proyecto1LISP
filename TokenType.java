
// Clase que indica los tipos de token a los que puede asignarse los elementos de texto ingresados

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
