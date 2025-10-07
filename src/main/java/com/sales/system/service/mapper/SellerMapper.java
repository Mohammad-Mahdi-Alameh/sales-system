package com.sales.system.service.mapper;

import com.sales.system.domain.Seller;
import com.sales.system.service.dto.SellerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Seller} and its DTO {@link SellerDTO}.
 */
@Mapper(componentModel = "spring")
public interface SellerMapper extends EntityMapper<SellerDTO, Seller> {}
