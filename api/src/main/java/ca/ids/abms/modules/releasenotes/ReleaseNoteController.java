package ca.ids.abms.modules.releasenotes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/releasenotes")
public class ReleaseNoteController {

    private final ReleaseNoteService releaseNoteService;

    public ReleaseNoteController(ReleaseNoteService releaseNoteService) {
        this.releaseNoteService = releaseNoteService;
    }

    @GetMapping
    public ResponseEntity<List<ReleaseNote>> get() {
        return ResponseEntity.ok().body(releaseNoteService.findAllReleaseNotes());
    }
}
