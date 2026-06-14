package org.vaadin.addons.maplibre;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

/**
 * Serves the JPG images stitched by {@link TileStitcher} from the same origin as
 * the app, so MapLibre can use them as an image source without any CORS concerns.
 */
@RestController
public class GeneratedOrthoController {

    public static final String PATH = "/generated-ortho/";

    @GetMapping(value = PATH + "{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> image(@PathVariable("name") String name) throws Exception {
        // Guard against path traversal: only plain file names are accepted.
        if (!name.matches("[\\w.-]+\\.jpg")) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(TileStitcher.OUTPUT_DIR, name);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Files.readAllBytes(file.toPath()));
    }
}
