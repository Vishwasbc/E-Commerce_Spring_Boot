package com.ecommerce.service.impl;

import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.payload.AddressDTO;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.utility.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AuthUtil authUtil;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setAddresses(new ArrayList<>());
    }

    @Test
    void testCreateAddress() {
        AddressDTO dto = new AddressDTO(null, "street", "city", "building", "state", "country", "123456");
        Address address = new Address();
        Address saved = new Address();
        saved.setAddressId(1L);
        AddressDTO savedDto = new AddressDTO(1L, "street", "city", "building", "state", "country", "123456");

        when(modelMapper.map(dto, Address.class)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(saved);
        when(modelMapper.map(saved, AddressDTO.class)).thenReturn(savedDto);

        AddressDTO result = addressService.createAddress(dto, user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        // user should have the raw address object added
        assertTrue(user.getAddresses().contains(address));
        verify(addressRepository).save(address);
    }

    @Test
    void testGetAllAddress() {
        Address a1 = new Address(); a1.setAddressId(1L);
        Address a2 = new Address(); a2.setAddressId(2L);
        List<Address> list = List.of(a1, a2);

        AddressDTO d1 = new AddressDTO(1L, null, null, null, null, null, null);
        AddressDTO d2 = new AddressDTO(2L, null, null, null, null, null, null);

        when(addressRepository.findAll()).thenReturn(list);
        when(modelMapper.map(a1, AddressDTO.class)).thenReturn(d1);
        when(modelMapper.map(a2, AddressDTO.class)).thenReturn(d2);

        List<AddressDTO> result = addressService.getAllAddress();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testGetAddressFound() {
        Address a = new Address(); a.setAddressId(5L);
        AddressDTO dto = new AddressDTO(5L, null, null, null, null, null, null);

        when(addressRepository.findById(5L)).thenReturn(Optional.of(a));
        when(modelMapper.map(a, AddressDTO.class)).thenReturn(dto);

        AddressDTO result = addressService.getAddress(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void testGetAddressNotFound() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddress(10L));
    }

    @Test
    void testGetUserAddresses() {
        Address a1 = new Address(); a1.setAddressId(11L);
        Address a2 = new Address(); a2.setAddressId(12L);
        user.getAddresses().add(a1);
        user.getAddresses().add(a2);

        AddressDTO d1 = new AddressDTO(11L, null, null, null, null, null, null);
        AddressDTO d2 = new AddressDTO(12L, null, null, null, null, null, null);

        when(authUtil.loggedInUser()).thenReturn(user);
        when(modelMapper.map(a1, AddressDTO.class)).thenReturn(d1);
        when(modelMapper.map(a2, AddressDTO.class)).thenReturn(d2);

        List<AddressDTO> result = addressService.getuserAddresses();

        assertEquals(2, result.size());
        assertEquals(11L, result.get(0).getId());
        assertEquals(12L, result.get(1).getId());
    }

    @Test
    void testUpdateAddressSuccess() {
        Long id = 20L;
        Address existing = new Address(); existing.setAddressId(id);
        User owner = new User();
        owner.setAddresses(new ArrayList<>());
        owner.getAddresses().add(existing);
        existing.setUser(owner);

        AddressDTO updateDto = new AddressDTO(null, "newStreet", "newCity", "newBuilding", "newState", "newCountry", "999999");

        Address updated = new Address(); updated.setAddressId(id);
        updated.setStreet("newStreet");
        updated.setCity("newCity");
        updated.setBuildingName("newBuilding");
        updated.setState("newState");
        updated.setCountry("newCountry");
        updated.setPinCode("999999");
        updated.setUser(owner);

        when(addressRepository.findById(id)).thenReturn(Optional.of(existing));
        when(addressRepository.save(existing)).thenReturn(updated);
        when(modelMapper.map(updated, AddressDTO.class)).thenReturn(new AddressDTO(id, "newStreet", "newCity", "newBuilding", "newState", "newCountry", "999999"));

        AddressDTO result = addressService.updateAddress(id, updateDto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        // verify user repository save was called and the owner's addresses were updated
        verify(userRepository).save(owner);
        assertTrue(owner.getAddresses().stream().anyMatch(a -> a.getAddressId().equals(id)));
    }

    @Test
    void testUpdateAddressNotFound() {
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());
        AddressDTO dto = new AddressDTO();
        assertThrows(ResourceNotFoundException.class, () -> addressService.updateAddress(99L, dto));
    }

    @Test
    void testDeleteAddressSuccess() {
        Long id = 30L;
        Address a = new Address(); a.setAddressId(id);
        User owner = new User(); owner.setAddresses(new ArrayList<>());
        owner.getAddresses().add(a);
        a.setUser(owner);

        when(addressRepository.findById(id)).thenReturn(Optional.of(a));

        String msg = addressService.deleteAddress(id);

        assertTrue(msg.contains(String.valueOf(id)));
        verify(userRepository).save(owner);
        verify(addressRepository).delete(a);
    }

    @Test
    void testDeleteAddressNotFound() {
        when(addressRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> addressService.deleteAddress(123L));
    }
}

