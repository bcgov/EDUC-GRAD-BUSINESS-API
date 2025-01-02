package ca.bc.gov.educ.api.gradbusiness.model.dto.v2;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseModel {
	private String createUser;
	private String createDate;
	@NotBlank(message = "updateUser must not be null or empty")
	private String updateUser;
	private String updateDate;
}
