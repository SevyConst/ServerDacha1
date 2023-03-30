package com.company;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApplicationTests {

    @Autowired
    private EventsController controller;

    @Autowired
    private EventsService eventsService;

    @Autowired
    CheckingLastDate checkingLastDate;

    @MockBean
    TelegramBot telegramBotMock;

    @Autowired
    private MockMvc mockMvc;

    private final static Integer PERIOD_PING = 2;
    private final static Integer COEFFICIENT_NOTIFICATION = 2;

    @Test
    public void contextLoads() {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    public void onlyStartTest() throws Exception {

        doNothing().when(telegramBotMock).sendToAll(isA(String.class));

        eventsService.applicationProperties.setPeriodPing(PERIOD_PING);

        this.mockMvc.perform(post("/event").content("""
                        {
                          "events": [
                            {
                              "id": 48187,
                              "nameEvent": "start",
                              "timeEvent": "2023-03-21 12:50:18.858",
                              "temperature": 0,
                              "processor": 0,
                              "usedMemory": 0,
                              "freeMemory": 0,
                              "sent": false
                            }
                          ],
                          "deviceId": 0
                        }""").contentType("application/json")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"eventsIdsDelivered\":[48187],\"periodSent\":"
                        + PERIOD_PING +"}"));


        ArgumentCaptor<String> capturedString = ArgumentCaptor.forClass(String.class);
        verify(telegramBotMock).sendToAll(capturedString.capture());
        assertThat(capturedString.getValue()).isEqualTo(EventsService.MESSAGE_FIRST
                + "2023-03-21 12:50:18;\n" + EventsService.MESSAGE_RECONNECTED);
    }

    @Test
    public void PiIsOff2TimesTest() throws Exception {
        doNothing().when(telegramBotMock).sendToAll(isA(String.class));

        eventsService.applicationProperties.setPeriodPing(PERIOD_PING);


        this.mockMvc.perform(post("/event").content("""
                        {
                           "events": [
                             {
                               "id": 49266,
                               "nameEvent": "start",
                               "timeEvent": "2023-03-27 13:32:59.756",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             },
                             {
                               "id": 49267,
                               "nameEvent": "ping",
                               "timeEvent": "2023-03-27 13:33:00.767",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             },
                             {
                               "id": 49268,
                               "nameEvent": "start",
                               "timeEvent": "2023-03-27 13:33:10.783",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             },
                             {
                               "id": 49269,
                               "nameEvent": "ping",
                               "timeEvent": "2023-03-27 13:33:11.803",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             },
                             {
                               "id": 49270,
                               "nameEvent": "ping",
                               "timeEvent": "2023-03-27 13:33:12.495",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             },
                             {
                               "id": 49271,
                               "nameEvent": "start",
                               "timeEvent": "2023-03-27 14:15:17.473",
                               "temperature": 0,
                               "processor": 0,
                               "usedMemory": 0,
                               "freeMemory": 0,
                               "sent": true
                             }
                           ],
                           "deviceId": 0
                         }""").contentType("application/json")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"eventsIdsDelivered\":[49266,49267,49268,49269,49270,49271],\"periodSent\":"
                                + PERIOD_PING +"}"));


        ArgumentCaptor<String> capturedString = ArgumentCaptor.forClass(String.class);
        verify(telegramBotMock).sendToAll(capturedString.capture());
        assertThat(capturedString.getValue()).isEqualTo(EventsService.MESSAGE_FIRST
                + "2023-03-27 13:32:59;\n"

                + EventsService.MESSAGE_ELECTRICITY_OFF
                + "2023-03-27 13:33:00"
                + EventsService.MESSAGE_UNTIL
                + "2023-03-27 13:33:10;\n"

                + EventsService.MESSAGE_ELECTRICITY_OFF
                + "2023-03-27 13:33:12"
                + EventsService.MESSAGE_UNTIL
                + "2023-03-27 14:15:17;\n"

                + EventsService.MESSAGE_RECONNECTED);
    }

    @Test
    public void offlineTest() throws Exception{
        doNothing().when(telegramBotMock).sendToAll(isA(String.class));

        checkingLastDate.setPeriodPing(PERIOD_PING);
        checkingLastDate.setCoefficientNotification(COEFFICIENT_NOTIFICATION);

        eventsService.applicationProperties.setPeriodPing(PERIOD_PING);
        eventsService.applicationProperties.setCoefficientNotification(COEFFICIENT_NOTIFICATION);

        this.mockMvc.perform(post("/event").content("""
                        {
                          "events": [
                            {
                              "id": 48187,
                              "nameEvent": "start",
                              "timeEvent": "2023-03-21 12:50:18.858",
                              "temperature": 0,
                              "processor": 0,
                              "usedMemory": 0,
                              "freeMemory": 0,
                              "sent": false
                            }
                          ],
                          "deviceId": 0
                        }""").contentType("application/json")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"eventsIdsDelivered\":[48187],\"periodSent\":"
                        + PERIOD_PING +"}"));

        LocalDateTime previousConnectionTime = LocalDateTime.now();

        TimeUnit.SECONDS.sleep(PERIOD_PING*COEFFICIENT_NOTIFICATION + 1);

        this.mockMvc.perform(post("/event").content("""
                        {
                          "events": [
                            {
                              "id": 48188,
                              "nameEvent": "start",
                              "timeEvent": "2023-03-21 12:55:18.858",
                              "temperature": 0,
                              "processor": 0,
                              "usedMemory": 0,
                              "freeMemory": 0,
                              "sent": false
                            }
                          ],
                          "deviceId": 0
                        }""").contentType("application/json")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"eventsIdsDelivered\":[48188],\"periodSent\":"
                        + PERIOD_PING +"}"));

        ArgumentCaptor<String> capturedString = ArgumentCaptor.forClass(String.class);
        verify(telegramBotMock, times(3)).sendToAll(capturedString.capture());
        assertThat(capturedString.getAllValues().get(0)).isEqualTo(EventsService.MESSAGE_FIRST
                + "2023-03-21 12:50:18;\n" + EventsService.MESSAGE_RECONNECTED);
        assertThat(capturedString.getAllValues().get(1)).isEqualTo(CheckingLastDate.MESSAGE_OFFLINE);
        assertThat(capturedString.getAllValues().get(2)).isIn
                (EventsService.MESSAGE_ELECTRICITY_OFF
                + previousConnectionTime.format(CheckingLastDate.dateTimeFormatterSeconds)
                + EventsService.MESSAGE_UNTIL
                + "2023-03-21 12:55:18;\n"

                + EventsService.MESSAGE_RECONNECTED,

                        EventsService.MESSAGE_ELECTRICITY_OFF
                        + previousConnectionTime.minusSeconds(1).format(CheckingLastDate.dateTimeFormatterSeconds)
                        + EventsService.MESSAGE_UNTIL
                                + "2023-03-21 12:55:18;\n"
                );
    }
}
