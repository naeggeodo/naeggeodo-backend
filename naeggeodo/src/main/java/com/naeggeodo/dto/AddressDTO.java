package com.naeggeodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
	@NotBlank
	private String address;
	@NotBlank
	private String zonecode;
	@NotBlank
	private String buildingCode;

}
