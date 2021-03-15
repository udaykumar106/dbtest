package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.component.HadoopClient;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

	private static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	private static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	private static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");
	private static final String HADOOP_URL = "http://localhost:8090/hadoopserver/pushbigdata";

	@Mock
	private Server serverMock;

	@Autowired
	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@Mock
	private HadoopClient hadoopClientMock;

	@Before
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock, hadoopClientMock);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

		when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
	}

	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {

		when(hadoopClientMock.pushData(anyString())).thenReturn(true);
		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
	}

	@Test
	public void testGetShouldWorkAsExpected() throws Exception{
		BlockTypeEnum typeA = BlockTypeEnum.BLOCKTYPEA;
		when(serverMock.findDataEnvelopeByBlocktype(eq(typeA))).thenReturn(Arrays.asList(testDataEnvelope));
		mockMvc.perform(get(URI_GETDATA.expand(typeA))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.dataEnvelopeList.[0].dataHeader.name").value("Test"));

	}

	@Test
	public void testPatchCallWorksAsExpected()throws Exception{
		String name = "TEST_NAME";
		BlockTypeEnum blockTypeEnum = BlockTypeEnum.BLOCKTYPEA;
		mockMvc.perform(patch(URI_PATCHDATA.expand(name, blockTypeEnum))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNoContent())
				.andReturn();

	}

	//@Test
	public void testPatchExceptionWorksAsExpected() throws Exception{
		BlockTypeEnum blockTypeEnum = BlockTypeEnum.BLOCKTYPEA;
		String longName = "abcdefghijklmnopqrstuvwxyz";
		mockMvc.perform(patch(URI_PATCHDATA.expand(longName, blockTypeEnum))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isConflict())
				.andReturn();
	}

}
