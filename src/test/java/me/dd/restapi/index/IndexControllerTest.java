package me.dd.restapi.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import me.dd.restapi.BaseControllerTest;

public class IndexControllerTest extends BaseControllerTest {

    @Test
    void index() throws Exception {
        this.mockMvc.perform(get("/api/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.events").exists());

    }
}
