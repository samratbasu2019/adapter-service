package com.org.infy.adapter.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;


import java.io.Serializable;

@Data
public class CoinDTO implements Serializable{
    private Long id;
    private String key;
    private Deals deals;
    private Default defaults;
    private Long resetToDefaultDate;
}
