/**
 * Clase que evalúa finalmente la expresión para el intérprete LISP.
 * Proporciona la lógica para cada caso específico sin usar Environment ni clases de valores.
 */

import java.util.*;
import java.util.function.Function;

public class LispEvaluator {
    // Usamos Map<String, Object> como si fuera un ambiente (No entiendo bien que es un environment aun, entonces mejor usar Map)
    private final Map<String, Object> globalEnv;
    
    /**
     * Constructor del evaluador.
     * Inicializa el entorno global y define funciones predeterminadas.
     */
    public LispEvaluator() {
        this.globalEnv = new HashMap<>();
        setupPredefinedFunctions();
    }
    
    /**
     * Configura las funciones predefinidas del lenguaje LISP.
     * Las funciones se implementan como objetos Function.
     */
    private void setupPredefinedFunctions() {
        // Operaciones aritméticas básicas
        globalEnv.put("+", (Function<List<Object>, Object>) this::add);
        globalEnv.put("-", (Function<List<Object>, Object>) this::subtract);
        globalEnv.put("*", (Function<List<Object>, Object>) this::multiply);
        globalEnv.put("/", (Function<List<Object>, Object>) this::divide);
        
        // Operaciones de comparación
        globalEnv.put("=", (Function<List<Object>, Object>) this::equals);
        globalEnv.put("<", (Function<List<Object>, Object>) this::lessThan);
        globalEnv.put(">", (Function<List<Object>, Object>) this::greaterThan);
        
        // Funciones de lista
        globalEnv.put("car", (Function<List<Object>, Object>) this::car);
        globalEnv.put("cdr", (Function<List<Object>, Object>) this::cdr);
        globalEnv.put("cons", (Function<List<Object>, Object>) this::cons);
        globalEnv.put("list", (Function<List<Object>, Object>) args -> args);
        
        // Funciones lógicas
        globalEnv.put("and", (Function<List<Object>, Object>) this::and);
        globalEnv.put("or", (Function<List<Object>, Object>) this::or);
        globalEnv.put("not", (Function<List<Object>, Object>) this::not);
        
        // Funciones de tipo
        globalEnv.put("null?", (Function<List<Object>, Object>) this::isNull);
        globalEnv.put("number?", (Function<List<Object>, Object>) this::isNumber);
        globalEnv.put("symbol?", (Function<List<Object>, Object>) this::isSymbol);
        globalEnv.put("list?", (Function<List<Object>, Object>) this::isList);
    }
    
