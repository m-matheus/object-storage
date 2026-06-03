package com.matheus.object_storage.bucket;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.matheus.object_storage.bucket.dto.BucketRequest;
import com.matheus.object_storage.exception.GlobalExceptionHandler;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BucketControllerTest {
    private final UUID bucketId = UUID.randomUUID();
    private final Bucket bucket = new Bucket(bucketId, "my-bucket", "anonymous", Instant.now());

    private MockMvc mockMvc;

    @Autowired
    BucketController bucketController;

    @Autowired
    GlobalExceptionHandler exceptionHandler;

    @MockitoBean
    BucketService bucketService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bucketController)
                .setControllerAdvice(exceptionHandler).build();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void create_shouldReturn201WithBucket() throws Exception {
        given(bucketService.createBucket("my-bucket")).willReturn(bucket);

        mockMvc.perform(post("/buckets").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BucketRequest("my-bucket"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("my-bucket"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void create_whenNameExists_shouldReturn409() throws Exception {
        given(bucketService.createBucket("my-bucket"))
                .willThrow(new BucketAlreadyExistsException("Bucket already exists: my-bucket"));

        mockMvc.perform(post("/buckets").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BucketRequest("my-bucket"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Bucket already exists: my-bucket"));
    }

    @Test
    void list_shouldReturn200WithBuckets() throws Exception {
        given(bucketService.listBuckets()).willReturn(List.of(bucket));

        mockMvc.perform(get("/buckets")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("my-bucket"));
    }

    @Test
    void get_whenExists_shouldReturn200() throws Exception {
        given(bucketService.getBucket(bucketId)).willReturn(bucket);

        mockMvc.perform(get("/buckets/{id}", bucketId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bucketId.toString()));
    }

    @Test
    void get_whenNotFound_shouldReturn404() throws Exception {
        given(bucketService.getBucket(bucketId))
                .willThrow(new BucketNotFoundException("Bucket not found: " + bucketId));

        mockMvc.perform(get("/buckets/{id}", bucketId)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void delete_whenExists_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/buckets/{id}", bucketId)).andExpect(status().isNoContent());
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new BucketNotFoundException("Bucket not found: " + bucketId)).when(bucketService)
                .deleteBucket(bucketId);

        mockMvc.perform(delete("/buckets/{id}", bucketId)).andExpect(status().isNotFound());
    }

}
