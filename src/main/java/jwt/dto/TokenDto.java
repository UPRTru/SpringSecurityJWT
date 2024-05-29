package jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized //missing
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
  String token = "";
}
