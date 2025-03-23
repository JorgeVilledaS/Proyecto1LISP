import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispParserTest {
    @Test// verifivar que el parser identifique que no se cerro la expresion
    void testParseErrorMissingClosingParen() {
        List<Token> tokens = List.of(
                new Token(TokenType.INITPAREN, "("),
                new Token(TokenType.OPERATOR, "+"),
                new Token(TokenType.NUMBER, "7"),
                new Token(TokenType.NUMBER, "6")
        
        );
    }

    

}
