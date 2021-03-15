package com.db.dataplatform.techtest.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataEnvelope.class)
@JsonDeserialize(as = DataEnvelope.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DataEnvelope {

    @NotNull
    private DataHeader dataHeader;

    @NotNull
    private DataBody dataBody;

    @NotNull
    private String mdSum;

    public DataEnvelope(final DataHeader dataHeader, final DataBody dataBody){
        this.dataHeader = new DataHeader(dataHeader.getName(), dataHeader.getBlockType());
        this.dataBody = new DataBody(dataBody.getDataBody());
    }
}
