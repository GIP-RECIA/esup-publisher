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
package org.esupportail.publisher.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.predicates.ReaderPredicates;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald 25 sept. 2014
 */
@ExtendWith(SpringExtension.class)//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class ReaderRepositoryTest {

	public static final String READER_NAME = "TEST UPDATE";
	@Inject
	private ReaderRepository repository;

	@BeforeAll
	public void setUp() throws Exception {
		log.info("starting up {}", this.getClass().getName());
		Reader r = ObjTest.newReader("1");
		repository.saveAndFlush(r);

		Reader r2 = ObjTest.newReader("2");
		repository.saveAndFlush(r2);
	}

	@Test
	public void testUnique() {
		Reader r2 = ObjTest.newReader("1");
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			repository.saveAndFlush(r2);
		});
	}

	@Test
	public void testInsert() {
		Reader r = ObjTest.newReader("3");
		log.info("Before insert : {}", r);
		repository.saveAndFlush(r);
		assertThat(r.getId(), notNullValue());
		log.info("After insert : {}", r);
		Optional<Reader> optionalReader = repository.findById(r.getId());
		Reader r2 = optionalReader == null || !optionalReader.isPresent()? null : optionalReader.get();
		log.info("After select : {}", r2);
		assertThat(r2, notNullValue());
		assertThat(r2, equalTo(r));

		r2.setName(READER_NAME);
		r2 = repository.saveAndFlush(r2);
		log.info("After update : {}", r2);
		assertThat(r2.getName(), equalTo(READER_NAME));

		Reader r4 = ObjTest.newReader("4");
		log.info("Before insert : {}", r4);
		repository.save(r4);
		log.info("After insert : {}", r4);
		assertThat(r4.getId(), notNullValue());
		assertThat(repository.existsById(r4.getId()), is(true));
		r = repository.getById(r.getId());
		assertThat(r, notNullValue());
		log.info("After select : {}", r);
		assertThat(repository.count(), equalTo(4L));

		List<Reader> results = repository.findAll();
		assertThat(results.size(), is(4));
		assertThat(results, hasItem(r));

		r2 = repository.findByName(r.getName());
		assertThat(r2, notNullValue());

		results = Lists.newArrayList(repository.findAll(ReaderPredicates
				.sameName(r2)));
		assertThat(results, is(empty()));

		repository.deleteById(r.getId());
		assertThat(repository.findAll().size(), equalTo(3));
		assertThat(repository.existsById(r.getId()), is(false));

		Optional<Reader> optionalR = repository.findById((long) 0);
		r = optionalR == null || !optionalR.isPresent()? null : optionalR.get();
		assertThat(r, is(nullValue()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(2));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		Reader e = ObjTest.newReader("init3");
		Reader e2 = ObjTest.newReader("init4");
		repository.saveAll(Arrays.asList(e, e2));
		assertThat(repository.findAll().size(), is(4));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#existsById(Object)}
	 * .
	 */
	@Test
	public void testExists() {
		assertThat(repository.existsById(repository.findAll().get(0).getId()), is(true));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertThat(repository.count(), equalTo(2L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		repository.delete(repository.findAll().get(0));
		assertThat(repository.count(), equalTo(1L));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	public void testDeleteAll() {
		repository.deleteAll();
		assertThat(repository.count(), equalTo(0L));
	}

}