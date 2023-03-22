package com.company;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    @Autowired
    private EventsController controller;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {

        TelegramBot telegramBotSpy = spy(TelegramBot.class);
        doNothing().when(telegramBotSpy).sendToAll(isA(String.class));

        eventsService.telegramBot = telegramBotSpy;

        this.mockMvc.perform(post("/event").content("{\n" +
                        "  \"events\": [\n" +
                        "    {\n" +
                        "      \"id\": 48187,\n" +
                        "      \"nameEvent\": \"start\",\n" +
                        "      \"timeEvent\": \"2023-03-21 12:50:18.858\",\n" +
                        "      \"temperature\": 0,\n" +
                        "      \"processor\": 0,\n" +
                        "      \"usedMemory\": 0,\n" +
                        "      \"freeMemory\": 0,\n" +
                        "      \"sent\": false\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"deviceId\": 0\n" +
                        "}").contentType("application/json")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"eventsIdsDelivered\":[48187],\"periodSent\":1}"));


        ArgumentCaptor<String> capturedString = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSpy, times(2)).sendToAll(capturedString.capture());

        List<String> listValues = capturedString.getAllValues();
        assertThat(listValues.get(0)).isEqualTo(EventsService.FIRST_MESSAGE);
        assertThat(listValues.get(1)).isEqualTo(EventsService.MESSAGE_RECONNECTED);
    }
}
