package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntity;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    @Mock
    private DataHeaderRepository dataHeaderRepository;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;

    @Before
    public void setup() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);

        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock, dataHeaderRepository);
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected(){
        dataBodyService.saveDataBody(expectedDataBodyEntity);

        verify(dataStoreRepositoryMock, times(1))
                .save(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldGetDataByBlockType(){
        when(dataStoreRepositoryMock.findDataBodyEntitiesByDataHeaderEntity_Blocktype(BlockTypeEnum.BLOCKTYPEA)).thenReturn(Arrays.asList(expectedDataBodyEntity));
        List<DataBodyEntity> dataByBlockType = dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);
        assertEquals(1, dataByBlockType.size());
        verify(dataStoreRepositoryMock, times(1)).findDataBodyEntitiesByDataHeaderEntity_Blocktype(eq(BlockTypeEnum.BLOCKTYPEA));
    }

}
