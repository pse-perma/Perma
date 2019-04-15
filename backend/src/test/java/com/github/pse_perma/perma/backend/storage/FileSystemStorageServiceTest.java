package com.github.pse_perma.perma.backend.storage;

import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileSystemStorageServiceTest {

	private FileSystemStorageService fileService = new FileSystemStorageService(new StorageProperties());

	@Test
	public void loadAsResource() {
		Resource expected;
		try {
			expected =
					new UrlResource("file:" + new File("").getAbsolutePath() + "/upload-dir/va_capability_example" +
							".jar");
		} catch (MalformedURLException mue) {
			fail();
			return;
		}

		assertEquals(expected, fileService.loadAsResource("va_capability_example.jar"));
	}

	@Test(expected = StorageFileNotFoundException.class)
	public void loadAsResourceWithEx() {
		fileService.loadAsResource("malformed test file");
	}
}
