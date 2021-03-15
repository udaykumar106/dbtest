package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.api.model.DataEnvelope;
import com.db.dataplatform.techtest.api.model.ResponseDataEnvelope;
import com.db.dataplatform.techtest.server.component.HadoopClient;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;
    private  final HadoopClient hadoopClient;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);
        if(!checksumPass){
            return ResponseEntity.ok(checksumPass);
        }

        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        hadoopClient.pushData(dataEnvelope.getDataBody().getDataBody());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value="/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDataEnvelope> getDataEnvelopesForBlockType(@PathVariable("blockType")BlockTypeEnum blockType){
        List<DataEnvelope> dataEnvelopeByBlocktype = server.findDataEnvelopeByBlocktype(blockType);
        ResponseDataEnvelope responseDataEnvelope = new ResponseDataEnvelope(dataEnvelopeByBlocktype);
        return ResponseEntity.ok(responseDataEnvelope);
    }

    @PatchMapping(value="/update/{name}/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateBlockType(@Size(min = 2) @PathVariable("name") String name, @PathVariable("blockType") BlockTypeEnum blockType){
        Boolean response = server.updateBlocktypeByName(name.toString(), blockType);
        return ResponseEntity.noContent().build();
    }

}
