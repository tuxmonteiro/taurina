package com.globo.ateam.taurina.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.globo.ateam.taurina.util.Result;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

@Service
public class JmeterService {

    private final FilesService filesService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public JmeterService(FilesService filesService) {
        this.filesService = filesService;
    }

    public Result start(long testId) throws IOException {
        log.info("Starting jmeter id " + testId);
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        JMeterUtils.setJMeterHome(filesService.jmeterHome());
        JMeterUtils.loadJMeterProperties(filesService.props());
        SaveService.loadProperties();
        Path testPlanPath = Paths.get(filesService.tmpDir() + "/" + Long.toString(testId) + "/test.jmx");
        if (Files.exists(testPlanPath)) {
            File testPlanFile = new File(testPlanPath.toString());
            
            XStream xStream = new XStream();
            ArrayMapper hashTree = (ArrayMapper) xStream.fromXML(testPlanFile);

            log.info(hashTree.toString());

//
//            ObjectMapper mapper = new ObjectMapper(new XmlFactory());
//            ObjectNode node = (ObjectNode) mapper.readTree(testPlanFile);
//
//            log.info(node.get("hashTree").get("hashTree").get("ThreadGroup").toString());
//            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
//            while (it.hasNext()) {
//                Map.Entry<String, JsonNode> next = it.next();
//                log.info(next.getKey() + " >> " + next.getValue());
//            }


            HashTree testPlan = SaveService.loadTree(testPlanFile);
            log.info(testPlan.toString());
            jmeter.configure(testPlan);
            jmeter.run();
            log.info("Finished jmeter id " + testId);
            byte[] result;
            try {
                result = Files.readAllBytes(Paths.get(filesService.pathResultFile(testId)));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                result = new byte[0];
            }
            return new Result(testId, result);
        }
        log.error("Aborting jmeter id " + jmeter + ". Test Plan Failed");
        return new Result(-1L, new byte[0]);
    }

}
