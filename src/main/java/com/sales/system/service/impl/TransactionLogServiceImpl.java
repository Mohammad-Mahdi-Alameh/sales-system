package com.sales.system.service.impl;

import com.sales.system.domain.TransactionLog;
import com.sales.system.repository.TransactionLogRepository;
import com.sales.system.service.TransactionLogService;
import com.sales.system.service.dto.TransactionLogDTO;
import com.sales.system.service.mapper.TransactionLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sales.system.domain.TransactionLog}.
 */
@Service
@Transactional
public class TransactionLogServiceImpl implements TransactionLogService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionLogServiceImpl.class);

    private final TransactionLogRepository transactionLogRepository;

    private final TransactionLogMapper transactionLogMapper;

    public TransactionLogServiceImpl(TransactionLogRepository transactionLogRepository, TransactionLogMapper transactionLogMapper) {
        this.transactionLogRepository = transactionLogRepository;
        this.transactionLogMapper = transactionLogMapper;
    }

    @Override
    public TransactionLogDTO save(TransactionLogDTO transactionLogDTO) {
        LOG.debug("Request to save TransactionLog : {}", transactionLogDTO);
        TransactionLog transactionLog = transactionLogMapper.toEntity(transactionLogDTO);
        transactionLog = transactionLogRepository.save(transactionLog);
        return transactionLogMapper.toDto(transactionLog);
    }

    @Override
    public TransactionLogDTO update(TransactionLogDTO transactionLogDTO) {
        LOG.debug("Request to update TransactionLog : {}", transactionLogDTO);
        TransactionLog transactionLog = transactionLogMapper.toEntity(transactionLogDTO);
        transactionLog = transactionLogRepository.save(transactionLog);
        return transactionLogMapper.toDto(transactionLog);
    }

    @Override
    public Optional<TransactionLogDTO> partialUpdate(TransactionLogDTO transactionLogDTO) {
        LOG.debug("Request to partially update TransactionLog : {}", transactionLogDTO);

        return transactionLogRepository
            .findById(transactionLogDTO.getId())
            .map(existingTransactionLog -> {
                transactionLogMapper.partialUpdate(existingTransactionLog, transactionLogDTO);

                return existingTransactionLog;
            })
            .map(transactionLogRepository::save)
            .map(transactionLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionLogDTO> findOne(Long id) {
        LOG.debug("Request to get TransactionLog : {}", id);
        return transactionLogRepository.findById(id).map(transactionLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TransactionLog : {}", id);
        transactionLogRepository.deleteById(id);
    }
}
