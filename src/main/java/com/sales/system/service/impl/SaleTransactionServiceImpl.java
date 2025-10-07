package com.sales.system.service.impl;

import com.sales.system.domain.SaleTransaction;
import com.sales.system.domain.TransactionLog;
import com.sales.system.repository.SaleTransactionRepository;
import com.sales.system.repository.TransactionLogRepository;
import com.sales.system.service.SaleTransactionService;
import com.sales.system.service.dto.SaleTransactionDTO;
import com.sales.system.service.mapper.SaleTransactionMapper;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sales.system.domain.SaleTransaction}.
 */
@Service
@Transactional
public class SaleTransactionServiceImpl implements SaleTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleTransactionServiceImpl.class);

    private final SaleTransactionRepository saleTransactionRepository;

    private final TransactionLogRepository transactionLogRepository;

    private final SaleTransactionMapper saleTransactionMapper;

    public SaleTransactionServiceImpl(SaleTransactionRepository saleTransactionRepository, SaleTransactionMapper saleTransactionMapper, TransactionLogRepository transactionLogRepository) {
        this.saleTransactionRepository = saleTransactionRepository;
        this.transactionLogRepository = transactionLogRepository;
        this.saleTransactionMapper = saleTransactionMapper;
    }

    @Override
    public SaleTransactionDTO save(SaleTransactionDTO saleTransactionDTO) {
        LOG.debug("Request to save SaleTransaction : {}", saleTransactionDTO);
        SaleTransaction saleTransaction = saleTransactionMapper.toEntity(saleTransactionDTO);
        saleTransaction = saleTransactionRepository.save(saleTransaction);
        saveTransactionLog("CREATE", saleTransaction);
        return saleTransactionMapper.toDto(saleTransaction);
    }

    @Override
    public SaleTransactionDTO update(SaleTransactionDTO saleTransactionDTO) {
        LOG.debug("Request to update SaleTransaction : {}", saleTransactionDTO);
        SaleTransaction saleTransaction = saleTransactionMapper.toEntity(saleTransactionDTO);
        saleTransaction = saleTransactionRepository.save(saleTransaction);
        saveTransactionLog("PUT", saleTransaction);
        return saleTransactionMapper.toDto(saleTransaction);
    }

    @Override
    public Optional<SaleTransactionDTO> partialUpdate(SaleTransactionDTO saleTransactionDTO) {
        LOG.debug("Request to partially update SaleTransaction : {}", saleTransactionDTO);

        return saleTransactionRepository
            .findById(saleTransactionDTO.getId())
            .map(existingSaleTransaction -> {
                saleTransactionMapper.partialUpdate(existingSaleTransaction, saleTransactionDTO);
                Optional<SaleTransaction> saleTransactionOpt = saleTransactionRepository.findById(saleTransactionDTO.getId());
                if (saleTransactionOpt.isPresent()) {
                    SaleTransaction saleTransaction = saleTransactionOpt.get();
                    saleTransactionRepository.deleteById(saleTransactionDTO.getId());
                    saveTransactionLog("PATCH", saleTransaction);
                }
                return existingSaleTransaction;
            })
            .map(saleTransactionRepository::save)
            .map(saleTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleTransactionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SaleTransactions");
        return saleTransactionRepository.findAll(pageable).map(saleTransactionMapper::toDto);
    }

    public Page<SaleTransactionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return saleTransactionRepository.findAllWithEagerRelationships(pageable).map(saleTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get SaleTransaction : {}", id);
        return saleTransactionRepository.findOneWithEagerRelationships(id).map(saleTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SaleTransaction : {}", id);
        SaleTransaction saleTransaction = saleTransactionRepository.getReferenceById(id);
        saveTransactionLog("DELETE", saleTransaction);
        saleTransactionRepository.deleteById(id);
    }

    public void saveTransactionLog(String operationType, SaleTransaction saleTransaction) {
        TransactionLog log = new TransactionLog();
        log.setTimestamp(Instant.now());
        log.setOperationType(operationType);
        log.setModifiedBy("system");
        log.setSaleTransaction(saleTransaction);
        transactionLogRepository.save(log);
    }

}
