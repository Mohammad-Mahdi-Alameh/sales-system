package com.sales.system.service.impl;

import com.sales.system.domain.Sale;
import com.sales.system.repository.SaleRepository;
import com.sales.system.service.SaleService;
import com.sales.system.service.dto.SaleDTO;
import com.sales.system.service.mapper.SaleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sales.system.domain.Sale}.
 */
@Service
@Transactional
public class SaleServiceImpl implements SaleService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;

    private final SaleMapper saleMapper;

    public SaleServiceImpl(SaleRepository saleRepository, SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
    }

    @Override
    public SaleDTO save(SaleDTO saleDTO) {
        LOG.debug("Request to save Sale : {}", saleDTO);
        Sale sale = saleMapper.toEntity(saleDTO);
        sale = saleRepository.save(sale);
        return saleMapper.toDto(sale);
    }

    @Override
    public SaleDTO update(SaleDTO saleDTO) {
        LOG.debug("Request to update Sale : {}", saleDTO);
        Sale sale = saleMapper.toEntity(saleDTO);
        sale = saleRepository.save(sale);
        return saleMapper.toDto(sale);
    }

    @Override
    public Optional<SaleDTO> partialUpdate(SaleDTO saleDTO) {
        LOG.debug("Request to partially update Sale : {}", saleDTO);

        return saleRepository
            .findById(saleDTO.getId())
            .map(existingSale -> {
                saleMapper.partialUpdate(existingSale, saleDTO);

                return existingSale;
            })
            .map(saleRepository::save)
            .map(saleMapper::toDto);
    }

    public Page<SaleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return saleRepository.findAllWithEagerRelationships(pageable).map(saleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleDTO> findOne(Long id) {
        LOG.debug("Request to get Sale : {}", id);
        return saleRepository.findOneWithEagerRelationships(id).map(saleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Sale : {}", id);
        saleRepository.deleteById(id);
    }
}
