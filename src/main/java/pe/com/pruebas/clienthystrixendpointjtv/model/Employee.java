package pe.com.pruebas.clienthystrixendpointjtv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
@AllArgsConstructor
@Getter @Setter
public class Employee {
	private @NonNull String id;
	private @NonNull String firstName;
	private @NonNull String lastName;
	private @NonNull String email;
	private @NonNull Boolean active;
	public Employee() {
		
	}
}
