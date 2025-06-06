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

import java.security.Permission;
import java.util.List;

import org.esupportail.publisher.domain.AbstractPermission;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author GIP RECIA - Julien Gribonvald 4 juil. 2014
 */
public interface PermissionRepository<T extends AbstractPermission> extends
		AbstractRepository<T, Long> {

	final static String CacheName = "PermissionCache";

	@Override
	// @Cacheable(value = CacheName, unless = "#result == null")
	List<T> findAll();

	@Override
	// @Cacheable(value = CacheName, unless = "#result == null")
	Iterable<T> findAll(Predicate predicate);

}
