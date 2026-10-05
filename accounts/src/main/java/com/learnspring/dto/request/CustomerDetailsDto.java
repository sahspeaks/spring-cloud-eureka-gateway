package com.learnspring.dto.request;

import lombok.Data;

@Data
public class CustomerDetailsDto {

    private CustomerDto customerDto;
    private LoansDto loansDto;
    private CardsDto cardsDto;

}
