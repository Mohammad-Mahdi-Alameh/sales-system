package com.sales.system.service.mapper;

import com.sales.system.domain.SaleTransaction;
import com.sales.system.domain.TransactionLog;
import com.sales.system.service.dto.SaleTransactionDTO;
import com.sales.system.service.dto.TransactionLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionLog} and its DTO {@link TransactionLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionLogMapper extends EntityMapper<TransactionLogDTO, TransactionLog> {
//    @Mapping(target = "saleTransaction", source = "saleTransaction", qualifiedByName = "saleTransactionId")
    @Mapping(target = "saleTransactionId", expression = "java(s.getSaleTransaction() != null ? s.getSaleTransaction().getId() : null)")
    TransactionLogDTO toDto(TransactionLog s);

    @Named("saleTransactionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleTransactionDTO toDtoSaleTransactionId(SaleTransaction saleTransaction);
}
