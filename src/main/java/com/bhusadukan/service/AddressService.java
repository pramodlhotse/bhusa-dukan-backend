package com.bhusadukan.service;

import com.bhusadukan.dto.AddressResponse;
import com.bhusadukan.dto.CreateAddressRequest;
import com.bhusadukan.entity.CustomerAddress;
import com.bhusadukan.entity.User;
import com.bhusadukan.repository.CustomerAddressRepository;
import com.bhusadukan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressResponse createAddress(
            Long customerId,
            CreateAddressRequest request) {

        User customer = getCustomer(customerId);

        boolean makeDefault = Boolean.TRUE.equals(request.defaultAddress());

        // First address automatically becomes default.
        boolean hasExistingAddress =
                !addressRepository.findByUserId(customerId).isEmpty();

        if (!hasExistingAddress) {
            makeDefault = true;
        }

        if (makeDefault) {
            removeExistingDefault(customerId);
        }

        CustomerAddress address = CustomerAddress.builder()
                .user(customer)
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .village(request.village())
                .city(request.city())
                .district(request.district())
                .state(
                        request.state() == null || request.state().isBlank()
                                ? "Bihar"
                                : request.state()
                )
                .pincode(request.pincode())
                .landmark(request.landmark())
                .defaultAddress(makeDefault)
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long customerId) {

        getCustomer(customerId);

        return addressRepository.findByUserId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddress(
            Long customerId,
            Long addressId) {

        getCustomer(customerId);

        CustomerAddress address =
                addressRepository.findByIdAndUserId(
                                addressId,
                                customerId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found: " + addressId
                                )
                        );

        return toResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(
            Long customerId,
            Long addressId,
            CreateAddressRequest request) {

        getCustomer(customerId);

        CustomerAddress address =
                addressRepository.findByIdAndUserId(
                                addressId,
                                customerId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found: " + addressId
                                )
                        );

        boolean makeDefault =
                Boolean.TRUE.equals(request.defaultAddress());

        if (makeDefault && !Boolean.TRUE.equals(address.getDefaultAddress())) {
            removeExistingDefault(customerId);
        }

        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setVillage(request.village());
        address.setCity(request.city());
        address.setDistrict(request.district());
        address.setState(
                request.state() == null || request.state().isBlank()
                        ? "Bihar"
                        : request.state()
        );
        address.setPincode(request.pincode());
        address.setLandmark(request.landmark());

        if (makeDefault) {
            address.setDefaultAddress(true);
        }

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse setDefaultAddress(
            Long customerId,
            Long addressId) {

        getCustomer(customerId);

        CustomerAddress address =
                addressRepository.findByIdAndUserId(
                                addressId,
                                customerId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Address not found: " + addressId
                                )
                        );

        removeExistingDefault(customerId);

        address.setDefaultAddress(true);

        return toResponse(addressRepository.save(address));
    }

    private void removeExistingDefault(Long customerId) {

        addressRepository
                .findByUserIdAndDefaultAddressTrue(customerId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefaultAddress(false);
                    addressRepository.save(existingDefault);
                });
    }

    private User getCustomer(Long customerId) {

        return userRepository.findById(customerId)
                .filter(user -> user.getRole() == User.Role.CUSTOMER)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found: " + customerId
                        )
                );
    }

    private AddressResponse toResponse(CustomerAddress address) {

        return new AddressResponse(
                address.getId(),
                address.getUser().getId(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getVillage(),
                address.getCity(),
                address.getDistrict(),
                address.getState(),
                address.getPincode(),
                address.getLandmark(),
                address.getDefaultAddress()
        );
    }
}