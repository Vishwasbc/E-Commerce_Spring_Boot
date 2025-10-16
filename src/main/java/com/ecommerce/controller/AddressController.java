package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.AddressService;
import com.ecommerce.utility.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDto = addressService.createAddress(addressDTO,user);
        return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);
    }
}
