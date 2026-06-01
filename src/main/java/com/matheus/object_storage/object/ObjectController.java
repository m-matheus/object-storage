package com.matheus.object_storage.object;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.matheus.object_storage.object.dto.ObjectResponse;

@RestController
@RequestMapping("/buckets/{bucketId}/objects")
public class ObjectController {
    private final ObjectService service;

    public ObjectController(ObjectService service) {
        this.service = service;
    }

    private ObjectResponse toResponse(BucketObject object) {
        return new ObjectResponse(object.id(), object.key(), object.bucketId(),
                object.contentType(), object.size(), object.createdAt());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjectResponse upload(@PathVariable UUID bucketId, @RequestParam String key,
            @RequestParam MultipartFile file) throws IOException {
                String contentType = file.getContentType();
                byte[] data = file.getBytes();

                BucketObject result = service.uploadObject(bucketId, key, contentType, data);
                
                return toResponse(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(
        @PathVariable UUID bucketId,
        @PathVariable UUID id) {
            BucketObject object = service.getObject(bucketId, id);

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(object.contentType())).body(object.data());
        }

        @GetMapping
        public List<ObjectResponse> list(@PathVariable UUID bucketId) {
            List<BucketObject> objects = service.listObjects(bucketId);

            return objects.stream().map(this::toResponse).toList();
        }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID bucketId, @PathVariable UUID id) {
        service.deleteObject(bucketId, id);
    }
}
