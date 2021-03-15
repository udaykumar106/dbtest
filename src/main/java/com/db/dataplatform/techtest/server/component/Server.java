package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    Boolean saveDataEnvelope(DataEnvelope envelope) throws IOException, NoSuchAlgorithmException;
    List<DataEnvelope> findDataEnvelopeByBlocktype(BlockTypeEnum blockType);
    Boolean updateBlocktypeByName(String name, BlockTypeEnum blockType);
}
