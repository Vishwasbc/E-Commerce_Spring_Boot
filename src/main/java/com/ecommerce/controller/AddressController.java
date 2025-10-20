package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.service.AddressService;
import com.ecommerce.utility.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDto = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressList = addressService.getAllAddress();
        if (addressList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(addressList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddresses(@PathVariable Long id) {
        AddressDTO addressDTO = addressService.getAddress(id);
        if (addressDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(addressDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        List<AddressDTO> addressList = addressService.getuserAddresses();
        if (addressList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(addressList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddressDTO = addressService.updateAddress(id, addressDTO);
        if (updatedAddressDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(updatedAddressDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        String status = addressService.deleteAddress(id);
        return ResponseEntity.ok(status);
    }
}
