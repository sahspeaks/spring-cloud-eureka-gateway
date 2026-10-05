package com.learnspring.service;

import com.learnspring.dto.request.CustomerDetailsDto;

public interface ICustomerService {

    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber);
}
