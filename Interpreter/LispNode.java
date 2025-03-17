import java.util.*;
/**
 * Representa un nodo en el Árbol de Sintaxis Abstracta (AST).
 */
class LispNode {
    private final NodeType type;
    private final String value;
    private final List<LispNode> children;
    
    /**
     * Constructor de un nodo.
     * @param type Tipo del nodo.
     * @param value Valor del nodo (puede ser null).
     */
    public LispNode(NodeType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }
    
    /**
     * Agrega un hijo al nodo.
     * @param child Nodo hijo.
     */
    public void addChild(LispNode child) {
        children.add(child);
    }
    
    /**
     * Obtiene el tipo del nodo.
     * @return Tipo del nodo.
     */
    public NodeType getType() {
        return type;
    }
    
    /**
     * Obtiene el valor del nodo.
     * @return Valor del nodo.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Obtiene la lista de hijos del nodo.
     * @return Lista de hijos.
     */
    public List<LispNode> getChildren() {
        return children;
    }
}

