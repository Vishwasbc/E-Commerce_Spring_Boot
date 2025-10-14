package com.ecommerce.exceptions;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
	@Serial
    private static final long serialVersionUID = 9162283598759358845L;
	String resourceName;
	String field;
	String fieldName;
	Long fieldId;

    public ResourceNotFoundException(String resourceName,String field,String fieldName) {
		super(String.format("%s not found with %s : %s",resourceName,field,fieldName));
		this.resourceName=resourceName;
		this.field=field;
		this.fieldName=fieldName;
	}
	public ResourceNotFoundException(String resourceName,String field,Long fieldId) {
		super(String.format("%s not found with %s : %d",resourceName,field,fieldId));
		this.resourceName=resourceName;
		this.field=field;
		this.fieldId=fieldId;
	}
	
}
