package com.db.dataplatform.techtest.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonSerialize(as = ResponseDataEnvelope.class)
@JsonDeserialize(as = ResponseDataEnvelope.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDataEnvelope {
    List<DataEnvelope> dataEnvelopeList;
}
