/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.web.rest;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.esupportail.publisher.domain.enums.AccessType;
import org.esupportail.publisher.domain.enums.ClassificationDecorType;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.PermissionClass;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.enums.WritingFormat;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Evaluator.
 */
@RestController
@RequestMapping("/api/enums")
public class EnumsResource {

	private final Logger log = LoggerFactory.getLogger(EnumsResource.class);

    /**
     * GET  /access -> get all the AccessType.
     */
    @RequestMapping(value = "/accesstype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccessType>> getAllAccessType() {
        return new ResponseEntity<List<AccessType>>(Arrays.asList(AccessType.values()), HttpStatus.OK);
    }

    /**
     * GET  /access -> get all the AccessType.
     */
    @RequestMapping(value = "/contexttype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContextType>> getAllContextType() {
        return new ResponseEntity<List<ContextType>>(Arrays.asList(ContextType.values()), HttpStatus.OK);
    }

    /**
     * GET  /classifdecorationtype -> get all the DecorationType.
     */
    @RequestMapping(value = "/classifdecorationtype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClassificationDecorType>> getAllClassifDecorType() {
        return new ResponseEntity<List<ClassificationDecorType>>(Arrays.asList(ClassificationDecorType.values()), HttpStatus.OK);
    }

    /**
     * GET  /displayorder -> get all the DisplayOrderType.
     */
    @RequestMapping(value = "/displayordertype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DisplayOrderType>> getAllDisplayOrder() {
        return new ResponseEntity<List<DisplayOrderType>>(Arrays.asList(DisplayOrderType.values()), HttpStatus.OK);
    }


    /**
     * GET  /filter -> get all the FilterType.
     */
    @RequestMapping(value = "/filtertype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilterType>> getAllFilterType() {
        return new ResponseEntity<List<FilterType>>(Arrays.asList(FilterType.values()), HttpStatus.OK);
    }

    /**
     * GET  /itemstatus -> get all the ItemStatus.
     */
    @RequestMapping(value = "/itemstatus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemStatus>> getAllItemStatus() {
        return new ResponseEntity<List<ItemStatus>>(Arrays.asList(ItemStatus.values()), HttpStatus.OK);
    }

    /**
     * GET  /itemtype -> get all the ItemStatus.
     */
    @RequestMapping(value = "/itemtype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemType>> getAllItemType() {
        return new ResponseEntity<List<ItemType>>(Arrays.asList(ItemType.values()), HttpStatus.OK);
    }

    /**
     * GET  /operator -> get all the OperatorType.
     */
    @RequestMapping(value = "/operatortype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OperatorType>> getAllOperatorType() {
        return new ResponseEntity<List<OperatorType>>(Arrays.asList(OperatorType.values()), HttpStatus.OK);
    }

    /**
     * GET  /permissionclass -> get all the PermissionClass.
     */
    @RequestMapping(value = "/permissionclass",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PermissionClass>> getAllPermissionClass() {
        // to limit to managed way
        // return new ResponseEntity<List<PermissionClass>>(Arrays.asList(PermissionClass.values()), HttpStatus.OK);
        return new ResponseEntity<List<PermissionClass>>(Lists.newArrayList(PermissionClass.CONTEXT, PermissionClass.CONTEXT_WITH_SUBJECTS), HttpStatus.OK);
    }

    /**
     * GET  /permission -> get all the PermissionType.
     */
    @RequestMapping(value = "/permissiontype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PermissionType>> getAllPermissionType() {
        return new ResponseEntity<List<PermissionType>>(Arrays.asList(PermissionType.values()), HttpStatus.OK);
    }

    /**
     * GET  /stringevaluationmode -> get all the StringEvaluationMode.
     */
    @RequestMapping(value = "/stringevaluationmode",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StringEvaluationMode>> getAllStringEvaluationMode() {
        return new ResponseEntity<List<StringEvaluationMode>>(Arrays.asList(StringEvaluationMode.values()), HttpStatus.OK);
    }

    /**
     * GET  /subject -> get all the SubjectType.
     */
    @RequestMapping(value = "/subjecttype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubjectType>> getAllSubjectType() {
        return new ResponseEntity<List<SubjectType>>(Arrays.asList(SubjectType.values()), HttpStatus.OK);
    }

    /**
     * GET  /subscribe -> get all the SubscribeType.
     */
    @RequestMapping(value = "/subscribetype",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubscribeType>> getAllSubscribeType() {
        return new ResponseEntity<List<SubscribeType>>(Arrays.asList(SubscribeType.values()), HttpStatus.OK);
    }

    /**
     * GET  /writingmode -> get all the WritingMode.
     */
    @RequestMapping(value = "/writingmode",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WritingMode>> getAllWritingMode() {
        return new ResponseEntity<List<WritingMode>>(Arrays.asList(WritingMode.values()), HttpStatus.OK);
    }
    /**
     * GET  /writingformat -> get all the WritingFormat.
     */
    @RequestMapping(value = "/writingformat",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WritingFormat>> getAllWritingFormat() {
        return new ResponseEntity<List<WritingFormat>>(Arrays.asList(WritingFormat.values()), HttpStatus.OK);
    }

}
