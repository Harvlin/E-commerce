package com.project.Ecommerce.service.impl;

import com.project.Ecommerce.domain.dto.AddressDto;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.AddressEntity;
import com.project.Ecommerce.domain.entity.UserEntity;
import com.project.Ecommerce.repository.AddressRepository;
import com.project.Ecommerce.service.interf.AddressService;
import com.project.Ecommerce.service.interf.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    @Override
    public Response saveAndUpdateAddress(AddressDto addressDto) {
        UserEntity userEntity = userService.getLoggedInUser();
        AddressEntity addressEntity = userEntity.getAddress();

        if (addressEntity == null) {
            addressEntity = new AddressEntity();
            addressEntity.setUserEntity(userEntity);
        }

        if (addressDto.getStreet() != null && !addressDto.getStreet().isEmpty()) addressEntity.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null && !addressDto.getCity().isEmpty()) addressEntity.setCity(addressDto.getCity());
        if (addressDto.getState() != null && !addressDto.getState().isEmpty()) addressEntity.setState(addressDto.getState());
        if (addressDto.getCountry() != null && !addressDto.getCountry().isEmpty()) addressEntity.setCountry(addressDto.getCountry());
        if (addressDto.getZipCode() != null && !addressDto.getZipCode().isEmpty()) addressEntity.setZipCode(addressDto.getZipCode());

        addressRepository.save(addressEntity);
        String message = (userEntity.getAddress() == null) ? "Address successfully created" : "Address successfully updated";
        return Response.builder()
                .status(200)
                .message(message)
                .build();
    }


}
