package com.sales.system.service.mapper;

import com.sales.system.domain.Product;
import com.sales.system.domain.Sale;
import com.sales.system.domain.SaleTransaction;
import com.sales.system.service.dto.ProductDTO;
import com.sales.system.service.dto.SaleDTO;
import com.sales.system.service.dto.SaleTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleTransaction} and its DTO {@link SaleTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleTransactionMapper extends EntityMapper<SaleTransactionDTO, SaleTransaction> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    @Mapping(target = "sale", source = "sale", qualifiedByName = "saleId")
    SaleTransactionDTO toDto(SaleTransaction s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);
}
