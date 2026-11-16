package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.InstrumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstrumentController.class)
class InstrumentControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private InstrumentService instruments;

    @Test
    void returnsConsistentValidationErrorWithoutCallingService() throws Exception {
        mvc.perform(post("/api/v1/instruments").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"lowercase\",\"name\":\"\",\"currency\":\"US\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.symbol").exists());
    }
}
