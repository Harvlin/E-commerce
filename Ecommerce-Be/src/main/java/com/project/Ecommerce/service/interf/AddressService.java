package com.project.Ecommerce.service.interf;

import com.project.Ecommerce.domain.dto.AddressDto;
import com.project.Ecommerce.domain.dto.response.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);
}
