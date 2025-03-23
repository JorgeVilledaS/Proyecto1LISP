import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispLexerTest {
    @Test // Verificar que el programa identifique la posicion del error
    void testErrorPosition() {
        LispLexer lexer = new LispLexer("(+ 1 @)");
        assertThrows(RuntimeException.class, lexer::tokenize);
        assertEquals(5, lexer.getErrorPosition()); // La posición del error es donde pusimo el @
    }

    @Test //Prueba para asegurarnos que los parentesis desbalanceados sean identificados
    void testCheckParenthesesUnbalanced() {
        LispLexer lexer = new LispLexer("( ( ) ( )");
        assertFalse(lexer.checkParentheses());
    }
    
}
