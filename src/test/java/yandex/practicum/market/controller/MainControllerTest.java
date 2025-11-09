package yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest {

    @InjectMocks
    private MainController mainController;

    @Test
    public void redirectToMain_ShouldRedirectToItemsPage() throws Exception {
        // Настраиваем MockMvc для тестирования контроллера
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();

        // Выполняем запрос и проверяем результат
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())  // Проверяем что ответ - редирект
                .andExpect(redirectedUrl("/items")); // Проверяем URL редиректа
    }
}