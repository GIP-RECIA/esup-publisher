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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.esupportail.publisher.service.bean.FileUploadHelper;
import org.esupportail.publisher.service.exceptions.UnsupportedMimeTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.mock.web.MockMultipartFile;

public class FileServiceTest {

    private final FileService fileService = new FileService();

	@Test
	public void deleteInternalResource_UrlPathIsNull_ReturnFalse() {
		String urlPath = null;
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertThat(result, is(false));
	}

	@Test
	public void deleteInternalResource_UrlPathNotStartWithHtpp_ReturnFalse() {
		String urlPath = "http://test.com";
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertThat(result, is(false));
	}

	@Test
	public void deleteInternalResource_UrlPathNotStartWithHtpps_ReturnFalse() {
		String urlPath = "https://test.com";
		final boolean result = fileService.deleteInternalResource(urlPath);
		assertThat(result, is(false));
	}

	@Test
	public void uploadInternalResource_AuthorizedFile_PersistsFileAndReturnsUrl(@TempDir final Path uploadDirectory) throws Exception {
		//GIVEN
		configurePublicFileUploadHelper(uploadDirectory, 1024L, Collections.singleton("text/plain"));
		final MockMultipartFile file = new MockMultipartFile("file", "notice.txt", "text/plain", "content".getBytes(StandardCharsets.UTF_8));

		//WHEN
		final String url = fileService.uploadInternalResource(42L, "notice", file);

		//THEN
		assertThat(url, containsString("/files/" + String.valueOf(42L).hashCode() + "/"));
		final Path uploadedFile = uploadDirectory.resolve(url.substring("/files/".length()));
		assertThat(Files.exists(uploadedFile), is(true));
		assertThat(new String(Files.readAllBytes(uploadedFile), StandardCharsets.UTF_8), equalTo("content"));
	}

	@Test
	public void uploadInternalResource_FileExceedsConfiguredSize_ThrowsMaxUploadSizeExceededException(@TempDir final Path uploadDirectory) {
		//GIVEN
		configurePublicFileUploadHelper(uploadDirectory, 3L, Collections.singleton("text/plain"));
		final MockMultipartFile file = new MockMultipartFile("file", "notice.txt", "text/plain", "content".getBytes(StandardCharsets.UTF_8));

		//WHEN / THEN
		org.junit.jupiter.api.Assertions.assertThrows(MaxUploadSizeExceededException.class,
				() -> fileService.uploadInternalResource(42L, "notice", file));
	}

	@Test
	public void uploadInternalResource_UnauthorizedMimeType_ThrowsUnsupportedMimeTypeException(@TempDir final Path uploadDirectory) {
		//GIVEN
		configurePublicFileUploadHelper(uploadDirectory, 1024L, Collections.singleton("image/png"));
		final MockMultipartFile file = new MockMultipartFile("file", "notice.txt", "text/plain", "content".getBytes(StandardCharsets.UTF_8));

		//WHEN / THEN
		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedMimeTypeException.class,
				() -> fileService.uploadInternalResource(42L, "notice", file));
	}

	@Test
	public void deleteInternalResource_ExistingFile_DeletesFile(@TempDir final Path uploadDirectory) throws IOException {
		//GIVEN
		configurePublicFileUploadHelper(uploadDirectory, 1024L, Collections.singleton("text/plain"));
		final Path file = uploadDirectory.resolve("notice.txt");
		Files.write(file, "content".getBytes(StandardCharsets.UTF_8));

		//WHEN
		final boolean result = fileService.deleteInternalResource("/files/notice.txt");

		//THEN
		assertThat(result, is(true));
		assertThat(Files.exists(file), is(false));
	}

	private void configurePublicFileUploadHelper(final Path uploadDirectory, final long maxSize, final java.util.Set<String> authorizedMimeTypes) {
		final FileUploadHelper fileUploadHelper = new FileUploadHelper(uploadDirectory.toString() + "/", null, "/files/",
				Collections.emptySet(), false, maxSize, authorizedMimeTypes);
		ReflectionTestUtils.setField(fileService, "publicFileUploadHelper", fileUploadHelper);
	}
}
