package by.it.academy.grodno.elibrary.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@SuperBuilder
@JsonPropertyOrder(value = {"code", "number"})
public class PhoneNumber implements Serializable {

    @JsonIgnoreProperties
    public static final String COUNTRY_CODE = "+375";

    @JsonProperty(value = "code")
    private String phoneCode;

    @JsonProperty(value = "number")
    private String number;
}
