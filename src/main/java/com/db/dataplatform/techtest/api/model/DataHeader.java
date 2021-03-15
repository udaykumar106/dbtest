package com.db.dataplatform.techtest.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@ToString
public class DataHeader {

    @NotNull
    public final String name;

    @NotNull
    private final BlockTypeEnum blockType;

}
