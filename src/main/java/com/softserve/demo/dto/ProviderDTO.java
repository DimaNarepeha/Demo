package com.softserve.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ProviderDTO {
    private Integer id;
    private String name;
    @Email
    private String email;
    private String description;
    private String image;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;
    private UserDTO userDTO;
    private List<OrderDTO> ordersDTO;
    private List<ServiceDTO> servicesDTO;
    private LocationDTO location;
}
