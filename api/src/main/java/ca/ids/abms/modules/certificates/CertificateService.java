package ca.ids.abms.modules.certificates;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.modules.util.models.ModelUtils;

/**
 * Created by c.talpa on 14/12/2016.
 */
@Service
@Transactional
public class CertificateService {

    private CertificateRepository certificateRepository;


    public CertificateService(CertificateRepository certificateRepository){
        this.certificateRepository=certificateRepository;
    }


    @Transactional(readOnly = true)
    public Certificate findOne (final Integer id) {
        return certificateRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findAll (Pageable pageable, String filter) {
        Page<Certificate> certificates=null;
        /* Set the default sorting */
        if (pageable.getSort() == null) {
            final Sort sortingOpts = new Sort(new Sort.Order(Sort.Direction.DESC, "dateOfIssue"));
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sortingOpts);
        }

        if (StringUtils.isNotEmpty(filter)) {

            switch(filter){
                case "active-certificate":
                    certificates= certificateRepository.findAllByExpiryWarningIssued(pageable,Boolean.TRUE); break;

                case "expired-certificate":
                    certificates= certificateRepository.findAllByExpiryWarningIssued(pageable,Boolean.FALSE);
                    break;

                case "near-certificate":
                    // CT: ASK to Werner info on this custom filter

                default:certificates=certificateRepository.findAll(pageable);

            }

        }else{
            certificates=certificateRepository.findAll(pageable);
        }


        return certificates;
    }

    public void delete(final Integer id) {
        certificateRepository.delete(id);
    }


    public Certificate create (final Certificate certificate) {
        return certificateRepository.saveAndFlush(certificate);
    }

    public Certificate update (final Integer id, final Certificate item) {
        final Certificate existingItem = certificateRepository.getOne(id);
        ModelUtils.merge(item, existingItem, "id");
        return certificateRepository.saveAndFlush(existingItem);
    }

    public Certificate uploadImage(final Integer id, final MultipartFile image) throws IOException {
        final Certificate existingItem = certificateRepository.getOne(id);
        existingItem.setCertificateImage(image.getBytes());
        existingItem.setCertificateImageType(image.getContentType());
        certificateRepository.saveAndFlush(existingItem);
        return certificateRepository.getOne(id);
    }


}