        /**
     * Evalúa si un valor es considerado "verdadero" en LISP.
     * @param value Valor a evaluar
     * @return true si el valor es "verdadero", false en caso contrario
     */
    private boolean EsVerdadero(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof List) {
            return !((List<?>) value).isEmpty();
        }
        if (value instanceof Double) {
            return ((Double) value) != 0.0;
        }
        return true;
    }

    private Object add(List<Object> args) {
        double result = 0;
        for (Object arg : args) {
            if (arg instanceof Double) {
                result += (Double) arg;
            } else {
                throw new RuntimeException("Se esperaba un número: " + arg);
            }
        }
        return result;
    }

    public Object subtract(List<Object> args) {
        if (args.isEmpty()) {
            throw new RuntimeException("La función '-' requiere al menos un argumento.");
        }
        double result = ((Number) args.get(0)).doubleValue();
        for (int i = 1; i < args.size(); i++) {
            result -= ((Number) args.get(i)).doubleValue();
        }
        return result;
    }

    public Object multiply(List<Object> args) {
        double product = 1;
        for (Object arg : args) {
            product *= ((Number) arg).doubleValue();
        }
        return product;
    }

    private Object divide(List<Object> args) {
        if (args.isEmpty()) {
            return 1.0;
        }
        
        if (!(args.get(0) instanceof Double)) {
            throw new RuntimeException("Se esperaba un número: " + args.get(0));
        }
        
        double result = (Double) args.get(0);
        
        if (args.size() == 1) {
            return 1.0 / result;  
        }
        
        for (int i = 1; i < args.size(); i++) {
            Object arg = args.get(i);
            if (arg instanceof Double) {
                double divisor = (Double) arg;
                if (divisor == 0) {
                    throw new RuntimeException("División por cero");
                }
                result /= divisor;
            } else {
                throw new RuntimeException("Se esperaba un número: " + arg);
            }
        }
        
        return result;
    }
    
    private Object equals(List<Object> args) {
        if (args.size() < 2) {
            return true;  
        }
        
        Object first = args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (!Objects.equals(first, args.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    private Object lessThan(List<Object> args) {
        if (args.size() < 2) {
            return true;  // Por definición
        }
        
        for (int i = 0; i < args.size() - 1; i++) {
            if (!(args.get(i) instanceof Double) || !(args.get(i + 1) instanceof Double)) {
                throw new RuntimeException("Se esperaban números para comparación");
            }
            
            if ((Double) args.get(i) >= (Double) args.get(i + 1)) {
                return false;
            }
        }
        
        return true;
    }
    
    private Object greaterThan(List<Object> args) {
        if (args.size() < 2) {
            return true;  // Por definición
        }
        
        for (int i = 0; i < args.size() - 1; i++) {
            if (!(args.get(i) instanceof Double) || !(args.get(i + 1) instanceof Double)) {
                throw new RuntimeException("Se esperaban números para comparación");
            }
            
            if ((Double) args.get(i) <= (Double) args.get(i + 1)) {
                return false;
            }
        }
        
        return true;
    }
    
    private Object car(List<Object> args) {
        if (args.size() != 1 || !(args.get(0) instanceof List)) {
            throw new RuntimeException("car requiere una lista como argumento");
        }
        
        List<?> list = (List<?>) args.get(0);
        if (list.isEmpty()) {
            return null;
        }
        
        return list.get(0);
    }
    
    private Object cdr(List<Object> args) {
        if (args.size() != 1 || !(args.get(0) instanceof List)) {
            throw new RuntimeException("cdr requiere una lista como argumento");
        }
        
        List<?> list = (List<?>) args.get(0);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(list.subList(1, list.size()));
    }
    
    private Object cons(List<Object> args) {
        if (args.size() != 2 || !(args.get(1) instanceof List)) {
            throw new RuntimeException("cons requiere dos argumentos, el segundo debe ser una lista");
        }
        
        Object first = args.get(0);
        @SuppressWarnings("unchecked")
        List<Object> rest = new ArrayList<>((List<Object>) args.get(1));
        
        rest.add(0, first);
        return rest;
    }
    
    private Object and(List<Object> args) {
        if (args.isEmpty()) {
            return true;
        }
        
        Object result = true;
        for (Object arg : args) {
            if (!EsVerdadero(arg)) {
                return false;
            }
            result = arg;  
        }
        
        return result;
    }
    
    private Object or(List<Object> args) {
        if (args.isEmpty()) {
            return false;
        }
        
        for (Object arg : args) {
            if (EsVerdadero(arg)) {
                return arg;  // El primer valor verdadero
            }
        }
        
        return false;
    }
    
    private Object not(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("not requiere exactamente un argumento");
        }
        
        return !EsVerdadero(args.get(0));
    }
    
    private Object isNull(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("null? requiere exactamente un argumento");
        }
        
        return args.get(0) == null || 
               (args.get(0) instanceof List && ((List<?>) args.get(0)).isEmpty());
    }
    
    private Object isNumber(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("number? requiere exactamente un argumento");
        }
        
        return args.get(0) instanceof Double;
    }
    
    private Object isSymbol(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("symbol? requiere exactamente un argumento");
        }
        
        return args.get(0) instanceof String && !(((String) args.get(0)).startsWith("\""));
    }
    
    private Object isList(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("list? requiere exactamente un argumento");
        }
        
        return args.get(0) instanceof List;
    }
    
    /**
     * Evalúa una expresión LISP representada como un nodo del AST.
     * @param ast Nodo raíz del AST
     * @return Resultado de la evaluación
     */
    public Object evaluate(LispNode ast) {
        return evaluate(ast, new HashMap<>());
    }


    /**
     * Método principal para evaluar un nodo del AST.
     * @param ast Nodo a evaluar
     * @param env Entorno de evaluación (mapa de símbolos a valores)
     * @return Resultado de la evaluación
     */
    public Object evaluate(LispNode ast, Map<String, Object> env) {
        if (ast == null) {
            return null;
        }
        
        switch (ast.getType()) {
            case NUMBER:
                return Double.parseDouble(ast.getValue());
                
            case STRING:
                return ast.getValue();
                
            case SYMBOL:
                String symbol = ast.getValue();
                if (env.containsKey(symbol)) {
                    return env.get(symbol);
                }
                if (globalEnv.containsKey(symbol)) {
                    return globalEnv.get(symbol);
                }
                throw new RuntimeException("Símbolo no definido: " + symbol);
                
            case LIST:
                // Lista vacía
                if (ast.getChildren().isEmpty()) {
                    return new ArrayList<>();
                }
                
                // Evaluar una lista como aplicación de función o forma especial
                return evaluateList(ast, env);
                
            case PROGRAM:
                // Esto es la secuencia de expresiones, devolvemos el resultado de la última
                Object result = null;
                for (LispNode expr : ast.getChildren()) {
                    result = evaluate(expr, env);
                }
                return result;
                
            default:
                throw new RuntimeException("No se puede evaluar: " + ast.getType());
        }
    }
    
    /**
     * Evalúa una lista, que puede ser una forma especial o una llamada a función.
     * @param listNode Nodo de tipo LIST
     * @param env Entorno de evaluación
     * @return Resultado de la evaluación
     */
    private Object evaluateList(LispNode listNode, Map<String, Object> env) {
        List<LispNode> children = listNode.getChildren();
        if (children.isEmpty()) {
            return new ArrayList<>();
        }
        
        LispNode first = children.get(0);
        String operator = first.getValue();
        
        // Formas especiales que no evalúan todos sus argumentos
        if (first.getType() == NodeType.SYMBOL || first.getType() == NodeType.KEYWORD) {
            switch (operator) {
                case "quote":
                    return quoteExpression(children.get(1));
                    
                case "define":
                case "defun":
                    return evaluateDefine(listNode, env);
                    
                case "if":
                    return evaluateIf(listNode, env);
                    
                case "cond":
                    return evaluateCond(listNode, env);
                    
                case "lambda":
                    return evaluateLambda(listNode, env);
                    
                case "let":
                    return evaluateLet(listNode, env);
                    
                case "setq":
                    return evaluateSetq(listNode, env);
            }
        }
        
        // Llamada a función: evaluar el operador y los argumentos
        return evaluateFunctionCall(listNode, env);
    }

    /**
     * Evalúa una llamada a función.
     * @param callNode Nodo que representa la llamada a función
     * @param env Entorno de evaluación
     * @return Resultado de la llamada a función
     */
    private Object evaluateFunctionCall(LispNode callNode, Map<String, Object> env) {
        List<LispNode> children = callNode.getChildren();
        
        // Evaluar el operador (primer elemento de la lista)
        Object operator = evaluate(children.get(0), env);
        
        // Evaluar los argumentos
        List<Object> args = new ArrayList<>();
        for (int i = 1; i < children.size(); i++) {
            args.add(evaluate(children.get(i), env));
        }
        
        // Aplicar la función
        if (operator instanceof Function) {
            @SuppressWarnings("unchecked")
            Function<List<Object>, Object> func = (Function<List<Object>, Object>) operator;
            return func.apply(args);
        } else {
            throw new RuntimeException("No se puede llamar como función: " + operator);
        }
    }
    
    /**
     * Convierte un nodo del AST a un valor sin evaluarlo (para quote).
     * @param node Nodo a convertir
     * @return Representación del nodo como valor
     */
    private Object quoteExpression(LispNode node) {
        switch (node.getType()) {
            case NUMBER:
                return Double.parseDouble(node.getValue());
                
            case STRING:
                return node.getValue();
                
            case SYMBOL:
                return node.getValue();  // Como símbolo, no como valor
                
            case LIST:
                List<Object> result = new ArrayList<>();
                for (LispNode child : node.getChildren()) {
                    result.add(quoteExpression(child));
                }
                return result;
                
            default:
                return null;
        }
    }
    
    /**
     * Evalúa una definición (define/defun).
     * @param defineNode Nodo que representa la definición
     * @param env Entorno de evaluación
     * @return Resultado de la definición (generalmente el símbolo definido)
     */
    private Object evaluateDefine(LispNode defineNode, Map<String, Object> env) {
        List<LispNode> children = defineNode.getChildren();
        
        // Obtener el nombre (símbolo) para la definición
        LispNode nameNode = children.get(1);
        String name = nameNode.getValue();
        
        // Distinguir entre definición de función y variable
        if (children.get(0).getValue().equals("defun")) {
            // Definición de función (defun name (params) body)
            LispNode paramsNode = children.get(2);
            List<String> params = new ArrayList<>();
            
            for (LispNode param : paramsNode.getChildren()) {
                params.add(param.getValue());
            }
            
            // Recopilar el cuerpo de la función (puede ser múltiples expresiones)
            List<LispNode> bodyNodes = new ArrayList<>();
            for (int i = 3; i < children.size(); i++) {
                bodyNodes.add(children.get(i));
            }
            
            // Crear función como un lambda
            Function<List<Object>, Object> function = args -> {
                Map<String, Object> functionEnv = new HashMap<>(env);
                
                // Vincular argumentos a parámetros
                for (int i = 0; i < params.size(); i++) {
                    if (i < args.size()) {
                        functionEnv.put(params.get(i), args.get(i));
                    } else {
                        functionEnv.put(params.get(i), null); // parámetro sin valor
                    }
                }
                
                // Evaluar el cuerpo de la función
                Object result = null;
                for (LispNode bodyNode : bodyNodes) {
                    result = evaluate(bodyNode, functionEnv);
                }
                return result;
            };
            
            globalEnv.put(name, function);
            return name;
            
        } else {
            // Definición de variable (define name value)
            Object value = evaluate(children.get(2), env);
            globalEnv.put(name, value);
            return name;
        }
    }


}