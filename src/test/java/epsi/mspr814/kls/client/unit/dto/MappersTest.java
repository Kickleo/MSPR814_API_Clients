package epsi.mspr814.kls.client.unit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import epsi.mspr814.kls.client.dto.Mappers;
import org.junit.jupiter.api.Test;

class MappersTest {

    @Test
    void testPrivateConstructorThrowsUnsupportedOperationException() throws Exception {
        // Récupérer le constructeur privé de Mappers
        Constructor<Mappers> constructor = Mappers.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Vérifier que l'invocation du constructeur lance une InvocationTargetException
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // L'exception réelle doit être une UnsupportedOperationException avec le message attendu
        Throwable cause = exception.getCause();
        assertEquals(UnsupportedOperationException.class, cause.getClass());
        assertEquals("Utility class should not be instantiated", cause.getMessage());
    }
}