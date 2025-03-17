
/**
 * Enum que define los tipos de nodos en el AST.
 */
enum NodeType {
    PROGRAM,    // Programa completo
    LIST,       // Lista (expresión)
    DEFINE,     // Definición (defun, define)
    LAMBDA,     // Expresión lambda
    IF,         // Expresión condicional if
    COND,       // Expresión condicional cond
    CLAUSE,     // Cláusula en una expresión cond
    LET,        // Expresión let
    BINDINGS,   // Lista de bindings en un let
    BINDING,    // Binding individual en un let
    SETQ,       // Asignación de variable
    QUOTE,      // Cita (quote)
    PARAMS,     // Lista de parámetros
    SYMBOL,     // Símbolo (variable, función)
    KEYWORD,    // Palabra clave
    NUMBER,     // Número
    STRING,     // Cadena de texto
    OPERATOR,   // Operador
    NIL         // Valor nulo
}
