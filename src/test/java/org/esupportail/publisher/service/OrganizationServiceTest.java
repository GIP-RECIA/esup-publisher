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
package org.esupportail.publisher.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    private static final long ORGANIZATION_ID = 1L;

    @InjectMocks
    private OrganizationService organizationService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Test
    public void doMoveToLowerPositionUpdatesRangeAndSavesOrganization() {
        Organization organization = new Organization();
        when(organizationRepository.getDisplayOrderOf(ORGANIZATION_ID)).thenReturn(2);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));

        organizationService.doMove(ORGANIZATION_ID, 5);

        verify(organizationRepository).setLowerDisplayOrderOfRange(2, 5);
        verify(organizationRepository).findById(ORGANIZATION_ID);
        verify(organizationRepository).save(organization);
        assertThat(organization.getDisplayOrder(), equalTo(5));
    }

    @Test
    public void doMoveToUpperPositionUpdatesRangeAndSavesOrganization() {
        Organization organization = new Organization();
        when(organizationRepository.getDisplayOrderOf(ORGANIZATION_ID)).thenReturn(5);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));

        organizationService.doMove(ORGANIZATION_ID, 2);

        verify(organizationRepository).setUpperDisplayOrderOfRange(2, 5);
        verify(organizationRepository).findById(ORGANIZATION_ID);
        verify(organizationRepository).save(organization);
        assertThat(organization.getDisplayOrder(), equalTo(2));
    }

    @Test
    public void doMoveToCurrentPositionDoesNotSaveOrganization() {
        when(organizationRepository.getDisplayOrderOf(ORGANIZATION_ID)).thenReturn(2);

        organizationService.doMove(ORGANIZATION_ID, 2);

        verify(organizationRepository, never()).setLowerDisplayOrderOfRange(2, 2);
        verify(organizationRepository, never()).setUpperDisplayOrderOfRange(2, 2);
        verify(organizationRepository, never()).findById(ORGANIZATION_ID);
        verify(organizationRepository, never()).save(any(Organization.class));
    }
}
