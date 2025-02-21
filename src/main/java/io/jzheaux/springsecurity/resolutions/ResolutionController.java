package io.jzheaux.springsecurity.resolutions;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;
	private final  UserService users;

	public ResolutionController(ResolutionRepository resolutions, UserService users) {
		this.resolutions = resolutions;
		 this.users = users;
	}
	
	@CrossOrigin
	@GetMapping("/resolutions")
	@PreAuthorize("hasAuthority('resolution:read')")
	@PostFilter("@post.filter(#root)")
	public Iterable<Resolution> read() {
	    Iterable<Resolution> resolutions = this.resolutions.findAll();
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("user:read"))) {
	    for (Resolution resolution : resolutions) {
	    	String name = this.users.getFullName(resolution.getOwner())
	    			.orElse("Anonymous");
	        resolution.setText(resolution.getText() + ", by " + name);
	    }
	    }
	    return resolutions;
	}

	@GetMapping("/resolution/{id}")
	@PreAuthorize("hasAuthority('resolution:read')")
	@PostAuthorize("@post.authorize(#root)")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	@PreAuthorize("hasAuthority('resolution:write')")
	public Resolution make(@CurrentUsername String owner,@RequestBody String text) {		
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@Transactional
	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.authorize(#root)")
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@Transactional
	@PreAuthorize("hasAuthority('resolution:write')")
	@PostAuthorize("@post.authorize(#root)")
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
	
	@PreAuthorize("hasAuthority('resolution:share')")
	@PostAuthorize("@post.authorize(#root)")
	@PutMapping("/resolution/{id}/share")
	@Transactional
	public Optional<Resolution> share(@AuthenticationPrincipal User user,@PathVariable("id") UUID id) {
		
		 Optional<Resolution> resolution = read(id);
		    resolution
		            .filter(r -> r.getOwner().equals(user.getUsername()))
		            .map(Resolution::getText).ifPresent(text -> {
		                    for (User friend : user.getFriends()) {
		                        make(friend.getUsername(), text);
		                    }
		            });
		    return resolution;
	}
}
