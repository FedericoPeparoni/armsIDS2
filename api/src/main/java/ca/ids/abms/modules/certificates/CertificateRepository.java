package ca.ids.abms.modules.certificates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    Page<Certificate> findAllByExpiryWarningIssued(Pageable pageable, Boolean expiryWarningIssued);

}
