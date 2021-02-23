package ca.ids.abms.modules.releasenotes;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReleaseNoteService {

    private final ReleaseNoteRepository releaseNoteRepository;

    public ReleaseNoteService(ReleaseNoteRepository releaseNoteRepository) {
        this.releaseNoteRepository = releaseNoteRepository;
    }

    public List<ReleaseNote> findAllReleaseNotes() {
        return releaseNoteRepository.findAll();
    }
}
