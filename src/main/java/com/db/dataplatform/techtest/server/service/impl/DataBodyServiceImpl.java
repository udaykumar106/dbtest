package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataBodyServiceImpl implements DataBodyService {

    private final DataStoreRepository dataStoreRepository;
    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveDataBody(DataBodyEntity dataBody) {
        dataStoreRepository.save(dataBody);
    }

    @Override
    public List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType) {
        return dataStoreRepository.findDataBodyEntitiesByDataHeaderEntity_Blocktype(blockType);
    }

    public void updateBlocktypeByName(String name, BlockTypeEnum blockType){
        dataHeaderRepository.updateBlocktypeByName(blockType, name);
    }
}
