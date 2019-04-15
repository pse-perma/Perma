package com.github.pse_perma.perma.backend.controller;

import com.github.pse_perma.perma.backend.storage.StorageFileNotFoundException;
import com.github.pse_perma.perma.backend.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api/files"})
public class FileUploadController {

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping
	public Set<CapabilityFile> listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files",
				storageService.loadAll().map(path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class
						, "serveFile", path.getFileName().toString()).build().toString()).collect(Collectors.toList()));

		ArrayList urls = (ArrayList) model.asMap().values().toArray()[0];
		Set<CapabilityFile> files = new HashSet<>();

		for (Object value : urls) {
			String[] parts = value.toString().split("/");
			String name = parts[parts.length - 1];
			files.add(new CapabilityFile(name, value.toString()));
		}

		return files;
	}

	@GetMapping(path = {"/{filename:.+}"})
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() +
				"!");

		return "redirect:/";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
