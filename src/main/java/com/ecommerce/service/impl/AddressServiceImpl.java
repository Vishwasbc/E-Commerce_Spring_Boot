package com.ecommerce.service.impl;

import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/*https://ecommerce-api-4095.postman.co/workspace/ECommerce_Spring_Boot~420f1a80-5e7b-4f88-b4eb-286ccfbe02e8/collection/41362366-c9154067-2852-44b9-86fd-144b6ce3c4e6?action=share&source=copy-link&creator=41362366*/

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }
}
