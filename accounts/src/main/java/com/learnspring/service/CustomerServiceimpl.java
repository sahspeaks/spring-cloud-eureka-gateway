package com.learnspring.service;

import com.learnspring.dto.request.*;
import com.learnspring.entity.AccountsEntity;
import com.learnspring.entity.CustomerEntity;
import com.learnspring.exception.ResourceNotFoundException;
import com.learnspring.mapper.AccountsMapper;
import com.learnspring.mapper.CustomerMapper;
import com.learnspring.repository.AccountsRepository;
import com.learnspring.repository.CustomerRepository;
import com.learnspring.service.clients.CardsFeignClient;
import com.learnspring.service.clients.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceimpl implements ICustomerService{

    private final CustomerRepository customerRepository;
    private final AccountsRepository accountsRepository;
    private final LoansFeignClient loansFeignClient;
    private final CardsFeignClient cardsFeignClient;


    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        CustomerEntity customerEntity = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        AccountsEntity accountsEntity = accountsRepository.findByCustomerId(customerEntity.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customerEntity.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customerEntity, new CustomerDto());
        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(accountsEntity, new AccountsDto());
        customerDto.setAccountsDto(accountsDto);
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();
        customerDetailsDto.setCustomerDto(customerDto);
        //need to set cards and loans details here
        ResponseEntity<CardsDto> cardsResponse = cardsFeignClient.fetchCardDetails(mobileNumber);
        if (cardsResponse.getStatusCode().is2xxSuccessful()) {
            customerDetailsDto.setCardsDto(cardsResponse.getBody());
        }
        ResponseEntity<LoansDto> loansResponse = loansFeignClient.fetchLoanDetails(mobileNumber);
        if (loansResponse.getStatusCode().is2xxSuccessful()) {
            customerDetailsDto.setLoansDto(loansResponse.getBody());
        }
        return customerDetailsDto;
    }
}
