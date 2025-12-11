package com.makurohashami.realtorconnect.entity.realestate.embedded;

import com.makurohashami.realtorconnect.entity.realestate.enumeration.LoggiaType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loggia {

    @Column(name = "loggia_type_id")
    private LoggiaType type;
    @Min(1)
    @Max(127)
    @Column(name = "loggias_count")
    private short count;
    @Column(name = "is_loggia_glassed")
    private boolean glassed;

}
