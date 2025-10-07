package com.sales.system.service.impl;

import com.sales.system.domain.Seller;
import com.sales.system.repository.SellerRepository;
import com.sales.system.service.SellerService;
import com.sales.system.service.dto.SellerDTO;
import com.sales.system.service.mapper.SellerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sales.system.domain.Seller}.
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    private static final Logger LOG = LoggerFactory.getLogger(SellerServiceImpl.class);

    private final SellerRepository sellerRepository;

    private final SellerMapper sellerMapper;

    public SellerServiceImpl(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    @Override
    public SellerDTO save(SellerDTO sellerDTO) {
        LOG.debug("Request to save Seller : {}", sellerDTO);
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);
        return sellerMapper.toDto(seller);
    }

    @Override
    public SellerDTO update(SellerDTO sellerDTO) {
        LOG.debug("Request to update Seller : {}", sellerDTO);
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);
        return sellerMapper.toDto(seller);
    }

    @Override
    public Optional<SellerDTO> partialUpdate(SellerDTO sellerDTO) {
        LOG.debug("Request to partially update Seller : {}", sellerDTO);

        return sellerRepository
            .findById(sellerDTO.getId())
            .map(existingSeller -> {
                sellerMapper.partialUpdate(existingSeller, sellerDTO);

                return existingSeller;
            })
            .map(sellerRepository::save)
            .map(sellerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellerDTO> findOne(Long id) {
        LOG.debug("Request to get Seller : {}", id);
        return sellerRepository.findById(id).map(sellerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Seller : {}", id);
        sellerRepository.deleteById(id);
    }
}
