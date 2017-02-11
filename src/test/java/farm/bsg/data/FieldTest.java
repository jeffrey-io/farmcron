package farm.bsg.data;

import org.junit.Test;

public class FieldTest {

    @Test
    public void Coverage() {
        Field.UUID("id");
        Field.STRING("id");
        Field.BOOL("id");
        Field.TOKEN_STRING_LIST("id");
        Field.DATETIME("id");
        Field.NUMBER("id");
    }

}
