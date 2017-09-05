/*
 * Copyright (c) 2014-2017 Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globo.ateam.taurina.controlles;

import com.globo.ateam.taurina.model.Scenario;
import com.globo.ateam.taurina.services.FilesService;
import com.globo.ateam.taurina.services.QueueExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/test")
public class TestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final FilesService filesService;
    private final QueueExecutorService queueExecutorService;

    @Autowired
    public TestController(FilesService filesService, QueueExecutorService queueExecutorService) {
        this.filesService = filesService;
        this.queueExecutorService = queueExecutorService;
    }

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<?> create(@RequestBody Scenario scenario, HttpServletRequest request) throws IOException {
        long id = filesService.nextId();
        queueExecutorService.put(id, scenario);
        final URI locationURI = URI.create(request.getRequestURL().toString().replaceAll("/$", "") + "/" + id);
        return ResponseEntity.created(locationURI).build();
    }

}
