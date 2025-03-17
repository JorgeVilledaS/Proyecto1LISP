
/**
 * Parser para lenguaje LISP.
 * Convierte una lista de tokens en un árbol de sintaxis abstracta (AST).
 */
import java.util.*;

public class LispParser {
    /** Lista de tokens generada por el lexer. */
    private final List<Token> tokens;
    /** Posición actual en la lista de tokens. */
    private int position;

    /**
     * Constructor del parser.
     * @param tokens Lista de tokens generada por el lexer.
     */
    public LispParser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    /**
     * Inicia el proceso de parsing.
     * @return Nodo raíz del AST resultante.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    public LispNode parse() throws ParserException {
        if (tokens.isEmpty()) {
            return new LispNode(NodeType.NIL, null);
        }
        
        LispNode program = new LispNode(NodeType.PROGRAM, null);
        
        while (position < tokens.size()) {
            program.addChild(parseExpression());
        }
        
        return program;
    }

    /**
     * Analiza una expresión LISP.
     * @return Nodo del AST que representa la expresión.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseExpression() throws ParserException {
        if (position >= tokens.size()) {
            throw new ParserException("Fin inesperado de entrada");
        }
        
        Token token = tokens.get(position);
        position++;
        
        switch (token.getType()) {
            case NUMBER:
                return new LispNode(NodeType.NUMBER, token.getValue());
            case STRING:
                return new LispNode(NodeType.STRING, token.getValue());
            case SYMBOL:
                return new LispNode(NodeType.SYMBOL, token.getValue());
            case RESERVEDWORDS:
                return new LispNode(NodeType.KEYWORD, token.getValue());
            case OPERATOR:
                return new LispNode(NodeType.OPERATOR, token.getValue());
            case INITPAREN:
                return parseList();
            case FINPAREN:
                position--; // Retrocedemos para que el paréntesis sea manejado por el método que llamó
                throw new ParserException("Paréntesis de cierre inesperado");
            default:
                throw new ParserException("Token inesperado: " + token.getType());
        }
    }

    /**
     * Analiza una lista LISP (expresión entre paréntesis).
     * @return Nodo del AST que representa la lista.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseList() throws ParserException {
        LispNode listNode = new LispNode(NodeType.LIST, null);
        
        // Lista vacía
        if (position < tokens.size() && tokens.get(position).getType() == TokenType.FINPAREN) {
            position++; // Consumimos el paréntesis de cierre
            return listNode;
        }
        
        // Caso especial para formas especiales (defun, define, lambda, etc.)
        if (position < tokens.size() && 
            (tokens.get(position).getType() == TokenType.RESERVEDWORDS || 
             tokens.get(position).getType() == TokenType.SYMBOL)) {
            
            Token firstToken = tokens.get(position);
            String value = firstToken.getValue();
            
            if (value.equals("defun") || value.equals("define")) {
                return parseDefine();
            } else if (value.equals("lambda")) {
                return parseLambda();
            } else if (value.equals("if")) {
                return parseIf();
            } else if (value.equals("cond")) {
                return parseCond();
            } else if (value.equals("let")) {
                return parseLet();
            } else if (value.equals("setq")) {
                return parseSetq();
            } else if (value.equals("quote")) {
                return parseQuote();
            }
        }
        
        // Lista normal (llamada a función o expresión)
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            listNode.addChild(parseExpression());
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        // Consumimos el paréntesis de cierre
        position++;
        
        return listNode;
    }

    /**
     * Analiza una definición de función o variable (defun, define).
     * @return Nodo del AST que representa la definición.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseDefine() throws ParserException {
        Token defineToken = tokens.get(position);
        position++; // Consumimos defun/define
        
        LispNode defineNode = new LispNode(NodeType.DEFINE, defineToken.getValue());
        
        // Nombre de la función o variable
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.SYMBOL) {
            throw new ParserException("Se esperaba un símbolo después de " + defineToken.getValue());
        }
        
        LispNode nameNode = new LispNode(NodeType.SYMBOL, tokens.get(position).getValue());
        defineNode.addChild(nameNode);
        position++;
        
        // Si es defun, esperamos una lista de parámetros
        if (defineToken.getValue().equals("defun")) {
            if (position >= tokens.size() || tokens.get(position).getType() != TokenType.INITPAREN) {
                throw new ParserException("Se esperaba una lista de parámetros");
            }
            
            position++; // Consumimos el paréntesis de apertura
            
            LispNode paramsNode = new LispNode(NodeType.PARAMS, null);
            
            while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
                if (tokens.get(position).getType() != TokenType.SYMBOL) {
                    throw new ParserException("Se esperaba un símbolo como parámetro");
                }
                
                paramsNode.addChild(new LispNode(NodeType.SYMBOL, tokens.get(position).getValue()));
                position++;
            }
            
            if (position >= tokens.size()) {
                throw new ParserException("Se esperaba paréntesis de cierre para lista de parámetros");
            }
            
            position++; // Consumimos el paréntesis de cierre
            defineNode.addChild(paramsNode);
        }
        
        // Cuerpo de la función o valor de la variable
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            defineNode.addChild(parseExpression());
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return defineNode;
    }

    /**
     * Analiza una expresión lambda.
     * @return Nodo del AST que representa la expresión lambda.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseLambda() throws ParserException {
        position++; // Consumimos lambda
        
        LispNode lambdaNode = new LispNode(NodeType.LAMBDA, "lambda");
        
        // Lista de parámetros
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.INITPAREN) {
            throw new ParserException("Se esperaba una lista de parámetros");
        }
        
        position++; // Consumimos el paréntesis de apertura
        
        LispNode paramsNode = new LispNode(NodeType.PARAMS, null);
        
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            if (tokens.get(position).getType() != TokenType.SYMBOL) {
                throw new ParserException("Se esperaba un símbolo como parámetro");
            }
            
            paramsNode.addChild(new LispNode(NodeType.SYMBOL, tokens.get(position).getValue()));
            position++;
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre para lista de parámetros");
        }
        
        position++; // Consumimos el paréntesis de cierre
        lambdaNode.addChild(paramsNode);
        
        // Cuerpo de la función
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            lambdaNode.addChild(parseExpression());
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return lambdaNode;
    }

    /**
     * Analiza una expresión if.
     * @return Nodo del AST que representa la expresión if.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseIf() throws ParserException {
        position++; // Consumimos if
        
        LispNode ifNode = new LispNode(NodeType.IF, "if");
        
        // Condición
        ifNode.addChild(parseExpression());
        
        // Rama then
        ifNode.addChild(parseExpression());
        
        // Rama else (opcional)
        if (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            ifNode.addChild(parseExpression());
        }
        
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.FINPAREN) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return ifNode;
    }

    /**
     * Analiza una expresión cond.
     * @return Nodo del AST que representa la expresión cond.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseCond() throws ParserException {
        position++; // Consumimos cond
        
        LispNode condNode = new LispNode(NodeType.COND, "cond");
        
        // Cláusulas
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            if (tokens.get(position).getType() != TokenType.INITPAREN) {
                throw new ParserException("Se esperaba una cláusula (lista)");
            }
            
            position++; // Consumimos el paréntesis de apertura
            
            LispNode clauseNode = new LispNode(NodeType.CLAUSE, null);
            
            // Condición
            clauseNode.addChild(parseExpression());
            
            // Cuerpo
            while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
                clauseNode.addChild(parseExpression());
            }
            
            if (position >= tokens.size()) {
                throw new ParserException("Se esperaba paréntesis de cierre para cláusula");
            }
            
            position++; // Consumimos el paréntesis de cierre
            condNode.addChild(clauseNode);
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return condNode;
    }

    /**
     * Analiza una expresión let.
     * @return Nodo del AST que representa la expresión let.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseLet() throws ParserException {
        position++; // Consumimos let
        
        LispNode letNode = new LispNode(NodeType.LET, "let");
        
        // Lista de bindings
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.INITPAREN) {
            throw new ParserException("Se esperaba una lista de bindings");
        }
        
        position++; // Consumimos el paréntesis de apertura
        
        LispNode bindingsNode = new LispNode(NodeType.BINDINGS, null);
        
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            if (tokens.get(position).getType() != TokenType.INITPAREN) {
                throw new ParserException("Se esperaba un binding (lista)");
            }
            
            position++; // Consumimos el paréntesis de apertura
            
            LispNode bindingNode = new LispNode(NodeType.BINDING, null);
            
            // Variable
            if (position >= tokens.size() || tokens.get(position).getType() != TokenType.SYMBOL) {
                throw new ParserException("Se esperaba un símbolo en binding");
            }
            
            bindingNode.addChild(new LispNode(NodeType.SYMBOL, tokens.get(position).getValue()));
            position++;
            
            // Valor
            bindingNode.addChild(parseExpression());
            
            if (position >= tokens.size() || tokens.get(position).getType() != TokenType.FINPAREN) {
                throw new ParserException("Se esperaba paréntesis de cierre para binding");
            }
            
            position++; // Consumimos el paréntesis de cierre
            bindingsNode.addChild(bindingNode);
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre para lista de bindings");
        }
        
        position++; // Consumimos el paréntesis de cierre
        letNode.addChild(bindingsNode);
        
        // Cuerpo del let
        while (position < tokens.size() && tokens.get(position).getType() != TokenType.FINPAREN) {
            letNode.addChild(parseExpression());
        }
        
        if (position >= tokens.size()) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return letNode;
    }

/**
     * Analiza una expresión setq.
     * @return Nodo del AST que representa la expresión setq.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseSetq() throws ParserException {
        position++; // Consumimos setq
        
        LispNode setqNode = new LispNode(NodeType.SETQ, "setq");
        
        // Variable
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.SYMBOL) {
            throw new ParserException("Se esperaba un símbolo (variable) después de setq");
        }
        
        setqNode.addChild(new LispNode(NodeType.SYMBOL, tokens.get(position).getValue()));
        position++;
        
        // Valor
        setqNode.addChild(parseExpression());
        
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.FINPAREN) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return setqNode;
    }

    /**
     * Analiza una expresión quote.
     * @return Nodo del AST que representa la expresión quote.
     * @throws ParserException Si se encuentra un error de sintaxis.
     */
    private LispNode parseQuote() throws ParserException {
        position++; // Consumimos quote
        
        LispNode quoteNode = new LispNode(NodeType.QUOTE, "quote");
        
        // Expresión a citar
        quoteNode.addChild(parseExpression());
        
        if (position >= tokens.size() || tokens.get(position).getType() != TokenType.FINPAREN) {
            throw new ParserException("Se esperaba paréntesis de cierre");
        }
        
        position++; // Consumimos el paréntesis de cierre
        
        return quoteNode;
    }

    /**
     * Método principal para probar el parser.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        String code = "(define x 42) (defun square (y) (* y y))";
        LispLexer lexer = new LispLexer(code);
        List<Token> tokens = lexer.tokenize();
        
        try {
            LispParser parser = new LispParser(tokens);
            LispNode ast = parser.parse();
            System.out.println("Árbol de Sintaxis Abstracta (AST):");
            printAST(ast, 0);
        } catch (ParserException e) {
            System.err.println("Error de parseo: " + e.getMessage());
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

/**
 * Excepción específica para errores durante el parseo.
 */
class ParserException extends Exception {
    public ParserException(String message) {
        super(message);
    }
}



