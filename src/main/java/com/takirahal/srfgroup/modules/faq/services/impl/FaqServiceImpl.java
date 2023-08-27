package com.takirahal.srfgroup.modules.faq.services.impl;

import com.takirahal.srfgroup.exceptions.ResouorceNotFoundException;
import com.takirahal.srfgroup.exceptions.UnauthorizedException;
import com.takirahal.srfgroup.modules.faq.dto.FaqDTO;
import com.takirahal.srfgroup.modules.faq.dto.filter.FaqFilter;
import com.takirahal.srfgroup.modules.faq.entities.Faq;
import com.takirahal.srfgroup.modules.faq.mapper.FaqMapper;
import com.takirahal.srfgroup.modules.faq.repositories.FaqRepository;
import com.takirahal.srfgroup.modules.faq.services.FaqService;
import com.takirahal.srfgroup.modules.home.entities.PostHomeFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FaqServiceImpl implements FaqService {

    private final Logger log = LoggerFactory.getLogger(FaqServiceImpl.class);

    @Autowired
    FaqRepository faqRepository;

    @Autowired
    FaqMapper faqMapper;

    @Override
    public FaqDTO save(FaqDTO faqDTO) {
        log.debug("Request to save Faq : {}", faqDTO);
        Faq faq = faqMapper.toEntity(faqDTO);
        faq = faqRepository.save(faq);
        return faqMapper.toDto(faq);
    }

    @Override
    // @Cacheable(value="faqs")
    public Page<FaqDTO> findByCriteria(FaqFilter criteria, Pageable pageable) {
        log.debug("Request to get Faq by : {}, : {}", criteria, pageable);
        return faqRepository.findAll(createSpecification(criteria), pageable).map(item-> faqMapper.toDto(item));
    }

    @Override
    public Page<Faq> findByCriteriaEntity(FaqFilter criteria, Pageable pageable) {
        return faqRepository.findAll(createSpecification(criteria), pageable);
    }

    @Override
    public List<Faq> findAll() {
        return faqRepository.findAll();
    }

    @Override
    public FaqDTO findById(Long id) {
        log.debug("Request to find faq us by id : {}", id);
        return faqRepository.findById(id)
                .map(item -> faqMapper.toDto(item))
                .orElseThrow(() -> new ResouorceNotFoundException("Not found entity with id "+id));
    }

    @Override
    public FaqDTO update(Long id, FaqDTO faqDTO) {
        if (!Objects.equals(id, faqDTO.getId())) {
            throw new UnauthorizedException("Unauthorized action");
        }

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResouorceNotFoundException("Entity not found with id"));

        faq.setQuestionAr(faqDTO.getQuestionAr());
        faq.setQuestionFr(faqDTO.getQuestionFr());
        faq.setQuestionEn(faqDTO.getQuestionEn());
        faq.setResponseAr(faqDTO.getResponseAr());
        faq.setResponseFr(faqDTO.getResponseFr());
        faq.setResponseEn(faqDTO.getResponseEn());

        faq = faqRepository.save(faq);

        return faqMapper.toDto(faq);
    }

    protected Specification<Faq> createSpecification(FaqFilter faqFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
