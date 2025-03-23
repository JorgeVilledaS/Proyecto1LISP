import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispEvaluatorTest {

    
    // pruebas de operaciones simples

    @Test
    void testEvaluarMultiplicacion() {
        // Expresión: (* 3 4)
        LispLexer lexer = new LispLexer("(* 3 4)");
        List<Token> tokens = lexer.tokenize();
        LispParser parser = new LispParser(tokens);
        LispNode arbolSintaxisAbstracta;
        try {
            arbolSintaxisAbstracta = parser.parse();
        } catch (ParserException e) {
            throw new RuntimeException("Error de parsing: " + e.getMessage());
        }
        LispEvaluator evaluador = new LispEvaluator();
        Object resultado = evaluador.evaluate(arbolSintaxisAbstracta);
        assertEquals(12.0, resultado);
    }
    @Test
    void testEvaluarResta() {
        // Expresión: (- 10 4)
        LispLexer lexer = new LispLexer("(- 10 4)");
        List<Token> tokens = lexer.tokenize();
        LispParser parser = new LispParser(tokens);
        LispNode arbolSintaxisAbstracta;
        try {
            arbolSintaxisAbstracta = parser.parse();
        } catch (ParserException e) {
            throw new RuntimeException("Error de parsing: " + e.getMessage());
        }
        LispEvaluator evaluador = new LispEvaluator();
        Object resultado = evaluador.evaluate(arbolSintaxisAbstracta);
        assertEquals(6.0, resultado);
    }
    @Test
    void testEvaluarDefun() {
        // Expresión: (defun cuadrado (x) (* x x)) seguido de (cuadrado 5)
        LispLexer lexer = new LispLexer("(defun cuadrado (x) (* x x))");
        List<Token> tokens = lexer.tokenize();
        LispParser parser = new LispParser(tokens);
        LispNode arbolSintaxisAbstracta;
        try {
            arbolSintaxisAbstracta = parser.parse();
        } catch (ParserException e) {
            throw new RuntimeException("Error de parsing: " + e.getMessage());
        }
        LispEvaluator evaluador = new LispEvaluator();
        evaluador.evaluate(arbolSintaxisAbstracta);

        lexer = new LispLexer("(cuadrado 5)");
        tokens = lexer.tokenize();
        parser = new LispParser(tokens);
        try {
            arbolSintaxisAbstracta = parser.parse();
        } catch (ParserException e) {
            throw new RuntimeException("Error de parsing: " + e.getMessage());
        }
        Object resultado = evaluador.evaluate(arbolSintaxisAbstracta);
        assertEquals(25.0, resultado);
    }

    @Test
    void testEvaluarLet() {
        // Expresión: (let ((x 5)) (* x x))
        LispLexer lexer = new LispLexer("(let ((x 5)) (* x x))");
        List<Token> tokens = lexer.tokenize();
        LispParser parser = new LispParser(tokens);
        LispNode arbolSintaxisAbstracta;
        try {
            arbolSintaxisAbstracta = parser.parse();
        } catch (ParserException e) {
            throw new RuntimeException("Error de parsing: " + e.getMessage());
        }
        LispEvaluator evaluador = new LispEvaluator();
        Object resultado = evaluador.evaluate(arbolSintaxisAbstracta);
        assertEquals(25.0, resultado);
    }
       
}
