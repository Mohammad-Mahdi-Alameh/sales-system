package com.sales.system.service.mapper;

import com.sales.system.domain.Client;
import com.sales.system.domain.Sale;
import com.sales.system.domain.Seller;
import com.sales.system.service.dto.ClientDTO;
import com.sales.system.service.dto.SaleDTO;
import com.sales.system.service.dto.SellerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sale} and its DTO {@link SaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleMapper extends EntityMapper<SaleDTO, Sale> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientName")
    @Mapping(target = "seller", source = "seller", qualifiedByName = "sellerEmployeeCode")
    SaleDTO toDto(Sale s);

    @Named("clientName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ClientDTO toDtoClientName(Client client);

    @Named("sellerEmployeeCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "employeeCode", source = "employeeCode")
    SellerDTO toDtoSellerEmployeeCode(Seller seller);
}
