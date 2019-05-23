package com.softserve.demo.util;

import com.softserve.demo.dto.ProviderCriteriaDTO;
import com.softserve.demo.model.search.ProviderCriteria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProviderCriteriaMapper {

    ProviderCriteriaDTO ProviderCriteriaToProviderCriteriaDTO(ProviderCriteria providerCriteria);

    ProviderCriteria ProviderCriteriaDTOToProviderCriteria(ProviderCriteriaDTO providerCriteriaDTO);

    List<ProviderCriteriaDTO> ProviderCriteriasToProviderCriteriaDTOs(List<ProviderCriteria> providerCriterias);
}


