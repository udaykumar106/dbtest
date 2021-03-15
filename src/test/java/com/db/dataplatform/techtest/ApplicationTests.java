package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.api.model.ResponseDataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriTemplate;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class ApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DataStoreRepository dataStoreRepository;

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");


    @BeforeEach
    public void setup(){
        dataStoreRepository.deleteAll();
    }

    @AfterEach
    public void teardown(){
        dataStoreRepository.deleteAll();
    }
    @Test
    void testPushDataPostCallWorksAsExpected(){
        DataEnvelope testDataEnvelopeApiObject = TestDataHelper.createTestDataEnvelopeApiObject();
        ResponseEntity<Boolean> booleanResponseEntity = testRestTemplate.postForEntity(URI_PUSHDATA, testDataEnvelopeApiObject, Boolean.class);
        Assertions.assertTrue(booleanResponseEntity.getBody());
        Assertions.assertEquals(1, dataStoreRepository.findAll().size());
    }

    @Test
    void testGetBlocksForBlockType(){
        DataHeaderEntity dataHeaderEntity = TestDataHelper.createTestDataHeaderEntity(Instant.now());
        DataBodyEntity dataBodyEntity = TestDataHelper.createTestDataBodyEntity(dataHeaderEntity);
        dataStoreRepository.save(dataBodyEntity);


        BlockTypeEnum blocktypea = BlockTypeEnum.BLOCKTYPEA;
        ResponseEntity<ResponseDataEnvelope> forEntity = testRestTemplate.getForEntity(URI_GETDATA.expand(blocktypea), ResponseDataEnvelope.class);
        Assertions.assertEquals(1, forEntity.getBody().getDataEnvelopeList().size());
    }

    @Test
    void testPatchForBlockType(){
        DataHeaderEntity dataHeaderEntity = TestDataHelper.createTestDataHeaderEntity(Instant.now());
        DataBodyEntity dataBodyEntity = TestDataHelper.createTestDataBodyEntity(dataHeaderEntity);
        DataBodyEntity save = dataStoreRepository.save(dataBodyEntity);


        BlockTypeEnum blocktype = BlockTypeEnum.BLOCKTYPEB;
        String name = TestDataHelper.TEST_NAME;
        testRestTemplate.patchForObject(URI_PATCHDATA.expand(name, blocktype), null, ResponseEntity.class);

        Optional<DataBodyEntity> byId = dataStoreRepository.findById(save.getDataStoreId());
        Assertions.assertEquals(BlockTypeEnum.BLOCKTYPEB, byId.get().getDataHeaderEntity().getBlocktype());

    }

}
