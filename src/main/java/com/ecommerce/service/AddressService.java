package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddress(Long id);

    List<AddressDTO> getuserAddresses();

    AddressDTO updateAddress(Long id, @Valid AddressDTO addressDTO);

    String deleteAddress(Long id);
}
