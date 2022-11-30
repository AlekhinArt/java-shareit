package ru.practicum.shareit.resttests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.ErrorResponse;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ErrorResponseTest {
    @MockBean
    private ErrorResponse errorResponse;

    @Test
    void getError() {
        Mockito.when(errorResponse.getError())
                .thenReturn("error");
        Assertions.assertEquals(errorResponse.getError(), "error");
        Assertions.assertNotNull(errorResponse.getError());
    }


}
