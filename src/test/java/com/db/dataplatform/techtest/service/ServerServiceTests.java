package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObjectWithInValidMDSum;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setCheckSum(TestDataHelper.MD5_CHECKSUM);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);
        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldNotSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        DataEnvelope invalidTestDataEnvelope = createTestDataEnvelopeApiObjectWithInValidMDSum();
        boolean result = server.saveDataEnvelope(invalidTestDataEnvelope);
        assertThat(result).isFalse();
        verify(dataBodyServiceImplMock, never()).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldFindDataBlocksAsExpected(){
        when(dataBodyServiceImplMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(Arrays.asList(expectedDataBodyEntity));
        List<DataEnvelope> dataEnvelopeByBlocktype = server.findDataEnvelopeByBlocktype(BlockTypeEnum.BLOCKTYPEA);
        assertEquals(1, dataEnvelopeByBlocktype.size());
    }
}
