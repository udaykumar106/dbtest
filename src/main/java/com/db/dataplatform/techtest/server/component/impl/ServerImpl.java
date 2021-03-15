package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.api.model.DataBody;
import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public Boolean saveDataEnvelope(DataEnvelope envelope) {
        if(!isEqualMDSum(envelope)) {
            return false;
        }
        // Save to persistence.
        persist(envelope);

        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        return true;
    }

    @Override
    public List<DataEnvelope> findDataEnvelopeByBlocktype(BlockTypeEnum blockType) {
        List<DataBodyEntity> dataByBlockType = dataBodyServiceImpl.getDataByBlockType(blockType);
        return dataByBlockType.stream()
                .map(this::fromDataBodyEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean updateBlocktypeByName(String name, BlockTypeEnum blockType) {
        dataBodyServiceImpl.updateBlocktypeByName(name, blockType);
        return true;

    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setCheckSum(envelope.getMdSum());

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    private boolean isEqualMDSum(final DataEnvelope dataEnvelope){
        String bodyMDSum = DigestUtils.md5DigestAsHex(dataEnvelope.getDataBody().getDataBody().getBytes(StandardCharsets.UTF_8));
        return dataEnvelope.getMdSum().equals(bodyMDSum);
    }

    private DataEnvelope fromDataBodyEntity(final DataBodyEntity dataBodyEntity){
        DataBody dataBody = new DataBody(dataBodyEntity.getDataBody());
        DataHeaderEntity dataHeaderEntity = dataBodyEntity.getDataHeaderEntity();
        DataHeader dataHeader = new DataHeader(dataHeaderEntity.getName(), dataHeaderEntity.getBlocktype());
        return new DataEnvelope(dataHeader, dataBody, dataBodyEntity.getCheckSum());

    }

}
